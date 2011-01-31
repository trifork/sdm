<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Stamdata Replikering</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
	<script language="JavaScript" charset="UTF-8">
		function confirm_entry()
		{
			return confirm("Er du helt sikker?");
		}
	</script>
</head>
<body>
	<div id="header">
		<ul id="nav">
			<li><a href="${contextRoot}/admin/clients">Klienter</a></li>
			<li><a href="${contextRoot}/admin/users">Administratorer</a></li>
			<li><a href="${contextRoot}/admin/log">Log</a></li>
		</ul>
		<span id="logo">Stamdata Replikering</span>
	</div>
	<div id="content">
		${body}
	</div>
</body>
</html>  