<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Administrator ${admin.name}</title>
	<link type="text/css" rel="stylesheet" href="/style.css" />
	<script language="JavaScript">
		function confirm_entry() {
		
			return confirm("Er du sikker på at du vil slette denne admin?");
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
		<h2>Admin: ${admin.name}</h2>
		<form action="/admin/admins?id=${admin.id}" method="POST">
		<input type="hidden" name="id" value="${admin.id}" />
		<input type="hidden" name="action" value="delete" />
		<p>
			CPR-nummer: ${admin.cpr}
		</p>
		<p>
			CVR-nummer: ${admin.cvr}
		</p>
		<hr />
		<p>
			<input class="delete" type="submit" value="Slet" onClick="javacript: return confirm_entry();" />
		</p>
		</form>
	</div>
</body>
</html>  