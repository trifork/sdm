package com.trifork.sdm.replication.gui.controllers;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.sdm.replication.gui.models.*;
import com.trifork.sdm.replication.replication.annotations.Registry;
import com.trifork.sdm.replication.replication.models.Record;


@Singleton
public class ClientController extends AbstractController {

	private static final long serialVersionUID = 6725511977937323744L;

	private final ClientDao clients;
	private final PermissionDao permissions;
	private final Map<String, Class<? extends Record>> registry;

	private final PageRenderer renderer;

	private final AuditLog audit;


	@Inject
	public ClientController(ClientDao clients, PermissionDao permissions, PageRenderer renderer, AuditLog audit, @Registry Map<String, Class<? extends Record>> registry) {

		this.clients = clients;
		this.permissions = permissions;
		this.renderer = renderer;
		this.audit = audit;
		this.registry = registry;
	}


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
			}
			else {
				getEdit(request, response);
			}
		}
		catch (Throwable e) {
			throw new ServletException(e);
		}
	}


	@Override
	@Transactional(ADMINISTRATION)
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// See if we are updating or creating.

		String method = request.getParameter("method");
		String id = request.getParameter("id");

		try {
			if (id == null) {
				getCreate(request, response);
			}
			else if ("DELETE".equals(method)) {
				getDelete(request, response);
			}
			else {
				getUpdate(request, response);
			}
		}
		catch (Throwable t) {
			throw new ServletException(t);
		}
	}


	protected void getCreate(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		// Create a new client.

		String name = request.getParameter("name");
		String certificateId = request.getParameter("certificate_id");

		Client client = clients.create(name, certificateId);

		if (client != null) {
			audit.write("New client added '%s'. Created by '%s'.", name, getUserCPR(request));

			redirect(request, response, "/admin/clients?id=" + client.getId());
		}
	}


	protected void getUpdate(HttpServletRequest request, HttpServletResponse response) throws SQLException, IOException {
		// Update an the permissions for a client.

		@SuppressWarnings("unchecked")
		Enumeration<String> params = request.getParameterNames();

		List<String> entities = new ArrayList<String>();

		while (params.hasMoreElements()) {
			String param = params.nextElement();

			if (param.startsWith("entity_")) {
				entities.add(param.substring("entity_".length()));
			}
		}

		String id = request.getParameter("id");
		permissions.update(id, entities);

		// TODO: This is not good enough, must show all entities.
		// You can make a list by fetching them first, and then seeing,
		// what the difference if the string becomes too long.

		StringBuilder stringBuilder = new StringBuilder();

		for (String entity : entities) {
			stringBuilder.append(entity).append(" ");
		}

		String permissionsAsString = stringBuilder.toString().trim();

		if (permissionsAsString.length() > 400) {
			permissionsAsString = permissionsAsString.substring(0, 400) + "...";
		}

		audit.write("User CPR=%s authorized client (ID=%s) for entities (%s).", getUserCPR(request), id, permissionsAsString);

		redirect(request, response, "/admin/clients");
	}


	protected void getDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, SQLException, IOException {
		String id = request.getParameter("id");

		Client client = clients.find(id);

		if (client != null) {
			clients.destroy(id);
			audit.write("Client '%s (ID=%s)' was deleted by '%s'.", client.getName(), client.getId(), getUserCPR(request));
		}

		redirect(request, response, "/admin/clients");
	}


	protected void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		renderer.render("client/new.ftl", null, request, response);
	}


	protected void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		root.put("client", clients.find(id));

		root.put("entities", getEntityNames());
		root.put("permissions", permissions.findByClientId(id));

		renderer.render("client/edit.ftl", root, request, response);
	}


	protected void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, SQLException {
		Map<String, Object> root = new HashMap<String, Object>();

		root.put("clients", clients.findAll());

		renderer.render("client/list.ftl", root, request, response);
	}


	private Set<String> getEntityNames() {

		return registry.keySet();
	}
}
