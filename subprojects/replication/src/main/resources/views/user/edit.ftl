<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Rediger Klient ${client.name}</title>
	<link type="text/css" rel="stylesheet" href="/style.css" />
	<script language="JavaScript">
		function confirm_entry() {
		
			return confirm("Er du sikker på at du vil slette denne klient?");
		}
	</script>
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
		<h2>Klient: ${client.name}</h2>
		<form action="/admin/users?id=${client.id}" method="POST">
		<input type="hidden" name="id" value="${client.id}" />
		<p>
			<b>Certifikat ID: ${client.certificateId}</b>
		</p>
		<p>
			<strong>Kan replikere:</strong><br />
			<#list resources as resource>
			<input type="checkbox" name="resource_${resource}" ${permissions?seq_contains(resource)?string("checked", "")} /> ${resource} <br />
			</#list>
		</p>
		<hr />
		<p>
			<input type="hidden" name="action" value="update" id="action" />
			<input type="submit" value="Gem" onClick="javacript: document.getElementById('action').value = 'update';" /> 
			<input class="delete" type="submit" value="Slet" onClick="javacript: document.getElementById('action').value = 'delete'; return confirm_entry();" />
		</p>
		</form>
	</div>
</body>
</html>  