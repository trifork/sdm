package com.trifork.sdm.replication.gateway;


import static com.trifork.sdm.replication.admin.models.RequestAttributes.*;

import java.io.*;

import javax.inject.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;

import com.trifork.sdm.replication.gateway.properties.SOAP;


@Singleton
public class GatewayServlet extends HttpServlet
{
	private static final long serialVersionUID = 8476350912985820545L;

	private final Provider<RequestProcessor> processorProvider;


	@Inject
	GatewayServlet(@SOAP Provider<RequestProcessor> processorProvider)
	{
		this.processorProvider = processorProvider;
	}


	protected void processRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		// Read the HTTP request.

		InputStream inputStream = httpRequest.getInputStream();
		String content = IOUtils.toString(inputStream, "UTF-8");
		String method = httpRequest.getMethod();

		// Get the client's CVR (This is produced by the STS filter.)

		String clientCVR = httpRequest.getAttribute(USER_CPR).toString();

		// Process it.

		RequestProcessor processor = processorProvider.get();

		processor.process(content, clientCVR, method);

		// Return the result.

		httpResponse.setContentType(processor.getContentType());
		httpResponse.setStatus(processor.getResponseCode());

		IOUtils.write(processor.getResponse(), httpResponse.getOutputStream());
	}


	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		processRequest(httpRequest, httpResponse);
	}


	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		processRequest(httpRequest, httpResponse);
	}


	@Override
	protected void doHead(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		processRequest(httpRequest, httpResponse);
	}


	@Override
	protected void doPut(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		processRequest(httpRequest, httpResponse);
	}


	@Override
	protected void doDelete(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		processRequest(httpRequest, httpResponse);
	}
}
