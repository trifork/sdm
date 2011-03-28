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
import com.trifork.stamdata.replication.util.DatabaseAuditLogger;


@Singleton
public class UserController extends AbstractController {

	private static final long serialVersionUID = 6245011626700765816L;

	private final Provider<UserDao> users;
	private final Map<String, String> whitelist;
	private final Provider<DatabaseAuditLogger> audit;
	private final Provider<PageRenderer> renderer;

	private final Provider<User> currentUser;

	@Inject
	UserController(Provider<User> currentUser, @Whitelist Map<String, String> whitelist, Provider<UserDao> users, Provider<DatabaseAuditLogger> audit, Provider<PageRenderer> renderer) {

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
				audit.get().write("New administrator created (new_user_cpr=%s, new_user_cvr=%s). Created by user_cpr=%s.", newUserCPR, newUserCVR, currentUser.get().getCpr());
			}
		}

		redirect(request, response, "/admin/users");
	}

	protected void getDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String id = request.getParameter("id");

		User deletedUser = users.get().find(id);

		if (deletedUser != null) {
			String userCPR = currentUser.get().getCpr();
			users.get().delete(id);
			audit.get().write("Administrator '%s (ID=%s)' was deleted by user %s.", deletedUser.getName(), deletedUser.getId(), userCPR);
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
