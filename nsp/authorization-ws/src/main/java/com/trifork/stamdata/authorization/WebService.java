// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
