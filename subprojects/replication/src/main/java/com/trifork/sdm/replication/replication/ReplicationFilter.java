package com.trifork.sdm.replication.replication;

import static java.net.HttpURLConnection.*;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trifork.sdm.replication.util.SignatureFactory;
import com.trifork.stamdata.Nullable;


@Singleton
public class ReplicationFilter implements Filter {

	private static final long SECONDS = 1000l;

	private final SignatureFactory signatureFactory;


	@Inject
	ReplicationFilter(SignatureFactory signatureFactory) {

		this.signatureFactory = signatureFactory;
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		// Noop
	}


	@Override
	public void destroy() {
		
		// Noop
	}


	@Override
	public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) rawRequest;
		HttpServletResponse response = (HttpServletResponse) rawResponse;

		String type = request.getParameter(ParameterName.RESOURCE_TYPE);
		String historyId = request.getParameter(ParameterName.HISTORY_ID);
		String signature = request.getParameter(ParameterName.SIGNATURE);
		
		
		long expires;

		try {
			
			expires = Long.parseLong(request.getParameter(ParameterName.EXPIRES));
		}
		catch (NumberFormatException e) {
			
			writeResponse(response, HTTP_BAD_REQUEST, "The request had an invalid 'expires' parameter.");
			return;
		}
		
		int pageSize;

		try {
			
			pageSize = Integer.parseInt(request.getParameter(ParameterName.PAGE_SIZE));
		}
		catch (NumberFormatException e) {
			
			writeResponse(response, HTTP_BAD_REQUEST, "The request had an invalid 'pageSize' parameter.");
			return;
		}
		
		// TODO: Test what happens if we send negative pageSize/expires parameters.
		
		// Validate the parameters.

		if (expires * SECONDS < System.currentTimeMillis()) {
			
			writeResponse(response, HTTP_GONE, "The requested resource has expired.");
		}
		else if (signature == null) {
			
			writeResponse(response, HTTP_FORBIDDEN, "The request did not contain a 'signature' parameter.");
		}
		else if (type == null) {
			
			writeResponse(response, HTTP_FORBIDDEN, "The request did not contain a 'type' parameter.");
		}
		else if (checkSignature(type, expires, historyId, pageSize, signature)) {
			
			chain.doFilter(request, response);
		}
		else {
			
			writeResponse(response, HTTP_FORBIDDEN, "The request's signature did not match the query.");
			
			// TODO: Log
		}
	}


	private boolean checkSignature(String type, long expires, @Nullable String historyId, int pageSize, String actualSignature) {

		String expectedSignature = signatureFactory.create(type, expires, historyId, pageSize);
		
		return expectedSignature.equals(expectedSignature);
	}


	private void writeResponse(HttpServletResponse response, int statusCode, String message) throws IOException {

		response.setStatus(statusCode);
		final String output = String.format("%d %s", statusCode, message);
		response.getOutputStream().println(output);
	}
}
