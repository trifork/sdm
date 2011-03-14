package com.trifork.sdm.replication.gui.controllers;


import static com.trifork.sdm.replication.gui.models.RequestAttributes.*;

import java.io.IOException;

import javax.servlet.http.*;


public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = -4680156227905470327L;


	protected String getUserCPR(HttpServletRequest request) {

		String cpr = null;

		if (request.getAttribute(USER_CPR) != null) {
			cpr = request.getAttribute(USER_CPR).toString();
		}

		return cpr;
	}


	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {
		String contextPath = request.getContextPath();
		String encodedUrl = response.encodeURL(contextPath + path);
		response.sendRedirect(encodedUrl);
	}
}
