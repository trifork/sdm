package com.trifork.sdm.replication.client;

import static java.lang.String.*;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.model.constants.SubjectIdentifierTypeValues;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.ClasspathCredentialVault;
import dk.sosi.seal.vault.CredentialVault;


public class STSConnection extends SOAPConnection {

	public static final String TEST_STS_URL = "http://pan.certifikat.dk/sts/services/SecurityTokenService";

	private IDCard signedCard = null;
	private SOSIFactory factory;

	private String systemName;
	private String cvr;


	public STSConnection(URL stsURL, String systemName, String cvr, File keyStore, String keystorePassword) {

		// Determine which crypto provider to use.

		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.

		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, keystorePassword);

		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		factory = new SOSIFactory(federation, vault, cryptoProviderSettings);

		this.systemName = systemName;
		this.cvr = cvr;
	}


	/*
	 * Fetches an updated id card.
	 * 
	 * The id card is valid for 24 hours starting from the time it was requested
	 * minus 5 minutes.
	 * 
	 * This method does not have to be called manually. If it is has not been
	 * called when a replication is started, or the previously fetched id card
	 * is no longer valid, the client automatically fetches an updated id card.
	 * This allows you to update the id card when it fits your program's flow,
	 * network and memory usage.
	 */
	// TODO: This method should throw some kind of special exception.
	IDCard getIDCard() throws Exception {

		if (signedCard != null && signedCard.isValidInTime()) {

			return signedCard;
		}

		// Create a SEAL ID card.

		IDCard unsignedCard = factory.createNewSystemIDCard(
			systemName,
			new CareProvider(SubjectIdentifierTypeValues.CVR_NUMBER, cvr, "dk"),
			AuthenticationLevel.VOCES_TRUSTED_SYSTEM,
			null, // Username
			null, // Password
			factory.getCredentialVault().getSystemCredentialPair().getCertificate(),
			null // Alternative ID
		);

		SecurityTokenRequest stsRequest = factory.createNewSecurityTokenRequest();
		stsRequest.setIDCard(unsignedCard);

		// Send the request.

		// TODO: Move the STS URL to a parameter.

		String responseXml = sendSOAP(new URL(TEST_STS_URL), stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = factory.deserializeSecurityTokenResponse(responseXml);

		// Check for errors.

		if (response.isFault()) {
			// TODO: Throw other exception.
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		this.signedCard = response.getIDCard();

		return this.signedCard;
	}
}
