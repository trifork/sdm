package com.trifork.sdm.replication.admin.controllers;


import static com.trifork.sdm.replication.db.properties.Database.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.slf4j.*;

import com.google.inject.*;
import com.trifork.sdm.replication.admin.models.*;
import com.trifork.sdm.replication.admin.security.WhitelistModule.Whitelist;
import com.trifork.sdm.replication.db.properties.Transactional;


@Singleton
public class UserController extends AbstractController
{
	private static final Logger LOG = getLogger(UserController.class);

	@Inject
	private IUserRepository users;

	@Inject
	@Whitelist
	private Set<String> whitelist;


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			// Check if the user requested a form to create
			// a new user.

			if (request.getRequestURI().endsWith("/new"))
			{
				getNew(request, response);
			}
			else if (request.getParameter("id") == null)
			{
				// If the ID parameter is null, we list all
				// users. If it is present we show a specific user.

				getList(request, response);
			}
			else
			{
				getEdit(request, response);
			}
		}
		catch (SQLException e)
		{
			throw new ServletException(e);
		}
	}


	@Override
	@Transactional(ADMINISTRATION)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		try
		{
			String method = request.getParameter("method");

			if ("DELETE".equals(method))
			{
				getDelete(request, response);
			}
			else
			{
				getCreate(request, response);
			}
		}
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}


	protected void getCreate(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException
	{
		// Get the new administrator's info from the
		// HTML form.

		String newUserName = request.getParameter("name");
		String newUserCPR = request.getParameter("cpr");
		String newUserCVR = request.getParameter("firm");

		User user = users.create(newUserName, newUserCPR, newUserCVR);

		if (!whitelist.contains(newUserCVR))
		{
			// TODO: Log and write the CVR
		}
		else if (user != null)
		{
			writeAudit("New administrator created (new_user_cpr=%s, new_user_cvr=%s). Created by user_cpr=%s.", newUserCPR, newUserCVR, getUserCPR(request));
		}

		redirect(request, response, "/admin/users");
	}


	protected void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException
	{
		String id = request.getParameter("id");

		User deletedUser = users.find(id);

		if (deletedUser != null)
		{
			String userCPR = getUserCPR(request);

			users.destroy(id);

			writeAudit("Administrator '%s (ID=%s)' was deleted by user %s.", deletedUser.getName(), deletedUser.getId(), userCPR);
		}

		redirect(request, response, "/admin/users");
	}


	protected void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException
	{
		Map<String, Object> root = new HashMap<String, Object>();

		root.put("users", users.findAll());

		render("/user/list.ftl", root, request, response);
	}


	protected void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		// List the white listed firms (CVR).

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("firms", whitelist);

		render("/user/new.ftl", root, request, response);
	}


	protected void getEdit(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException, ServletException
	{
		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		LOG.info("User found ID=" + id);
		User user = users.find(id);
		root.put("user", user);

		render("/user/edit.ftl", root, request, response);
	}


	private static final long serialVersionUID = 1L;
}
