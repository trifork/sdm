package com.trifork.sdm.replication.admin.controllers;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.IOException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.inject.Singleton;
import com.trifork.sdm.replication.db.properties.Transactional;


@Singleton
public class AuditLogController extends AbstractController
{
	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("entries", auditLog.all());

			render("log/list.ftl", root, request, response);
		}
		catch (Exception e)
		{
			throw new ServletException(e);
		}
	}


	private static final long serialVersionUID = 1L;
}
