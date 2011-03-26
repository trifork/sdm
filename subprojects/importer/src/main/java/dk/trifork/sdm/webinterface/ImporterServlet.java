package dk.trifork.sdm.webinterface;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.trifork.sdm.ProjectInfo;
import dk.trifork.sdm.config.Configuration;
import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.spooler.FileSpoolerImpl;
import dk.trifork.sdm.spooler.SpoolerManager;
import dk.trifork.sdm.util.DateUtils;


/**
 * Status servlet for the importer.
 *
 * @author Jan Buchholdt (jbu@trifork.com)
 */
public class ImporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private SpoolerManager manager = null;
	private DbIsAlive isAlive;
	private ProjectInfo build;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		if ("spoolers".equals(req.getParameter("isAlive"))) {
			isSpoolersAlive(manager, resp);
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
			FileSpoolerImpl spooler = manager.getSpooler(type);
			if (!spooler.isOverdue())
				os.print("SDM-" + build.getVersion() + "\nFile import for type: '" + type + "' is not overdue.");
			else
				resp.sendError(500, "SDM-" + build.getVersion() + "\nFile import for type: '" + type + "' is overdue! " + "Last import: " + DateUtils.toMySQLdate(spooler.getLastRun()) + " Next run was expected before: " + DateUtils.toMySQLdate(((FileImporterControlledIntervals) spooler.getImporter()).getNextImportExpectedBefore(spooler.getLastRun())));

		}
		catch (IllegalArgumentException e) {
			resp.sendError(500, "SDM-" + build.getVersion() + "\nUsage: rejectedFiles=type  example types: takst, cpr, ... " + e.getMessage());
		}
	}

	private void rejectedFiles(HttpServletResponse resp, String type) throws IOException {

		ServletOutputStream os = resp.getOutputStream();
		
		try {
			if (manager.isRejectDirEmpty(type)) {
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

		if (isAlive.isDbAlive()) {
			resp.getOutputStream().print("SDM-" + build.getVersion() + "\ndb connection is up");
		}
		else {
			resp.sendError(500, "SDM-" + build.getVersion() + "\ndb connection down");
		}
	}

	private void isSpoolersAlive(SpoolerManager manager, HttpServletResponse resp) throws IOException {

		if (manager.isAllSpoolersRunning())
			resp.getOutputStream().println("SDM-" + build.getVersion() + "\nall spoolers configured and running");
		else
			resp.sendError(500, "SDM-" + build.getVersion() + "\nOne or more spoolers are not running");
	}

	@Override
	public void destroy() {

		super.destroy();
		manager.destroy();
		manager = null;
	}

	@Override
	public void init() throws ServletException {

		super.init();
		manager = new SpoolerManager(Configuration.getString("spooler.rootdir"));
		isAlive = new DbIsAlive();
		build = new ProjectInfo(getServletConfig().getServletContext());
		getServletContext().setAttribute("manager", manager);
		getServletContext().setAttribute("dbstatus", isAlive);
		getServletContext().setAttribute("build", build);
	}


	public class DbIsAlive {

		public boolean isDbAlive() {

			boolean isAlive = false;
			Connection con = null;
			try {
				con = MySQLConnectionManager.getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1");
				rs.next();
				if (1 == rs.getInt(1)) isAlive = true;
			}
			catch (Exception e) {
				logger.error("db connection down", e);
			}
			finally {
				MySQLConnectionManager.close(con);
			}
			return isAlive;
		}
	}

	private void importHistory(HttpServletResponse resp) throws IOException {

		PrintWriter writer = resp.getWriter();

		writer.print("<html><head><title>Import History</title></head><body><h1>Import History</h1>");

		Connection con = null;

		try {
			con = MySQLConnectionManager.getConnection();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * from Import");

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
