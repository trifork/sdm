package com.trifork.sdm.replication.admin.controllers;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
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

import com.trifork.sdm.replication.admin.models.Client;
import com.trifork.sdm.replication.admin.models.ClientRepository;
import com.trifork.sdm.replication.admin.models.LogEntryRepository;
import com.trifork.sdm.replication.admin.models.PermissionRepository;
import com.trifork.stamdata.NamingConvention;
import com.trifork.stamdata.Record;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Singleton
public class ClientController extends ApplicationController {

	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;

	@Inject
	private ClientRepository clientRepository;

	@Inject
	private LogEntryRepository logEntryRepository;

	@Inject
	private PermissionRepository permissionRepository;


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");

		// Check if the user requested a form to create
		// a new user.

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


	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");

		// See if we are updating or creating.

		String action = request.getParameter("action");
		String id = request.getParameter("id");
		
		if (id == null) {

			// We are creating.
			
			String name = request.getParameter("name");
			String certificateId = request.getParameter("certificate_id");

			Client client = clientRepository.create(name, certificateId);

			if (client != null) {

				logEntryRepository.create("Ny client tilf�jet '%s'. Oprettet af '%s'.", name, request.getAttribute(REQUEST_CPR_PARAMETER));

				response.sendRedirect(format("admin/users?id=%s", client.getId()));
			}
		}
		else if ("delete".equals(action)) {

			getDelete(request, response);
		}
		else {

			// Update an existing client.
			
			final String PREFIX = "resource_";

			Enumeration<String> e = request.getParameterNames();
			
			List<String> resources = new ArrayList<String>();

			while (e.hasMoreElements()) {

				String param = e.nextElement();
				
				if (param.startsWith(PREFIX)) {

					resources.add(param.substring(PREFIX.length()));
				}
			}
			
			permissionRepository.setPermissions(id, resources);
			
			// Audit log the update of permissions
			StringBuilder sb = new StringBuilder();
			for (String resource : resources) {
				sb.append(resource + " ");
			}
			
			String permissionsAsString = sb.toString().trim();
			if (permissionsAsString.length() > 400) {
				permissionsAsString = permissionsAsString.substring(0, 400) + "...";
			}
			logEntryRepository.create("Rettigheder redigeret for klient med id '%s' (%s). Redigeret af '%s'.", id, permissionsAsString, request.getAttribute(REQUEST_CPR_PARAMETER));

			response.sendRedirect("/admin/users");
		}
	}
	
	
	private void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");

		Client client = clientRepository.find(id);
		
		if (client != null) {
			
			clientRepository.destroy(id);
			
			logEntryRepository.create("Klient '%s (ID=%s)' blev slettet af '%s'.", client.getName(), client.getId(), request.getAttribute(REQUEST_CPR_PARAMETER));
			
			response.sendRedirect("/admin/users");
		}
	}


	private void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("user/new.ftl");

		render(response, template, null);
	}


	private void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("user/edit.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		root.put("client", clientRepository.find(id));

		root.put("resources", getResourceNames());
		root.put("permissions", permissionRepository.findByClientId(id));

		render(response, template, root);
	}


	private void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("user/list.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("clients", clientRepository.findAll());

		render(response, template, root);
	}


	@SuppressWarnings("unchecked")
	private Set<String> getResourceNames() {

		SortedSet<String> resources = new TreeSet<String>();

		// Find all entities and serve them as resources.

		final String INCLUDE_PACKAGE = Record.class.getPackage().getName();

		// TODO: Include doseringsforslag.

		// Right now we don't have an importer for doeringsforslag
		// so they cannot be replicated.

		final String EXCLUDE_PACKAGE = com.trifork.stamdata.registre.doseringsforslag.Drug.class.getPackage().getName();

		Reflections reflector = new Reflections(new ConfigurationBuilder().setUrls(ClasspathHelper.getUrlsForPackagePrefix(INCLUDE_PACKAGE)).filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix(INCLUDE_PACKAGE)).exclude(FilterBuilder.prefix(EXCLUDE_PACKAGE))).setScanners(new TypeAnnotationsScanner()));

		// Serve all entities by deferring their URLs and using their
		// annotations.

		Set<Class<?>> entities = reflector.getTypesAnnotatedWith(Entity.class);

		for (Class<?> entity : entities) {

			String uri = NamingConvention.getResourceName((Class<? extends Record>)entity);
			resources.add(uri);
		}

		return resources;
	}
}
