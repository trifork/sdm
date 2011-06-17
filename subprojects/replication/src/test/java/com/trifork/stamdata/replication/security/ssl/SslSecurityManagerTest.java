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
import com.trifork.stamdata.ssl.MocesCertificateWrapper;
import com.trifork.stamdata.ssl.OcesHelper;
import com.trifork.stamdata.ssl.SubjectSerialNumber;
import com.trifork.stamdata.ssl.SubjectSerialNumber.Kind;

@RunWith(MockitoJUnitRunner.class)
public class SslSecurityManagerTest {
	@Mock X509Certificate certificate;
	@Mock HttpServletRequest request;
	@Mock ClientDao dao;
	@Mock OcesHelper ocesHelper;
	@Mock MocesCertificateWrapper certificateWrapper;
	@Mock Client client;
	SslSecurityManager securityManager;
	X509Certificate[] certificateList;
	String viewPath = "/foo/bar/v1";
	String viewName = "foo/bar/v1";
	SubjectSerialNumber ssn = new SubjectSerialNumber(Kind.MOCES, "12345678", "1234");

	@Before
	public void before() {
		securityManager = new SslSecurityManager(dao, ocesHelper);
		certificateList = new X509Certificate[] {certificate};
		when(request.getPathInfo()).thenReturn(viewPath);
	}

	@Test
	public void rejectsClientWhenNoCertificateIsPresent() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(null);
		assertFalse(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithInvalidCertificate() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(false);

		assertFalse(securityManager.isAuthorized(request));
	}

	@Test
	public void acceptsClientWithKnownCvrWithPermission() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(true);
		when(certificateWrapper.getSubjectSerialNumber()).thenReturn(ssn);
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(client);
		when(client.isAuthorizedFor(viewName)).thenReturn(true);

		assertTrue(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithKnownCvrWithoutPermission() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(true);
		when(certificateWrapper.getSubjectSerialNumber()).thenReturn(ssn);
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(client);
		when(client.isAuthorizedFor(viewName)).thenReturn(false);

		assertFalse(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithUnknownCvr() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(true);
		when(certificateWrapper.getSubjectSerialNumber()).thenReturn(ssn);
		when(dao.findBySubjectSerialNumber(ssn)).thenReturn(null);

		assertFalse(securityManager.isAuthorized(request));
	}
	
	@Test
	public void usesSubjectSerialNumberAsClientId() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.getSubjectSerialNumber()).thenReturn(ssn);
		
		assertEquals(ssn.toString(), securityManager.getClientId(request));
	}
}
