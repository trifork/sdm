package com.trifork.stamdata.replication.gui.controllers;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.ClientDao;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.util.DatabaseAuditLogger;


@Singleton
public class ClientController extends AbstractController {

	private static final long serialVersionUID = 6725511977937323744L;

	private final Provider<ClientDao> clients;
	private final Map<String, Class<? extends View>> registry;

	private final Provider<PageRenderer> renderer;

	private final Provider<DatabaseAuditLogger> audit;

	private final Provider<User> user;

	@Inject
	public ClientController(Provider<User> user, Provider<ClientDao> clients, Provider<PageRenderer> renderer, Provider<DatabaseAuditLogger> audit, @Registry Map<String, Class<? extends View>> registry) {

		this.user = checkNotNull(user);
		this.clients = checkNotNull(clients);
		this.renderer = checkNotNull(renderer);
		this.audit = checkNotNull(audit);
		this.registry = checkNotNull(registry);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Check if the user requested a form to create
		// a new user.

		if (request.getRequestURI().endsWith("/new")) {
			showNew(request, response);
		}

		// If the ID parameter is null, we list all
		// users. If it is present we show a specific user.

		else if (request.getParameter("id") == null) {
			showList(request, response);
		}
		else {
			showEdit(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// See if we are updating or creating.

		String method = request.getParameter("method");
		String id = request.getParameter("id");

		if (id == null) {
			create(request, response);
		}
		else if ("DELETE".equals(method)) {
			getDelete(request, response);
		}
		else {
			getUpdate(request, response);
		}
	}

	protected void create(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Create a new client.

		String name = request.getParameter("name");
		String cvr = request.getParameter("certificate_id");

		Client client = clients.get().create(name, cvr);
		
		if (client != null) {
			audit.get().write("New client %s created by %s.", client, user.get());
		}

		redirect(request, response, "/admin/clients");
	}

	protected void getUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Update an the permissions for a client.

		@SuppressWarnings("unchecked")
		Enumeration<String> params = request.getParameterNames();

		List<String> views = new ArrayList<String>();

		while (params.hasMoreElements()) {
			String param = params.nextElement();

			if (param.startsWith("entity_")) {
				views.add(param.substring("entity_".length()));
			}
		}

		String id = request.getParameter("id");

		Client client = clients.get().find(id);

		for (String view : views)
			client.addPermission(view);

		clients.get().update(client);

		redirect(request, response, "/admin/clients");
	}

	protected void getDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String id = request.getParameter("id");

		Client client = clients.get().find(id);

		if (clients.get().delete(id)) {
			audit.get().write("Client %s was deleted by %s.", client, user.get());
		}

		redirect(request, response, "/admin/clients");
	}

	protected void showNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		renderer.get().render("/client/new.ftl", null, request, response);
	}

	protected void showEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");

		Client client = clients.get().find(id);

		root.put("client", client);

		root.put("entities", registry.keySet());
		root.put("permissions", client.getPermissions());

		renderer.get().render("/client/edit.ftl", root, request, response);
	}

	protected void showList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("clients", clients.get().findAll());

		renderer.get().render("/client/list.ftl", root, request, response);
	}
}
