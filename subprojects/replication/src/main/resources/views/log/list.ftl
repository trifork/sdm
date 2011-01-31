<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Brugere</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
</head>
<body>
	<div id="header">
		<ul id="nav">
			<li><a href="${contextRoot}/admin/users">Klienter</a></li>
			<li><a href="${contextRoot}/admin/admins">Administratorer</a></li>
			<li><a href="${contextRoot}/admin/log">Audit Log</a></li>
		</ul>
		<span id="logo">SDM Replikering</span>
	</div>
	<div id="content">
		<h2>Audit Log</h2>
		<table id="log">
			<tr>
				<th>Tid</th>
				<th>Message</th>
			</tr>
			<#list entries as entry>
			<tr>
				<td>${entry.createdAt}</td>
				<td>${entry.message}</td>
			</tr>
			</#list>
		</table>
	</div>
</body>
</html>  