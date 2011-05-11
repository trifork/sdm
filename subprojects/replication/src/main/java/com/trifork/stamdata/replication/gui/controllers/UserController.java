
// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
import com.trifork.stamdata.replication.gui.models.User;
import com.trifork.stamdata.replication.gui.models.UserDao;
import com.trifork.stamdata.replication.logging.AuditLogger;


@Singleton
public class UserController extends AbstractController {

	private static final long serialVersionUID = 6245011626700765816L;

	private final Provider<UserDao> users;
	private final Provider<AuditLogger> audit;
	private final Provider<PageRenderer> renderer;

	private final Provider<User> currentUser;

	@Inject
	UserController(Provider<User> currentUser, Provider<UserDao> users, Provider<AuditLogger> audit, Provider<PageRenderer> renderer) {

		this.currentUser = currentUser;
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

		// Get the new administrator's info from the HTML form.

		String name = request.getParameter("name");
		String subjectSerialNumber = request.getParameter("subjectSerialNumber");

		// The CVR must be contained in the white list.

		User newUser = users.get().create(name, subjectSerialNumber);

		if (newUser != null) {
			audit.get().log("new_user=%s created by user=%s.", newUser, currentUser.get());
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
