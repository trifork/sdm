package com.trifork.sdm.replication.gateway;


import org.junit.Before;
import org.junit.Test;

import com.trifork.sdm.replication.admin.models.*;

import dk.sosi.seal.model.Request;


public class GatewayAuthorizeToResourceTest
{
	private ClientRepository clientRepository;
	private PermissionRepository permissionRepository;
	private LogEntryRepository logEntryRepository;
	private Request request;
	private String resource;


	@Before
	public void setup() throws Exception
	{

	}


	@Test
	public void can_authorize_client_against_requested_resource() throws Exception
	{

	}


	@Test
	public void not_authorized_when_no_client_created() throws Exception
	{

	}


	@Test
	public void not_authorized_when_no_permission_setup() throws Exception
	{

	}
}
