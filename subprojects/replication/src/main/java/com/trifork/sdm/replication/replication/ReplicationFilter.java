package com.trifork.sdm.replication.replication;

import static com.trifork.sdm.replication.replication.URLParameters.*;
import static java.lang.String.*;
import static java.net.HttpURLConnection.*;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.inject.*;
import com.trifork.sdm.replication.util.SignatureFactory;


@Singleton
public class ReplicationFilter implements Filter
{
	private static final long SECONDS = 1000l;

	private final SignatureFactory signatureFactory;


	@Inject
	ReplicationFilter(SignatureFactory signatureFactory)
	{
		this.signatureFactory = signatureFactory;
	}


	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
	}


	@Override
	public void destroy()
	{
	}


	@Override
	public void doFilter(ServletRequest rawRequest, ServletResponse rawResponse, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) rawRequest;
		HttpServletResponse response = (HttpServletResponse) rawResponse;

		String type = request.getParameter(ENTITY_TYPE);
		String historyId = request.getParameter(HISTORY_ID);
		String signature = request.getParameter(SIGNATURE);
		String expiresString = request.getParameter(EXPIRES);

		try
		{
			long expires = Long.parseLong(expiresString);
			int pageSize = Integer.parseInt(request.getParameter(PAGE_SIZE));

			// Validate the parameters.

			if (expires * SECONDS < System.currentTimeMillis())
			{
				writeResponse(response, HTTP_GONE, "The requested resource has expired. You took too long before using the URL.");
			}
			else if (signature == null)
			{
				writeResponse(response, HTTP_FORBIDDEN, "The request did not contain a 'signature' parameter.");
			}
			else if (!checkSignature(type, expires, historyId, pageSize, signature))
			{
				writeResponse(response, HTTP_FORBIDDEN, "The request's signature did not match the query.");
			}
			else
			{
				chain.doFilter(request, response);
			}
		}
		catch (NumberFormatException e)
		{
			writeResponse(response, HTTP_BAD_REQUEST, "The request had an invalid parameter.");
		}
	}


	private boolean checkSignature(String type, long expires, String historyId, int pageSize, String actualSignature)
	{
		String expectedSignature = signatureFactory.create(type, expires, historyId, pageSize);

		return actualSignature.equals(expectedSignature);
	}


	private void writeResponse(HttpServletResponse response, int statusCode, String message) throws IOException
	{
		response.setStatus(statusCode);
		final String output = format("%d %s", statusCode, message);
		response.getOutputStream().println(output);
	}
}
