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

package com.trifork.stamdata.webinterface;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.trifork.stamdata.ProjectInfo;
import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.FileImporterControlledIntervals;
import com.trifork.stamdata.spooler.FileSpoolerImpl;
import com.trifork.stamdata.spooler.SpoolerManager;
import com.trifork.stamdata.util.DateUtils;


/**
 * Status servlet for the importer, shows information about the running processes.
 *
 * @author Jan Buchholdt (jbu@trifork.com)
 * @author Thomas Børlum (thb@trifork.com)
 */
@Singleton
public class ImporterServlet extends HttpServlet {

	private static final long serialVersionUID = 2264195929113132612L;

	private final SpoolerManager spoolerManager;
	private final DatabaseStatus isAlive;
	private final ProjectInfo build;

	@Inject
	ImporterServlet(SpoolerManager spoolerManager, DatabaseStatus dbIsAlive, ProjectInfo projectInfo) {

		this.spoolerManager = spoolerManager;
		this.isAlive = dbIsAlive;
		this.build = projectInfo;
	}

	@Override
	public void init() throws ServletException {

		spoolerManager.start();
	}

	@Override
	public void destroy() {

		spoolerManager.stop();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if ("spoolers".equals(req.getParameter("isAlive"))) {
			isSpoolersAlive(spoolerManager, resp);
		}
		else if ("db".equals(req.getParameter("isAlive"))) {
			isDbAlive(resp);
		}
		else if (req.getParameter("history") != null) {
			importHistory(resp);
		}
		else {
			String rej = req.getParameter("rejectedFiles");
			String overdue = req.getParameter("overdue");

			if (rej != null) {
				rejectedFiles(resp, rej);
			}
			else if (overdue != null) {
				overdue(resp, overdue);
			}
			else {
				getServletContext().getRequestDispatcher("/jsp/showStatus.jsp").forward(req, resp);
			}
		}
	}

	private void overdue(HttpServletResponse resp, String type) throws IOException {

		ServletOutputStream os = resp.getOutputStream();

		try {
			FileSpoolerImpl spooler = spoolerManager.getSpooler(type);
			if (!spooler.isOverdue()) {
				os.print("SDM-" + build.getVersion() + "\nFile import for type: '" + type + "' is not overdue.");
			}
			else {
				resp.sendError(500, "SDM-" + build.getVersion() + "\nFile import for type: '" + type + "' is overdue! " + "Last import: " + DateUtils.toMySQLdate(spooler.getLastRun()) + " Next run was expected before: " + DateUtils.toMySQLdate(((FileImporterControlledIntervals) spooler.getImporter()).getNextImportExpectedBefore(spooler.getLastRun())));
			}

		}
		catch (IllegalArgumentException e) {
			resp.sendError(500, "SDM-" + build.getVersion() + "\nUsage: rejectedFiles=type  example types: takst, cpr, ... " + e.getMessage());
		}
	}

	private void rejectedFiles(HttpServletResponse resp, String type) throws IOException {

		ServletOutputStream os = resp.getOutputStream();

		try {
			if (spoolerManager.isRejectDirEmpty(type)) {
				os.print("SDM-" + build.getVersion() + "\nno files in rejected dir for type: '" + type + "'");
			}
			else {
				resp.sendError(500, "SDM-" + build.getVersion() + "\nrejected dirs contain rejected files!");
			}
		}
		catch (IllegalArgumentException e) {
			resp.sendError(500, "SDM-" + build.getVersion() + "\nUsage: rejectedFiles=type  example types: takst, cpr, ... " + e.getMessage());
		}
	}

	private void isDbAlive(HttpServletResponse resp) throws IOException {

		if (isAlive.isAlive()) {
			resp.getOutputStream().print("SDM-" + build.getVersion() + "\ndb connection is up");
		}
		else {
			resp.sendError(500, "SDM-" + build.getVersion() + "\ndb connection down");
		}
	}

	private void isSpoolersAlive(SpoolerManager manager, HttpServletResponse resp) throws IOException {

		if (manager.isAllSpoolersRunning()) {
			resp.getOutputStream().println("SDM-" + build.getVersion() + "\nall spoolers configured and running");
		}
		else {
			resp.sendError(500, "SDM-" + build.getVersion() + "\nOne or more spoolers are not running");
		}
	}

	private void importHistory(HttpServletResponse resp) throws IOException {

		PrintWriter writer = resp.getWriter();

		writer.print("<html><head><title>Import History</title></head><body><h1>Import History</h1>");

		Connection con = null;

		try {
			con = MySQLConnectionManager.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * from " + MySQLConnectionManager.getHousekeepingDBName() + ".Import");

			writer.print("<table>");

			while (rs.next()) {
				writer.print("<tr><td>" + rs.getTimestamp("importtime") + "</td><td>" + rs.getString("spoolername") + "</td></tr>");
			}

			writer.print("</table>");
		}
		catch (Exception e) {
			writer.print("<p>Cannot retrieve import stats.</p><pre>" + e.getMessage() + "</pre>");
		}
		finally {
			MySQLConnectionManager.close(con);
		}

		writer.print("</body></html>");
	}
}
