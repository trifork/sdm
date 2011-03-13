package com.trifork.sdm.replication.dgws;


import static com.trifork.sdm.replication.dgws.SOSITestConstants.*;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.*;
import static java.lang.String.*;

import java.util.Properties;

import com.google.inject.*;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.*;
import dk.sosi.seal.pki.*;
import dk.sosi.seal.vault.*;


public class TestSTSModule extends AbstractModule
{
	@Override
	protected void configure()
	{

	}


	@Provides
	@Singleton
	public SOSIFactory provideSOSIFactory()
	{
		// Determine which encryption provider to use.

		Properties cryptoProviderSettings = SignatureUtil.setupCryptoProviderForJVM();

		// Open the key store using the path and password.

		CredentialVault vault = new ClasspathCredentialVault(cryptoProviderSettings, KEY_STORE_PASSWORD);

		Federation federation = new SOSITestFederation(cryptoProviderSettings);
		return new SOSIFactory(federation, vault, cryptoProviderSettings);
	}


	/*
	 * Fetches an updated id card.
	 * 
	 * The id card is valid for 24 hours starting from the time it was requested minus 5 minutes.
	 */
	@Provides
	protected IDCard provideTestIDCard(SOSIFactory factory) throws Exception
	{
		// Create a SEAL ID card.

		// @formatter:off
		IDCard unsignedCard = factory.createNewSystemIDCard(TEST_IT_SYSTEM_NAME, new CareProvider(CVR_NUMBER, TEST_CVR, "dk"), AuthenticationLevel.VOCES_TRUSTED_SYSTEM, null, // Username
		null, // Password
		factory.getCredentialVault().getSystemCredentialPair().getCertificate(), null // Alternative
				);
		// @formatter:on

		SecurityTokenRequest stsRequest = factory.createNewSecurityTokenRequest();
		stsRequest.setIDCard(unsignedCard);

		// Send the request.

		String responseXML = SoapHelper.send(TEST_STS_URL, stsRequest.serialize2DOMDocument());
		SecurityTokenResponse response = factory.deserializeSecurityTokenResponse(responseXML);

		// Check for errors.

		if (response.isFault())
		{
			throw new Exception(format("STS Error: %s %s", response.getFaultCode(), response.getFaultString()));
		}

		// If all has gone well,
		// we can store the ID Card for later use.

		return response.getIDCard();
	}
}
