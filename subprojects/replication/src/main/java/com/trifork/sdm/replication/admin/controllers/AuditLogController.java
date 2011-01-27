package com.trifork.sdm.replication.admin.controllers;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.trifork.sdm.replication.admin.models.AuditLogRepository;
import com.trifork.sdm.replication.db.properties.AdminTransaction;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Singleton
public class AuditLogController extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;

	@Inject
	private AuditLogRepository repository;


	@Override
	@AdminTransaction
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			response.setContentType("text/html; charset=utf-8");

			Template template = config.getTemplate("log/list.ftl");

			Map<String, Object> root = new HashMap<String, Object>();

			root.put("entries", repository.findAll());

			Writer writer = new OutputStreamWriter(response.getOutputStream());

			template.process(root, writer);
		}
		catch (Exception e)
		{
			// TODO: Log this.

			throw new ServletException(e);
		}
	}
}
