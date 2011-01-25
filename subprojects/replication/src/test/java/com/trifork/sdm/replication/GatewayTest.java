package com.trifork.sdm.replication;


import org.junit.Test;

import com.trifork.sdm.replication.settings.Host;
import com.trifork.sdm.replication.settings.Port;


public class GatewayTest
{

	private static final String KEY_STORE_PASSWORD = "Test1234";
	private static final String KEY_STORE_PATH = "/SealKeystore.jks";

	private static final String IT_SYSTEM_NAME = "SOSITEST";

	// This is the CVR associated with the test certificate.
	private static final String TEST_CVR = "30808460";


	@Test
	public void should_require_a_resource_parameter() throws Exception
	{

	}


	@Test
	public void should_start_replicating_when_soap_request_is_valid() throws Exception
	{

	}
}
