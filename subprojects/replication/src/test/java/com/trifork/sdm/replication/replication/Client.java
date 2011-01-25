package com.trifork.sdm.replication.replication;


import static java.lang.String.format;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.trifork.sdm.replication.ClientException;
import com.trifork.sdm.replication.GatewayConnection;
import com.trifork.sdm.replication.gateway.GatewayResponse;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.CareProvider;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.SecurityTokenRequest;
import dk.sosi.seal.model.SecurityTokenResponse;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.vault.CredentialVault;


public class Client
{

	private final String systemName;
	private final String cvr;

	private IDCard idCard = null;

	private final String keyStorePassword;
	private final URL gatewayURL;

	private SOSIFactory factory;


	// TODO: Make the client's key store configurable (file path).
	// private final File keyStore;

	// TODO: Document the parameters.
	public Client(String systemName, String cvr, URL gatewayURL, File keyStore, String keystorePassword) throws Exception
	{
		this.systemName = systemName;
		this.cvr = cvr;
		this.gatewayURL = gatewayURL;
		// this.keyStore = keyStore;
		this.keyStorePassword = keystorePassword;
	}


	/**
	 * Fetches an URL for an
	 * 
	 * This method should only be used if you have not previously replicated any data from the
	 * specified resource. You will get data since
	 * 
	 * This will return an URL that is valid for a short period of time. If you do not use the URL
	 * before it expires you will have to fetch a new URL, which can be slow down the replication
	 * process.
	 * 
	 * @param resourceURI The URI of the resource you want to fetch.
	 * @param version The version of the resource you want to fetch.
	 * @return
	 * @throws Exception
	 */
	public URL fetchURL(String resourceURI, int version) throws Exception
	{

		return fetchURL(resourceURI, null, version);
	}


	public URL fetchURL(String resourceURL, String offset, int version) throws Exception
	{

		// Lazy-load the needed components.

		if (idCard == null || !idCard.isValidInTime())
		{

			idCard = fetchNewIDCard();
		}

		GatewayConnection gatewayConnection = new GatewayConnection(idCard, factory, gatewayURL);

		String responseXml = gatewayConnection.request(resourceURL, version);

		// Read the response.

		Reply response = factory.deserializeReply(responseXml);

		// Check for errors.

		if (response.isFault())
		{

			throw new ClientException(format("Gateway Error: %s %s", response.getFaultCode(), response.getFaultString()), response.getFaultCode(), response.getFaultString());
		}

		// Read the URL from the gateway response.

		GatewayResponse gatewayResponse = GatewayResponse.deserialize(response.getBody());

		// Now we have the URL, use it.

		return gatewayResponse.buildUrl();
	}


	public InputStream replicate(URL resourceURL) throws Exception
	{
		return sendREST(resourceURL);
	}


	/*
	 * Fetches an updated id card.
	 * 
	 * The id card is valid for 24 hours starting from the time it was requested minus 5 minutes.
	 * 
	 * This method does not have to be called manually. If it is has not been called when a
	 * replication is started, or the previously fetched id card is no longer valid, the client
	 * automatically fetches an updated id card. This allows you to update the id card when it fits
	 * your program's flow, network and memory usage.
	 */
	// TODO: This method should throw some kind of special exception.
	protected IDCard fetchNewIDCard() throws Exception
	{
		// Determine which crypto provider to use.

		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.

		// CredentialVault vault = new
		// FileBasedCredentialVault(cryptoProviderSettings, keyStore,
		// keyStorePassword);
		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, keyStorePassword);

		// Create a SEAL ID card.

		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		factory = new SOSIFactory(federation, vault, cryptoProviderSettings);

		IDCard card = factory.createNewSystemIDCard(systemName, new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, cvr, "dk"), AuthenticationLevel.VOCES_TRUSTED_SYSTEM, null, // Username
		null, // Password
		factory.getCredentialVault().getSystemCredentialPair().getCertificate(), null // Alternative
																						// ID
				);

		STSConnection stsConnection = new STSConnection(card, factory);
		String responseXml = stsConnection.request();
		SecurityTokenRequest request = factory.createNewSecurityTokenRequest();
		request.setIDCard(card);

		// Read the response.

		SecurityTokenResponse response = factory.deserializeSecurityTokenResponse(responseXml);

		// Check for errors.

		if (response.isFault())
		{

			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		return response.getIDCard();
	}


	// TODO: Don't just throw a plain Exception.
	private static InputStream sendREST(URL resourceURL) throws Exception
	{

		HttpURLConnection connection = (HttpURLConnection) resourceURL.openConnection();

		connection.connect();

		InputStream inputStream;

		if (connection.getResponseCode() < 400)
		{
			inputStream = connection.getInputStream();
		}
		else
		{
			inputStream = connection.getErrorStream();
		}

		return inputStream;
	}
}
