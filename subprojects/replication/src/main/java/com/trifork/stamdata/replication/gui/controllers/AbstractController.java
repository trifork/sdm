package com.trifork.stamdata.replication.gui.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class AbstractController extends HttpServlet {

	private static final long serialVersionUID = -4680156227905470327L;

	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {

		String contextPath = request.getContextPath();
		String encodedUrl = response.encodeURL(contextPath + path);
		response.sendRedirect(encodedUrl);
	}
}
