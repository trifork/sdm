package com.trifork.stamdata.replication.security.ssl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.ClientDao;
import com.trifork.stamdata.ssl.OcesHelper;
import com.trifork.stamdata.ssl.UncheckedProvider;

@RunWith(MockitoJUnitRunner.class)
public class SslSecurityManagerTest {
	@Mock X509Certificate certificate;
	@Mock HttpServletRequest request;
	@Mock ClientDao dao;
	@Mock OcesHelper ocesHelper;
	@Mock Client client;
	SslSecurityManager securityManager;
	X509Certificate[] certificateList;
	String viewPath = "/foo/bar/v1";
	String viewName = "foo/bar/v1";
	String ssn = "CVR:12345678-RID:1234";
	@Mock UncheckedProvider<String> ssnProvider;

	@Before
	public void before() {
		securityManager = new SslSecurityManager(dao, ssnProvider);
		certificateList = new X509Certificate[] {certificate};
		when(ssnProvider.get()).thenReturn(ssn);
		when(request.getPathInfo()).thenReturn(viewPath);
	}

	@Test
	public void rejectsClientWhenNoCertificateIsPresent() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(null);
		assertFalse(securityManager.isAuthorized(request));
	}

	@Test
	public void acceptsClientWithKnownCvrWithPermission() {
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(client);
		when(client.isAuthorizedFor(viewName)).thenReturn(true);

		assertTrue(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithKnownCvrWithoutPermission() {
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(client);
		when(client.isAuthorizedFor(viewName)).thenReturn(false);

		assertFalse(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithUnknownCvr() {
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(null);

		assertFalse(securityManager.isAuthorized(request));
	}
	
	@Test
	public void usesSubjectSerialNumberAsClientId() {
		assertEquals(ssn, securityManager.getClientId(request));
	}
}
