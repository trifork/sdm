<%@page import="com.trifork.stamdata.ProjectInfo"%>
<%@ page import="com.trifork.stamdata.webinterface.DatabaseStatus"%>
<%@ page import="com.trifork.stamdata.spooler.SpoolerManager"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.google.inject.Injector"%>
<%@ page import="com.google.inject.Guice"%>
<%@ page import="java.util.*" %>
<%@ page import="com.trifork.stamdata.spooler.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	Injector injector = (Injector) pageContext.getServletContext().getAttribute(Injector.class.getName());
	boolean dbstatus = injector.getInstance(DatabaseStatus.class).isAlive();
	SpoolerManager manager = injector.getInstance(SpoolerManager.class);
	ProjectInfo build = injector.getInstance(ProjectInfo.class);
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<meta http-equiv="refresh" content="5" />
	<link href="css/spooler.css" rel="stylesheet" media="all" type="text/css" />
	<title>Stamdata Importer Status</title>
	</head>
	<body>
	<div id="top_bar">
	Version: <%= build.getVersion() %> |
	Built-Date: <%= build.getBuildInfo() + " - " + build.getDeployType()  %> |
	Vendor: <%= build.getVendor() %> |
	Title: <%= build.getTitle() %>
	</div>

	<div class="centered">

	<table class="main">
	<tr class="header">
	
	<% if (dbstatus && manager.isAllRejectedDirsEmpty() && manager.isAllSpoolersRunning()) { %>
		<td class="success"><img class="header" src="images/success.png" />
	<% } else if (dbstatus && !manager.isAllRejectedDirsEmpty() && manager.isAllSpoolersRunning()) { %>
		<td class="warning"><img class="header" src="images/alertO.png" />
	<% } else { %>
		<td class="failure"><img class="header" src="images/failed.png" />
	<% } %>
		Importer Status
	</td>
	
	</tr>
	<tr>
	<td>
	<table width="700" class="element">
	<tr>
		<% if (dbstatus) { %>
				<td class="success" align="left">
				<img class="header" src="images/success.png" /> Database is RUNNING
				</td>
		<% } else { %>
				<td class="failure" align="left">
				<img class="header" src="images/failed.png" /> Database is DOWN					
				</td>
		<% } %>
	</tr>	
	<tr>
		<% if (manager.isAllSpoolersRunning()) { %>
				<td class="success" align="left">
				<img class="header" src="images/success.png" /> Spoolers are RUNNING
				</td>
		<% } else { %>
				<td class="failure" align="left">
				<img class="header" src="images/failed.png" /> Some spoolers are DOWN					
				</td>
		<% } %>
	</tr>	
	<tr>
		<% if (manager.isAllRejectedDirsEmpty()) { %>
				<td class="success" align="left">
				<img class="header" src="images/success.png" /> No rejected files
				</td>
		<% } else { %>
				<td class="warning" align="left">
				<img class="header" src="images/alertO.png" /> Some rejected files. See details below					
				</td>
		<% } %>
	</tr>
	<tr>
		<% if (manager.isNoOverdueImports()) { %>
				<td class="success" align="left">
				<img class="header" src="images/success.png" /> No imports are overdue
				</td>
		<% } else { %>
				<td class="warning" align="left">
				<img class="header" src="images/alertO.png" /> Some imports are overdue. See details below					
				</td>
		<% } %>
	</tr>
	<tr>
		<td>
			<a href="?isAlive=db">REST DB-Status Check</a> | <a href="?isAlive=spoolers">REST All Jobs Check</a>
		</td>
	</tr>
	</table>	
	</td></tr>
	</table>
	</div>
	<div class="space"></div>
	 
	<table class="main">
		<tr class="header">
		<% if (manager.isAllRejectedDirsEmpty() && manager.isAllSpoolersRunning()) { %>
			<td class="success"><img class="header" src="images/success.png" />
		<% } else if (!manager.isAllRejectedDirsEmpty() && manager.isAllSpoolersRunning()) { %>
			<td class="warning"><img class="header" src="images/alertO.png" />
		<% } else { %>
			<td class="failure"><img class="header" src="images/failed.png" />
		<% } %>
			Jobs
		</td>
		</tr>
		<tr><td>	
			<table width="700" class="element">
				<% for (FileSpoolerImpl spooler : manager.getSpoolers().values()) { %>
				<tr>
					<% if (!spooler.getStatus().equals("ERROR")) { %>
						<td class="success" align="left">
						<img class="header" src="images/success.png" />
					<% } else { %>
						<td class="failure" align="left">
						<img class="header" src="images/failed.png" />
					<% } %>
					<%= spooler.getName() %> (<%= spooler.getStatus() %>, <%= spooler.getActivity() %>)<br />
					Last import: <%= spooler.getLastImportFormatted() %> <br />
					Next import expected before: <%= spooler.getNextImportExpectedBeforeFormatted() %><br />
					<a href="?overdue=<%= spooler.getName() %>">Overdue Check</a> |
					<a href="?rejectedFiles=<%= spooler.getName() %>">Rejected Check</a><br />
					<hr />
				</td>
				</tr>
				<% } %>

				<% for (JobSpoolerImpl spooler : manager.getJobSpoolers().values()) { %>
				<tr>
					<% if (!spooler.getStatus().equals("ERROR")) { %>
						<td class="success" align="left">
							<img class="header" src="images/success.png" />
					<% } else { %>
						<td class="failure" align="left">
							<img class="header" src="images/failed.png" />
					<% } %>
					<%= spooler.getName() %> (<%= spooler.getStatus() %>, <%= spooler.getActivity() %>)<br />
					<hr />
				</td>
				</tr>
				<% } %>
			 </table>
		 </td></tr>
	 </table>
	</body>
</html>
