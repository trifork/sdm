package com.trifork.stamdata.replication.security.ssl;

import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;

import com.trifork.stamdata.replication.security.SecurityManager;

public class SslSecurityManager implements SecurityManager {

	@Override
	public boolean authorize(HttpServletRequest request) {
		X509Certificate[] certificateFromHeader = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

		return false;
	}

}
