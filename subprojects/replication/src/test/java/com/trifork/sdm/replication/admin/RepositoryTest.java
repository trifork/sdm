package com.trifork.sdm.replication.admin;

import org.junit.Before;
import org.junit.BeforeClass;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.admin.models.AuditLogRepository;
import com.trifork.sdm.replication.admin.models.ClientRepository;
import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.sdm.replication.admin.models.UserRepository;

public abstract class RepositoryTest {
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
	public void setUp() {
		permissionRepository = injector.getInstance(PermissionRepository.class);
		clientRepository = injector.getInstance(ClientRepository.class);
		userRepository = injector.getInstance(UserRepository.class);
		auditLogRepository = injector.getInstance(AuditLogRepository.class);
	}
}
