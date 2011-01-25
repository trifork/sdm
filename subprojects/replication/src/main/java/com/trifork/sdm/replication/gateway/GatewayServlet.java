package com.trifork.sdm.replication.gateway;


import static dk.sosi.seal.model.constants.FaultCodeValues.*;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.*;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;

import com.trifork.sdm.replication.settings.SOAP;

import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FaultCodeValues;


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


	@Override
	protected void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{

	}


	@Override
	protected void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		unsupportedMethod(httpRequest, httpResponse);
	}


	@Override
	protected void doHead(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		unsupportedMethod(httpRequest, httpResponse);
	}


	@Override
	protected void doPut(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		unsupportedMethod(httpRequest, httpResponse);
	}


	@Override
	protected void doDelete(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		unsupportedMethod(httpRequest, httpResponse);
	}


	protected void processRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException
	{
		// Read the HTTP request.

		InputStream inputStream = httpRequest.getInputStream();
		String content = IOUtils.toString(inputStream, "UTF-8");
		String method = httpRequest.getMethod();

		// Process it.

		RequestProcessor processor = processorProvider.get();

		processor.process(content, method);

		// Return the result.

		httpResponse.setContentType(processor.getContentType());
		httpResponse.setStatus(processor.getResponseCode());

		IOUtils.write(processor.getResponse(), httpResponse.getOutputStream());
	}


	protected void unsupportedMethod(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException
	{
		
	}
}
