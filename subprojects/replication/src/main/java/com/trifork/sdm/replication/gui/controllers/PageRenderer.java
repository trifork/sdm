package com.trifork.sdm.replication.gui.controllers;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;

import freemarker.template.*;


public class PageRenderer {

	protected final Configuration templates;


	@Inject
	PageRenderer(Configuration templates) {

		this.templates = templates;
	}


	protected void render(String templatePath, Map<String, Object> root, HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/html; charset=utf-8");

		if (root == null) {
			root = new HashMap<String, Object>();
		}

		root.put("contextRoot", request.getContextPath());

		try {
			Template page = templates.getTemplate(templatePath, "UTF-8");

			StringWriter bodyWriter = new StringWriter();
			page.process(root, bodyWriter);
			root.put("body", bodyWriter.toString());
			bodyWriter.close();

			Template html = templates.getTemplate("application.ftl");

			PrintWriter w = response.getWriter();
			html.process(root, w);
		}
		catch (TemplateException e) {
			throw new IOException(e);
		}
	}
}
