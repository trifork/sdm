package com.trifork.stamdata.authorization;

import static dk.sosi.seal.model.constants.DGWSConstants.VERSION_1_0_1;
import static dk.sosi.seal.model.constants.FaultCodeValues.PROCESSING_PROBLEM;
import static dk.sosi.seal.xml.XmlUtil.node2String;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private final JAXBContext jaxbContext;
	private final Set<String> whitelist;
	private final Provider<AuthorizationDao> authorizationDao;

	@Inject
	WebService(Set<String> whitelist, SOSIFactory factory, JAXBContext context, Provider<AuthorizationDao> authorizationDao) {

		this.whitelist = whitelist;
		this.factory = factory;
		this.jaxbContext = context;
		this.authorizationDao = authorizationDao;
	}

	@Override
	protected void doPost(HttpServletRequest in, HttpServletResponse out) throws ServletException, IOException {

		Reply response;

		try {
			String xml = new Scanner(in.getInputStream()).useDelimiter("\\A").next();
			
			Request request = factory.deserializeRequest(xml);
			RequestProcessor processor = new RequestProcessor(factory, whitelist, jaxbContext.createMarshaller(), jaxbContext.createUnmarshaller(), authorizationDao.get());
			response = processor.process(request);
		}
		catch (Exception e) {

			// We cannot say anything about the flow id
			// or message id in some circumstances.
			
			logger.warn("An unexpected error happend while processing request.", e);
			response = factory.createNewErrorReply(VERSION_1_0_1, "0", "0", PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
			out.setStatus(500);
		}

		out.setContentType("application/xml+soap");

		String xml = node2String(response.serialize2DOMDocument());
		out.getWriter().write(xml);
	}

	@Override
	public void init() throws ServletException {

	}
}
