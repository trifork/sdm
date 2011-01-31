<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Administrator ${user.name}</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
	<script language="JavaScript">
		function confirm_entry() {
		
			return confirm("Er du sikker på at du vil slette denne admin?");
		}
	</script>
</head>
<body>
	<div id="header">
		<ul id="nav">
			<li><a href="${contextRoot}/admin/clients">Klienter</a></li>
			<li><a href="${contextRoot}/admin/users">Administratorer</a></li>
			<li><a href="${contextRoot}/admin/log">Audit Log</a></li>
		</ul>
		<span id="logo">SDM Replikering</span>
	</div>
	<div id="content">
		<h2>Admin: ${user.name}</h2>
		<form action="${contextRoot}/admin/users?id=${admin.id}" method="POST">
		<input type="hidden" name="id" value="${admin.id}" />
		<input type="hidden" name="action" value="delete" />
		<p>
			CPR-nummer: ${user.cpr}
		</p>
		<p>
			CVR-nummer: ${user.cvr}
		</p>
		<hr />
		<p>
			<input class="delete" type="submit" value="Slet" onClick="javacript: return confirm_entry();" />
		</p>
		</form>
	</div>
</body>
</html>  