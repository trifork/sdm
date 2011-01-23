<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META http-equiv="refresh" content="5">
<link href="css/spooler.css" rel="stylesheet" media="all"
	type="text/css" />
<title>Stamdata Server Status</title>
</head>
<body>
<div class="centered">Version: ${build.version} Built-Date:
${build.buildInfo} - ${build.deployType} Vendor: ${build.vendor} Title:
${build.title}
<div class="main">
<div class="header"><c:choose>
	<c:when
		test="${dbstatus.dbAlive and manager.allRejectedDirsEmpty and manager.allSpoolersRunning}">
		<td class="success"><img class="header" src="images/success.png" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when
				test="${dbstatus.dbAlive and not manager.allRejectedDirsEmpty and manager.allSpoolersRunning}">
				<td class="warning"><img class="header" src="images/alertO.png" />
			</c:when>
			<c:otherwise>
				<td class="failure"><img class="header" src="images/failed.png" />
			</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose> Stamdata Server Status</div>
<table style="width: 700px" class="element">
	<tr>
		<td><c:choose>
			<c:when test="${dbstatus.dbAlive}">
				<td class="success" align="left"><img class="header"
					src="images/success.png" /> Database is RUNNING</td>
			</c:when>
			<c:otherwise>
				<td class="failure" align="left"><img class="header"
					src="images/failed.png" /> Database is DOWN</td>
			</c:otherwise>
		</c:choose></td>
	</tr>
	<tr>
		<td><c:choose>
			<c:when test="${manager.allSpoolersRunning}">
				<td class="success" align="left"><img class="header"
					src="images/success.png" /> Spoolers are RUNNING</td>
			</c:when>
			<c:otherwise>
				<td class="failure" align="left"><img class="header"
					src="images/failed.png" /> Some spoolers are DOWN</td>
			</c:otherwise>
		</c:choose></td>
	</tr>
	<tr>
		<td><c:choose>
			<c:when test="${manager.allRejectedDirsEmpty}">
				<td class="success" align="left"><img class="header"
					src="images/success.png" /> No rejected files</td>
			</c:when>
			<c:otherwise>
				<td class="warning" align="left"><img class="header"
					src="images/alertO.png" /> Some rejected files. See details below</td>
			</c:otherwise>
		</c:choose></td>
	</tr>
	<tr>
		<td><c:choose>
			<c:when test="${manager.noOverdueImports}">
				<td class="success" align="left"><img class="header"
					src="images/success.png" /> No imports are overdue</td>
			</c:when>
			<c:otherwise>
				<td class="warning" align="left"><img class="header"
					src="images/alertO.png" /> Some imports are overdue. See details
				below</td>
			</c:otherwise>
		</c:choose></td>
	</tr>
</table>
</div>

</div>
<div class="space"></div>

<table class="main">
	<tr class="header">
		<td><c:choose>
			<c:when
				test="${manager.allRejectedDirsEmpty and manager.allSpoolersRunning}">
				<td class="success"><img class="header"
					src="images/success.png" />
			</c:when>
			<c:otherwise>
				<c:choose>
					<c:when
						test="${not manager.allRejectedDirsEmpty and manager.allSpoolersRunning}">
						<td class="warning"><img class="header"
							src="images/alertO.png" />
					</c:when>
					<c:otherwise>
						<td class="failure"><img class="header"
							src="images/failed.png" />
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose> Consolidated Spooler Status</td>
	</tr>
	<tr>
		<td>
		<table style="width: 700px" class="element">
			<c:forEach var="spooler" items="${manager.spoolers}">
				<tr>
					<c:choose>
						<c:when test="${spooler.value.status eq 'RUNNING'}">
							<td class="success" align="left"><img class="header"
								src="images/success.png" /> Status for Spooler ${spooler.key}
							is ${spooler.value.status}.</td>
							<tr>
								<td class="activity">&nbsp;&nbsp;&nbsp; Current activity
								${spooler.value.activity} files. <c:choose>
									<c:when
										test="${spooler.value.activity eq 'IMPORTING' or spooler.value.activity eq 'STABILIZING'}">
										<img width="20" height="20" src="images/Files.jpg" />
									</c:when>
									<c:otherwise>
										<br />&nbsp;&nbsp;&nbsp; Last import: ${spooler.value.lastImportFormatted} 
											<br />&nbsp;&nbsp;&nbsp; Next import expected before: ${spooler.value.nextImportExpectedBeforeFormatted}
										</c:otherwise>
								</c:choose></td>
							</tr>
							<c:if test="${not spooler.value.rejectedDirEmpty}">
								<tr>
									<td class="warning">&nbsp;&nbsp;&nbsp; <img class="header"
										src="images/alertO.png" /> files exist in
									${spooler.value.setup.rejectPath}</td>
								</tr>
							</c:if>
						</c:when>
						<c:when test="${spooler.value.status eq 'INITIATING'}">
							<td class="warning" align="left"><img class="header"
								src="images/alertO.png" /> Status for Spooler ${spooler.key} is
							${spooler.value.status}.</td>
						</c:when>
						<c:otherwise>
							<td class="failure" align="left"><img class="header"
								src="images/failed.png" /> Status for Spooler ${spooler.key} is
							${spooler.value.status}.</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
			<c:forEach var="job" items="${manager.jobSpoolers}">
				<tr>
					<c:choose>
						<c:when test="${job.value.status eq 'RUNNING'}">
							<td class="success" align="left"><img class="header"
								src="images/success.png" /> Status for Job ${job.key} is
							${job.value.status}.</td>
							<tr>
								<td class="activity">&nbsp;&nbsp;&nbsp; Current activity
								${job.value.activity}. <br />
								&nbsp;&nbsp;&nbsp; Last run: ${job.value.lastRunFormatted}</td>
							</tr>
						</c:when>
						<c:when test="${job.value.status eq 'INITIATING'}">
							<td class="warning" align="left"><img class="header"
								src="images/alertO.png" /> Status for Job ${job.key} is
							${job.value.status}.</td>
						</c:when>
						<c:otherwise>
							<td class="failure" align="left"><img class="header"
								src="images/failed.png" /> Status for Job ${job.key} is
							${job.value.status}.</td>
						</c:otherwise>
					</c:choose>
				</tr>
			</c:forEach>
		</table>
		</td>
	</tr>
</table>
<div class="space"></div>
<table>
	<tr>
		<td>Links to tests</td>
		<td></td>
	</tr>
	<tr>
		<td></td>
		<td><a href="?isAlive=db">isAlive=db</a></td>
	</tr>
	<tr>
		<td></td>
		<td><a href="?isAlive=spoolers">isAlive=spoolers</a></td>
	</tr>
	<c:forEach var="spooler" items="${manager.spoolers}">
		<tr>
			<td><a href="?overdue=${spooler.key}">overdue=${spooler.key}</a></td>
			<td><a href="?rejectedFiles=${spooler.key}">rejectedFiles=${spooler.key}</a></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>
