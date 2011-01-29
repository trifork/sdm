package com.trifork.sdm.replication.admin;

import org.junit.*;

import com.google.inject.*;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.*;


public abstract class RepositoryTest
{
	protected PermissionRepository permissionRepository;
	protected ClientRepository clientRepository;
	protected UserRepository userRepository;
	protected AuditLogRepository auditLogRepository;
	private static Injector injector;


	@BeforeClass
	public static void init()
	{
		injector = Guice.createInjector(new ProductionModule());
	}


	@Before
	public void setUp()
	{
		permissionRepository = injector.getInstance(PermissionRepository.class);
		clientRepository = injector.getInstance(ClientRepository.class);
		userRepository = injector.getInstance(UserRepository.class);
		auditLogRepository = injector.getInstance(AuditLogRepository.class);
	}
}
