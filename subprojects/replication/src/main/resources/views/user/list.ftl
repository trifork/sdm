<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Klienter</title>
	<link type="text/css" rel="stylesheet" href="/style.css" /> 
</head>
<body>
	<div id="header">
		<ul id="nav">
			<li><a href="/admin/users">Klienter</a></li>
			<li><a href="/admin/admins">Administratorer</a></li>
			<li><a href="/admin/log">Audit Log</a></li>
		</ul>
		<span id="logo">SDM Replikering</span>
	</div>
	<div id="content">
		<h2>Klienter</h2>
		<a href="/admin/users/new">Opret ny klient</a>
		<ul id="users">
			<#list clients as client>
			<li><a href="/admin/users?id=${client.id}">${client.name}</a></li>
			</#list>
		</ul>
	</div>
</body>
</html>  