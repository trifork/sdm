package com.trifork.sdm.replication.admin.controllers;

import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.inject.*;
import com.trifork.sdm.replication.admin.models.AuditLogRepository;
import com.trifork.sdm.replication.db.properties.Transactional;

import freemarker.template.*;


@Singleton
public class AuditLogController extends AbstractController
{
	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;

	@Inject
	private AuditLogRepository repository;


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			response.setContentType("text/html; charset=utf-8");

			Template template = config.getTemplate("log/list.ftl");

			Map<String, Object> root = new HashMap<String, Object>();

			root.put("entries", repository.findAll());

			render(request, response, template, root);
		}
		catch (Exception e)
		{
			// TODO: Log this.

			throw new ServletException(e);
		}
	}
}
