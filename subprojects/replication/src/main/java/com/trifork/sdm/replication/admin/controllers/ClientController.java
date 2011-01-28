package com.trifork.sdm.replication.admin.controllers;


import static com.trifork.sdm.replication.admin.models.RequestAttributes.USER_CPR;
import static com.trifork.sdm.replication.db.properties.Database.ADMINISTRATION;
import static java.lang.String.format;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.Entity;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.trifork.sdm.replication.admin.models.AuditLogRepository;
import com.trifork.sdm.replication.admin.models.Client;
import com.trifork.sdm.replication.admin.models.ClientRepository;
import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.sdm.replication.db.TransactionManager.OutOfTransactionException;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.stamdata.Entities;
import com.trifork.stamdata.Record;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Singleton
public class ClientController extends AbstractController
{
	@Inject
	private Configuration config;

	@Inject
	private ClientRepository clientRepository;

	@Inject
	private AuditLogRepository auditlogRepository;

	@Inject
	private PermissionRepository permissionRepository;


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=utf-8");

		// Check if the user requested a form to create
		// a new user.

		try {
			if (request.getRequestURI().endsWith("/new")) {
				getNew(request, response);
			}

			// If the ID parameter is null, we list all
			// users. If it is present we show a specific user.

			else if (request.getParameter("id") == null) {
				getList(request, response);
			} else {
				getEdit(request, response);
			}
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

	@Override
	@Transactional(ADMINISTRATION)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		response.setContentType("text/html; charset=utf-8");

		// See if we are updating or creating.

		String action = request.getParameter("action");
		String id = request.getParameter("id");

		// TODO: Clean this method up.

		try
		{
			if (id == null)
			{
				// We are creating.

				String name = request.getParameter("name");
				String certificateId = request.getParameter("certificate_id");

				Client client = clientRepository.create(name, certificateId);

				if (client != null)
				{
					auditlogRepository.create("New client added '%s'. Created by '%s'.", name, request.getAttribute(USER_CPR));

					response.sendRedirect(format("admin/users?id=%s", client.getId()));
				}
			}
			else if ("delete".equals(action))
			{
				getDelete(request, response);
			}
			else
			{
				// Update an existing client.

				final String PREFIX = "entity_";

				@SuppressWarnings("unchecked")
				Enumeration<String> e = request.getParameterNames();

				List<String> entities = new ArrayList<String>();

				while (e.hasMoreElements())
				{
					String param = e.nextElement();

					if (param.startsWith(PREFIX))
					{
						entities.add(param.substring(PREFIX.length()));
					}
				}

				permissionRepository.setPermissions(id, entities);

				// Audit log the update of permissions.

				StringBuilder stringBuilder = new StringBuilder();

				for (String entity : entities)
				{
					stringBuilder.append(entity + " ");
				}

				String permissionsAsString = stringBuilder.toString().trim();

				if (permissionsAsString.length() > 400)
				{
					permissionsAsString = permissionsAsString.substring(0, 400) + "...";
				}

				String adminCPR = request.getAttribute(USER_CPR).toString();

				// TODO: This is not good enough, must show all entities.
				// You can make a list by fetching them first, and then seeing,
				// what the difference if the string becomes too long.

				auditlogRepository.create("Authorization updated for client_id='%s' by '%s' for entities (%s).", id, adminCPR, permissionsAsString);

				response.sendRedirect("/admin/users");
			}

		}
		catch (Throwable t)
		{
			// TODO: Log
			
			throw new ServletException(t);
		}
	}


	private void getDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException
	{
		String id = request.getParameter("id");

		try
		{
			Client client = clientRepository.find(id);

			if (client != null)
			{
				clientRepository.destroy(id);

				auditlogRepository.create("Client '%s (ID=%s)' was deleted by '%s'.", client.getName(), client.getId(), request.getAttribute(USER_CPR));

				response.sendRedirect("/admin/users");
			}
		}
		catch (Throwable t)
		{
			// TODO: Log

			throw new ServletException(t);
		}
	}


	private void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		Template template = config.getTemplate("user/new.ftl");

		render(response, template, null);
	}


	private void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, OutOfTransactionException, SQLException
	{

		Template template = config.getTemplate("user/edit.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		root.put("client", clientRepository.find(id));

		root.put("entities", getEntityNames());
		root.put("permissions", permissionRepository.findByClientId(id));

		render(response, template, root);
	}


	private void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, OutOfTransactionException, SQLException
	{
		Template template = config.getTemplate("user/list.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("clients", clientRepository.findAll());

		render(response, template, root);
	}


	@SuppressWarnings("unchecked")
	private Set<String> getEntityNames()
	{
		// TODO: Make thid method part of the Entities class.

		final String INCLUDE_PACKAGE = Record.class.getPackage().getName();

		// TODO: Include doseringsforslag at a later point.

		// Right now we don't have an importer for doeringsforslag
		// so they cannot be replicated.

		final String EXCLUDE_PACKAGE = com.trifork.stamdata.registre.doseringsforslag.Drug.class.getPackage().getName();

		Reflections reflector = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForPackagePrefix(INCLUDE_PACKAGE)).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(INCLUDE_PACKAGE)).exclude(FilterBuilder.prefix(EXCLUDE_PACKAGE))).setScanners(new TypeAnnotationsScanner()));

		// Serve all entities by deferring their URLs and using their
		// annotations.

		Set<Class<?>> classes = reflector.getTypesAnnotatedWith(Entity.class);
		Set<String> entities = new TreeSet<String>();

		for (Class<?> entity : classes)
		{
			String uri = Entities.getName((Class<? extends Record>) entity);
			entities.add(uri);
		}

		return entities;
	}
	
	private static final long serialVersionUID = 0;
}
