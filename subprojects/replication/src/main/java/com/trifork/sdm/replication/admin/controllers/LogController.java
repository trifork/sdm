package com.trifork.sdm.replication.admin.controllers;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trifork.sdm.replication.admin.models.LogEntryRepository;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


@Singleton
public class LogController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;
	
	@Inject
	private LogEntryRepository logEntryRepository;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");
		
		Template template = config.getTemplate("log/list.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("entries", logEntryRepository.findAll());

		Writer writer = new OutputStreamWriter(response.getOutputStream());

		try {
			template.process(root, writer);
			
		}
		catch (TemplateException e) {

			throw new ServletException(e);
		}
	}
}
