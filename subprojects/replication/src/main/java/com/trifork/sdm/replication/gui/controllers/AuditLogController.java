package com.trifork.sdm.replication.gui.controllers;


import static com.trifork.sdm.replication.db.properties.Database.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trifork.sdm.replication.db.properties.Transactional;
import com.trifork.sdm.replication.gui.models.AuditLog;


@Singleton
public class AuditLogController extends AbstractController {

	private static final long serialVersionUID = -4877311712952056542L;

	private final AuditLog auditLog;
	private final PageRenderer renderer;


	@Inject
	public AuditLogController(AuditLog auditLog, PageRenderer renderer) {

		this.auditLog = auditLog;
		this.renderer = renderer;
	}


	@Override
	@Transactional(ADMINISTRATION)
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			Map<String, Object> root = new HashMap<String, Object>();

			root.put("entries", auditLog.all());

			renderer.render("log/list.ftl", root, request, response);
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
	}
}
