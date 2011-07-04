package com.trifork.stamdata.authorization;

import static dk.sosi.seal.model.constants.DGWSConstants.VERSION_1_0_1;
import static dk.sosi.seal.model.constants.FaultCodeValues.PROCESSING_PROBLEM;
import static dk.sosi.seal.xml.XmlUtil.node2String;

import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;


@Singleton
public class WebService extends HttpServlet {

	private static final long serialVersionUID = 3803092375338013819L;

	private static final Logger logger = LoggerFactory.getLogger(WebService.class);
	
	private final SOSIFactory factory;
	private final Provider<RequestProcessor> requestProcessor;

	@Inject
	WebService(SOSIFactory factory, Provider<RequestProcessor> processorProvider) {

		this.factory = factory;
		this.requestProcessor = processorProvider;
	}

	@Override
	protected void doPost(HttpServletRequest in, HttpServletResponse out) throws ServletException, IOException {

		Reply response;

		try {
			Reader input = in.getReader();
			String xml = new Scanner(input).useDelimiter("\\A").next();
			
			Request request = factory.deserializeRequest(xml);
			response = requestProcessor.get().process(request);
		}
		catch (Exception e) {

			// We cannot say anything about the flow id
			// or message id in some circumstances.
			
			logger.warn("An unexpected error happend while processing request.", e);
			response = factory.createNewErrorReply(VERSION_1_0_1, "0", "0", PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
			out.setStatus(500);
		}

		out.setContentType("application/xml+soap");

		Document reply = response.serialize2DOMDocument();
		String xml = node2String(reply);
		out.getWriter().write(xml);
	}

	@Override
	public void init() throws ServletException {

	}
}
