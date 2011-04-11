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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.gui.annotations.Whitelist;
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.replication.logging.AuditLogger;


@Singleton
public class UserController extends AbstractController {

	private static final long serialVersionUID = 6245011626700765816L;

	private final Provider<UserDao> users;
	private final Map<String, String> whitelist;
	private final Provider<AuditLogger> audit;
	private final Provider<PageRenderer> renderer;

	private final Provider<User> currentUser;

	@Inject
	UserController(Provider<User> currentUser, @Whitelist Map<String, String> whitelist, Provider<UserDao> users, Provider<AuditLogger> audit, Provider<PageRenderer> renderer) {

		this.currentUser = currentUser;
		this.whitelist = whitelist;
		this.users = users;
		this.audit = audit;
		this.renderer = renderer;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// DISPATCH THE REQUEST

		if (request.getRequestURI().endsWith("/new")) {
			getNew(request, response);
		}
		else if (request.getParameter("id") == null) {

			// If the ID parameter is null, we list all
			// users. If it is present we show a specific user.

			list(request, response);
		}
		else {
			edit(request, response);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// DISPATCH THE REQUEST
		//
		// There is a 'method' field on the HTML page
		// that helps simulate the DELETE method.

		String method = request.getParameter("method");

		if ("DELETE".equals(method)) {
			getDelete(request, response);
		}
		else {
			getCreate(request, response);
		}
	}

	protected void getCreate(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// Get the new administrator's info from the
		// HTML form.

		String newUserName = request.getParameter("name");
		String newUserCPR = request.getParameter("cpr");
		String firm = request.getParameter("firm");

		// The CVR must be contained in the white list.

		if (whitelist.containsKey(firm)) {
			String newUserCVR = whitelist.get(firm);

			User user = users.get().create(newUserName, newUserCPR, newUserCVR);

			if (user != null) {
				audit.get().log("new_user=%s created by user=%s.", user, currentUser.get());
			}
		}

		redirect(request, response, "/admin/users");
	}

	protected void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");

		User deletedUser = users.get().find(id);
		User user = currentUser.get();

		if (!deletedUser.equals(null) && !deletedUser.equals(user)) {
			users.get().delete(id);
			audit.get().log("user=%s, deleted_user=%s", user, deletedUser);
		}

		redirect(request, response, "/admin/users");
	}

	protected void list(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("users", users.get().findAll());

		renderer.get().render("/user/list.ftl", root, request, response);
	}

	protected void getNew(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		// List the white listed firms (CVR).

		Map<String, Object> root = new HashMap<String, Object>();

		root.put("firms", whitelist.keySet());

		renderer.get().render("/user/new.ftl", root, request, response);
	}

	protected void edit(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		Map<String, Object> root = new HashMap<String, Object>();

		String id = request.getParameter("id");

		User user = users.get().find(id);
		root.put("user", user);

		renderer.get().render("/user/edit.ftl", root, request, response);
	}
}
