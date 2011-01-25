package com.trifork.sdm.replication.admin.controllers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class ApplicationController extends HttpServlet {
	protected static final String REQUEST_CPR_PARAMETER = "cpr";

	private static final long serialVersionUID = 1L;

	protected void render(HttpServletResponse response, Template template, Map<String, Object> root) throws IOException, ServletException {

		Writer writer = new OutputStreamWriter(response.getOutputStream());

		try {
			template.process(root, writer);

		}
		catch (TemplateException e) {

			throw new ServletException(e);
		}
	}
}
