package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.trifork.stamdata.replication.security.dgws.AuthorizationDao;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SslSecurityManagerTest {
	@Mock X509Certificate certificate;
	@Mock HttpServletRequest request;
	@Mock AuthorizationDao dao;
	@Mock OcesHelper ocesHelper;
	@Mock MocesCertificateWrapper certificateWrapper;
	SslSecurityManager securityManager;
	X509Certificate[] certificateList;
	String viewPath = "/foo/bar/v1";
	String cvr = "12345678";

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
	public void acceptsClientWithKnownCvr() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(true);
		when(certificateWrapper.getCvr()).thenReturn(cvr);
		when(dao.isClientAuthorized(cvr, "foo/bar/v1")).thenReturn(true);

		assertTrue(securityManager.isAuthorized(request));
	}

	@Test
	public void rejectsClientWithUnknownCvr() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.isValid()).thenReturn(true);
		when(certificateWrapper.getCvr()).thenReturn("12345678");
		when(dao.isClientAuthorized(cvr, "foo/bar/v1")).thenReturn(false);

		assertFalse(securityManager.isAuthorized(request));
	}
	
	@Test
	public void usesSubjectSerialNumberAsClientId() {
		when(ocesHelper.extractCertificateFromRequest(request)).thenReturn(certificateWrapper);
		when(certificateWrapper.getSubjectSerialNumber()).thenReturn("CVR:12345678-RID:someRid");
		
		assertEquals("CVR:12345678-RID:someRid", securityManager.getClientId(request));
	}
}