<%@ page
	import="com.trifork.stamdata.importer.webinterface.DatabaseStatus"%>
<%@ page import="com.trifork.stamdata.importer.jobs.JobManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="com.google.inject.Injector"%>
<%@ page import="com.google.inject.Guice"%>
<%@ page import="java.util.*"%>
<%@ page import="com.trifork.stamdata.importer.jobs.*"%>
<%
	Injector injector = (Injector) pageContext.getServletContext().getAttribute(Injector.class.getName());
	boolean isDBAlive = injector.getInstance(DatabaseStatus.class).isAlive();
	JobManager manager = injector.getInstance(JobManager.class);
%>

<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta http-equiv="refresh" content="5" />
<link href="css/screen.css" rel="stylesheet" media="all" type="text/css" />
<title>Stamdata Importer Status</title>
</head>
<body>
	<div id="container">
		<div id="header">
			<img alt="" src="images/bg_logo_png.png" />
		</div>

		<h2>Komponentstatus:</h2>
		<%
			String status;
			String message;

			if (!isDBAlive)
			{
				status = "error";
				message = "Fejl. Kan ikke forbinde til databasen. Se logfilen.";
			}
			else if (manager.areAllJobsRunning())
			{
				status = "ok";
				message = "OK. Programmet kører stabilt.";
			}
			else
			{
				status = "error";
				message = "Fejl. Et job har fejlet. Se logfilen.";
			}
		%>

		<div class="<%=status%> job">
			<div class="right">
				(<a href="status">REST Status URL</a>)
			</div>
			<%=message%>
		</div>

		<%
			if (manager.areAnyJobsOverdue())
			{
		%>
		<div class="warning job">En eller flere parsere har ikke
			modtaget nye filer før sin deadline.</div>
		<%
			}
		%>

		<h2>Liste over processer:</h2>
		<%
			Iterator<Executer> jobs = manager.getJobIterator();

			while (jobs.hasNext())
			{
				Executer job = jobs.next();
				String jobStatus = "ok";

				if (job.isOverdue() || !job.hasBeenRun())
				{
					jobStatus = "warning";
				}
				if (job.isExecuting())
				{
					jobStatus = "executing";
				}
				else if (!job.isOK())
				{
					jobStatus = "error";
				}
		%>
		<div class="<%=jobStatus%> job">
			<div class="right">
				<%
					if (job.hasBeenRun())
						{
							out.print("Seneste udførsel: <b>" + job.getLatestRunTime().toString("dd.MM.yyyy") + "</b>");
						}
						else
						{
							out.print("Aldrig udført");
						}
				%>
			</div>
			<b><%=job.getHumanName()%></b>
		</div>
		<%
			}
		%>
	</div>
</body>
</html>
