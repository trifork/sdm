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
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.trifork.stamdata.replication.logging.DatabaseAuditLogger;


@Singleton
public class LogController extends AbstractController {

	private static final long serialVersionUID = -4877311712952056542L;

	private final Provider<DatabaseAuditLogger> audit;
	private final Provider<PageRenderer> renderer;

	@Inject
	LogController(Provider<DatabaseAuditLogger> audit, Provider<PageRenderer> renderer) {

		this.audit = audit;
		this.renderer = renderer;
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Map<String, Object> vars = new HashMap<String, Object>();

		int offset = 0;
		
		if (request.getParameter("offset") != null) {
			offset = Integer.parseInt(request.getParameter("offset"));
			if (offset < 0) offset = 0;
		}
		
		int count = 100;
		
		List<?> entries = audit.get().findAll(offset, count);
		
		vars.put("entries", entries);
		
		// If there are might be more log entries make a link.
		
		if (offset > 0) vars.put("prevOffset", offset - count - 1);
		if (entries.size() == count) vars.put("nextOffset", offset + count + 1);

		renderer.get().render("log/list.ftl", vars, request, response);
	}
}
