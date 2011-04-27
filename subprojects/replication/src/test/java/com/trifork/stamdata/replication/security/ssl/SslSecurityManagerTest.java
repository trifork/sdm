package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SslSecurityManagerTest {
	@Mock X509Certificate certificate;
	@Mock HttpServletRequest request;
	SslSecurityManager securityManager;

	@Before
	public void before() {
		when(request.getAttribute("javax.servlet.request.X509Certificate")).thenReturn(new X509Certificate[] {certificate});
		securityManager = new SslSecurityManager();
	}

	@Test
	public void rejectsClientWithUnknownCvr() {

	}

	@Test
	public void acceptsClientWithKnownCvr() {

	}
}
