package com.trifork.sdm.replication.admin.controllers;


import static java.lang.String.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trifork.sdm.replication.admin.models.*;
import com.trifork.sdm.replication.admin.properties.Whitelist;
import com.trifork.sdm.replication.db.properties.AdminTransaction;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Singleton
public class UserController extends AbstractController
{
	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AuditLogRepository log;

	@SuppressWarnings("rawtypes")
	@Inject
	@Whitelist
	private Map whitelist;


	@Override
	@AdminTransaction
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=utf-8");

		// Check if the user requested a form to create
		// a new user.

		if (request.getRequestURI().endsWith("/new"))
		{
			getNew(request, response);
		}

		// If the ID parameter is null, we list all
		// users. If it is present we show a specific user.

		else if (request.getParameter("id") == null)
		{
			getList(request, response);
		}

		else
		{
			getEdit(request, response);
		}
	}


	@Override
	@AdminTransaction
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=utf-8");

		String action = request.getParameter("action");

		try
		{

			if ("delete".equals(action))
			{
				getDelete(request, response);
			}
			else
			{
				// Get the new administrator's info from the
				// HTML form.

				String newUserName = request.getParameter("name");
				String newUserCPR = request.getParameter("cpr");

				String newUserFirm = request.getParameter("firm");
				String newUserCVR = (String) whitelist.get(newUserFirm);

				User user = userRepository.create(newUserName, newUserCPR, newUserCVR);

				if (user != null)
				{
					// We also need info about the user creating the new

					String userCPR = getUserCPR(request);

					log.create("Ny admin tilføjet '%s'. Oprettet af '%s'.", newUserName, userCPR);
					response.sendRedirect(format("/admin/admins?id=%s", user.getId()));
				}
			}
		}
		catch (SQLException e)
		{
			// TODO: Log.
		}

		// TODO: Should always redirect.
	}


	private void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException
	{
		String id = request.getParameter("id");

		User deletedUser = userRepository.find(id);

		if (deletedUser != null)
		{
			String userCPR = getUserCPR(request);

			userRepository.destroy(id);

			log.create(format("User '%s (ID=%s)' blev slettet af '%s'.", deletedUser.getName(), deletedUser.getId(), userCPR));
		}

		response.sendRedirect("/admin/admins");
	}


	private void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		Template template = config.getTemplate("admin/list.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("admins", userRepository.findAll());

		render(response, template, root);
	}


	@SuppressWarnings("unchecked")
	private void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		Template template = config.getTemplate("admin/new.ftl");

		// List the white listed firms (CVR).

		Map<String, Object> root = new HashMap<String, Object>();
		root.put("firms", new ArrayList<String>(whitelist.keySet()));

		render(response, template, root);
	}


	private void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		Template template = config.getTemplate("admin/edit.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		User user = userRepository.find(id);
		root.put("admin", user);

		render(response, template, root);
	}
}
