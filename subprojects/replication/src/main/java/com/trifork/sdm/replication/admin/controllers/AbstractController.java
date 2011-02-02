package com.trifork.sdm.replication.admin.controllers;


import static com.trifork.sdm.replication.admin.models.RequestAttributes.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.inject.*;
import com.trifork.sdm.replication.admin.models.AuditLog;

import freemarker.template.*;


public class AbstractController extends HttpServlet
{
	@Inject
	protected Configuration templates;

	@Inject
	protected AuditLog auditLog;


	protected void render(String templatePath, Map<String, Object> root, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		response.setContentType("text/html; charset=utf-8");

		if (root == null)
		{
			root = new HashMap<String, Object>();
		}

		root.put("contextRoot", request.getContextPath());

		try
		{
			Template page = templates.getTemplate(templatePath, "UTF-8");

			StringWriter bodyWriter = new StringWriter();
			page.process(root, bodyWriter);
			root.put("body", bodyWriter.toString());
			bodyWriter.close();

			Template html = templates.getTemplate("application.ftl");
			html.process(root, response.getWriter());
		}
		catch (TemplateException e)
		{
			throw new ServletException(e);
		}
	}


	protected void writeAudit(String format, Object... args) throws SQLException
	{
		auditLog.create(format, args);
	}


	protected String getUserCPR(HttpServletRequest request)
	{
		String cpr;

		if (request.getAttribute(USER_CPR) != null)
		{
			cpr = request.getAttribute(USER_CPR).toString();
		}
		else
		{
			cpr = "UNKNOWN";
		}

		return cpr;
	}


	protected void redirect(HttpServletRequest request, HttpServletResponse response, String path) throws IOException
	{
		String contextPath = request.getContextPath();
		String encodedUrl = response.encodeURL(contextPath + path);
		response.sendRedirect(encodedUrl);
	}


	private static final long serialVersionUID = 1L;
}
