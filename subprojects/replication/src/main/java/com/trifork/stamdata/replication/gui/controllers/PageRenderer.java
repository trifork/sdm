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

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.inject.Inject;
import freemarker.template.*;


public class PageRenderer {

	protected final Configuration templates;

	@Inject
	PageRenderer(Configuration templates) {

		this.templates = templates;
	}

	protected void render(String templatePath, Map<String, Object> vars, HttpServletRequest request, HttpServletResponse response) throws IOException {

		response.setContentType("text/html; charset=utf-8");

		if (vars == null) {
			vars = new HashMap<String, Object>();
		}

		vars.put("contextRoot", request.getContextPath());

		try {
			Template page = templates.getTemplate(templatePath, "UTF-8");

			StringWriter bodyWriter = new StringWriter();
			page.process(vars, bodyWriter);
			vars.put("body", bodyWriter.toString());
			bodyWriter.close();

			Template html = templates.getTemplate("application.ftl");

			PrintWriter w = response.getWriter();
			html.process(vars, w);
		}
		catch (TemplateException e) {
			throw new IOException(e);
		}
	}
}
