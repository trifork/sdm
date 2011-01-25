package com.trifork.sdm.replication.gateway;


import java.io.IOException;

import org.junit.Test;


public class GatewayBuildUrlTest
{
	private static final String USERNAME_PARAM = "username=([^&]+)";
	private static final String EXPIRES_PARAM = "expires=(\\d+)";
	private static final String GATEWAY_USERNAME = "gateway";
	private static final String SIGNATURE_PARAM = "signature=([^&]+)";


	@Test
	public void can_generate_valid_url() throws Exception
	{

	}


	@Test
	public void should_return_an_expires_date_in_the_future() throws Exception
	{

	}


	@Test
	public void should_return_a_username() throws Exception
	{

	}


	@Test
	public void should_return_a_signature_that_matches_a_request_with_no_parameters() throws Exception
	{

	}
}
