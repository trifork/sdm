// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.gui.controllers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.gui.models.Client;
import com.trifork.stamdata.replication.gui.models.ClientDao;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.logging.AuditLogger;
import com.trifork.stamdata.replication.replication.annotations.Registry;
import com.trifork.stamdata.replication.replication.views.View;


@Singleton
public class ClientController extends AbstractController {

	private static final long serialVersionUID = 6725511977937323744L;

	private final Provider<ClientDao> clients;
	private final Map<String, Class<? extends View>> registry;

	private final Provider<PageRenderer> renderer;

	private final Provider<AuditLogger> audit;

	private final Provider<User> user;

	@Inject
	public ClientController(Provider<User> user, Provider<ClientDao> clients, Provider<PageRenderer> renderer, Provider<AuditLogger> audit, @Registry Map<String, Class<? extends View>> registry) {

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
			delete(request, response);
		}
		else {
			update(request, response);
		}
	}

	protected void create(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Create a new client.

		String name = request.getParameter("name");
		String cvr = request.getParameter("certificate_id");

		Client client = clients.get().create(name, cvr);
		
		if (client != null) {
			audit.get().log("New client %s created by %s.", client, user.get());
		}

		redirect(request, response, "/admin/clients");
	}

	protected void update(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Update an the permissions for a client.

		@SuppressWarnings("unchecked")
		Enumeration<String> params = request.getParameterNames();

		Set<String> views = Sets.newHashSet();

		while (params.hasMoreElements()) {
			String param = params.nextElement();

			if (param.startsWith("entity_")) {
				views.add(param.substring("entity_".length()));
			}
		}

		Client client = clients.get().find(request.getParameter("id"));
		
		AuditLogger auditLogger = audit.get();
		
		Set<String> addedPermissions = Sets.difference(views, client.getPermissions());
		
		for (String view : addedPermissions) {
			client.addPermission(view);
			auditLogger.log("User=%s granted Client=%s access to View=%s", user.get(), client, view);
		}
		
		Set<String> removedPermissions = ImmutableSet.copyOf(Sets.difference(client.getPermissions(), views));
		
		for (String view : removedPermissions) {
			client.removePermission(view);
			auditLogger.log("User=%s restricted Client=%s from accessing View=%s", user.get(), client, view);
		}

		clients.get().update(client);

		redirect(request, response, "/admin/clients");
	}

	protected void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String id = request.getParameter("id");

		Client client = clients.get().find(id);

		if (clients.get().delete(id)) {
			audit.get().log("Client %s was deleted by %s.", client, user.get());
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
