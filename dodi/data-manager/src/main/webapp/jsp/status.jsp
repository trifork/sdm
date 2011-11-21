<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.google.inject.Injector"%>
<%@ page import="com.trifork.stamdata.importer.config.ConnectionManager" %>
<%@ page import="com.trifork.stamdata.importer.webinterface.DataManagerComponentMonitor" %>
<%@ page import="com.trifork.stamdata.importer.parsers.ParserState" %>
<%
	Injector injector = (Injector) pageContext.getServletContext().getAttribute(Injector.class.getName());
    DataManagerComponentMonitor monitor = injector.getInstance(DataManagerComponentMonitor.class);
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
		<%
			String status;
			String message;

			if (!monitor.isDatabaseAvailable())
			{
				status = "error";
				message = "Fejl. Kan ikke forbinde til databasen. Se logfilen.";
			}
			else if (monitor.areAllJobsRunning())
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
			if (monitor.areAnyJobsOverdue())
			{
		%>
		<div class="warning job">En eller flere parsere har ikke
			modtaget nye filer inden deadlinen udløb.</div>
		<%
			}
		%>

		<h2>Parser Status</h2>
		<%
			for (ParserState job : monitor.getJobs())
			{
				String jobStatus = "ok";

				if (job.isOverdue() || !job.hasBeenRun())
				{
					jobStatus = "warning";
				}

                if (job.isInProgress())
				{
					jobStatus = "executing";
				}
				else if (job.isLocked())
				{
					jobStatus = "error";
				}
		%>
		<div class="<%=jobStatus%> job">
			<div class="right">
				<%
                if (job.hasBeenRun())
                {
                    out.print("Seneste kørsel: <b>" + job.latestRunTime().toString("dd.MM.yyyy") + "</b>");
                }
                else
                {
                    out.print("Aldrig kørt");
                }
				%>
			</div>
			<b><%=job.name()%></b>
		</div>
		<%
			}
		%>
	</div>
</body>
</html>
