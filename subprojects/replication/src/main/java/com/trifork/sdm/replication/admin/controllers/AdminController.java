package com.trifork.sdm.replication.admin.controllers;

import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.trifork.sdm.replication.admin.models.Admin;
import com.trifork.sdm.replication.admin.models.AdminRepository;
import com.trifork.sdm.replication.admin.models.LogEntryRepository;
import com.trifork.sdm.replication.settings.Whitelist;

import freemarker.template.Configuration;
import freemarker.template.Template;


@Singleton
public class AdminController extends ApplicationController {

	private static final long serialVersionUID = 1L;

	@Inject
	private Configuration config;

	@Inject
	private AdminRepository adminRepository;

	@Inject
	private LogEntryRepository logEntryRepository;
	
	@SuppressWarnings("rawtypes")
	@Inject @Whitelist
	private Map whitelist;


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


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");

		String action = request.getParameter("action");

		if ("delete".equals(action)) {

			getDelete(request, response);
		}
		else {

			String name = request.getParameter("name");
			String cpr = request.getParameter("cpr");
			
			// Figure out which CVR we should write,
			// by looking it up in the whitelist.
			
			String firm = request.getParameter("firm");
			String cvr = (String) whitelist.get(firm);
			
			Admin admin = adminRepository.create(name, cpr, cvr);

			if (admin != null) {

				logEntryRepository.create("Ny admin tilføjet '%s'. Oprettet af '%s'.", name, request.getAttribute(REQUEST_CPR_PARAMETER));

				response.sendRedirect(format("/admin/admins?id=%s", admin.getId()));
			}
		}
	}


	private void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");

		Admin admin = adminRepository.find(id);
		
		if (admin != null) {
			adminRepository.destroy(id);
			
			logEntryRepository.create(format("Admin '%s (ID=%s)' blev slettet af '%s'.", admin.getName(), admin.getId(), request.getAttribute(REQUEST_CPR_PARAMETER)));
			
			response.sendRedirect("/admin/admins");
		}
	}


	private void getList(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("admin/list.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("admins", adminRepository.findAll());

		render(response, template, root);
	}


	@SuppressWarnings("unchecked")
	private void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("admin/new.ftl");

		// List the white listed firms (CVR).
		
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("firms", new ArrayList<String>(whitelist.keySet()));
		
		render(response, template, root);
	}


	private void getEdit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Template template = config.getTemplate("admin/edit.ftl");

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");
		Admin admin = adminRepository.find(id);
		root.put("admin", admin);
		
		render(response, template, root);
	}
}
