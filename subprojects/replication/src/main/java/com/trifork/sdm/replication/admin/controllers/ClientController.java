package com.trifork.sdm.replication.admin.controllers;

import static com.trifork.sdm.replication.db.properties.Database.*;
import static org.slf4j.LoggerFactory.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.persistence.Entity;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;
import org.slf4j.*;

import com.google.inject.*;
import com.trifork.sdm.replication.admin.models.*;
import com.trifork.sdm.replication.db.TransactionManager.OutOfTransactionException;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.stamdata.*;


@Singleton
public class ClientController extends AbstractController
{
	private static final Logger LOG = getLogger(ClientController.class);
	
	@Inject
	private ClientRepository clients;

	@Inject
	private PermissionRepository permissions;


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Check if the user requested a form to create
		// a new user.

		try
		{
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
		catch (Throwable e)
		{
			throw new ServletException(e);
		}
	}


	@Override
	@Transactional(ADMINISTRATION)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// See if we are updating or creating.

		String method = request.getParameter("method");
		String id = request.getParameter("id");

		LOG.info("method='" + method + "' id=" + id);
		
		try
		{
			if (id == null)
			{
				getCreate(request, response);
			}
			else if ("DELETE".equals(method))
			{
				LOG.info("DELETE id=" + id);
				getDelete(request, response);
			}
			else
			{
				getUpdate(request, response);
			}
		}
		catch (Throwable t)
		{
			// TODO: Log

			throw new ServletException(t);
		}
	}


	private void getCreate(HttpServletRequest request, HttpServletResponse response) throws OutOfTransactionException, SQLException, IOException
	{
		// Create a new client.

		String name = request.getParameter("name");
		String certificateId = request.getParameter("certificate_id");

		Client client = clients.create(name, certificateId);

		if (client != null)
		{
			writeAudit("New client added '%s'. Created by '%s'.", name, getUserCPR(request));

			redirect(request, response, "/admin/clients?id=" + client.getId());
		}
	}


	private void getUpdate(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException
	{
		// Update an the permissions for a client.

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

		String id = request.getParameter("id");
		permissions.update(id, entities);

		// TODO: This is not good enough, must show all entities.
		// You can make a list by fetching them first, and then seeing,
		// what the difference if the string becomes too long.

		StringBuilder stringBuilder = new StringBuilder();

		for (String entity : entities)
		{
			stringBuilder.append(entity).append(" ");
		}

		String permissionsAsString = stringBuilder.toString().trim();

		if (permissionsAsString.length() > 400)
		{
			permissionsAsString = permissionsAsString.substring(0, 400) + "...";
		}

		writeAudit("User CPR=%s authorized client (ID=%s) for entities (%s).", getUserCPR(request), id, permissionsAsString);

		redirect(request, response, "/admin/clients");
	}


	private void getDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, OutOfTransactionException, SQLException, IOException
	{
		String id = request.getParameter("id");

		Client client = clients.find(id);

		if (client != null)
		{
			clients.destroy(id);
			writeAudit("Client '%s (ID=%s)' was deleted by '%s'.", client.getName(), client.getId(), getUserCPR(request));
		}

		redirect(request, response, "/admin/clients");
	}


	private void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
	{
		render("client/new.ftl", null, request, response);
	}


	private void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, OutOfTransactionException, SQLException
	{
		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		root.put("client", clients.find(id));

		root.put("entities", getEntityNames());
		root.put("permissions", permissions.findByClientId(id));

		render("client/edit.ftl", root, request, response);
	}


	private void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, OutOfTransactionException, SQLException
	{
		Map<String, Object> root = new HashMap<String, Object>();

		root.put("clients", clients.findAll());

		render("client/list.ftl", root, request, response);
	}


	private Set<String> getEntityNames()
	{
		// TODO: Make this method part of the Entities class.

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
			String uri = Entities.getName(entity);
			entities.add(uri);
		}

		return entities;
	}

	private static final long serialVersionUID = 1L;
}
