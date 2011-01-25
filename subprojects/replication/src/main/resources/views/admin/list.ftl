<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Administratorer</title>
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
		<h2>Administratorer</h2>
		<a href="/admin/admins/new">Opret ny administrator</a>
		<ul id="users">
			<#list admins as admin>
			<li><a href="admins?id=${admin.id}">${admin.name}</a></li>
			</#list>
		</ul>
	</div>
</body>
</html>
