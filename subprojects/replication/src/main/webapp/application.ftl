<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8" />
	<title>Stamdata Register Service</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
	<script language="JavaScript" charset="UTF-8">
		function confirm_entry() {
			return confirm("Er du helt sikker på at du vil slette?");
		}
	</script>
</head>
<body>
	<div id="header">
		<div class="container">
		<ul id="nav">
			<li><a href="${contextRoot}/admin/clients">Serviceaftagere</a></li>
			<li><a href="${contextRoot}/admin/users">Administratorer</a></li>
			<li><a href="${contextRoot}/admin/log">Revisionslog</a></li>
		</ul>
		<span id="logo">Stamdata</span>
		</div>
	</div>
	<div id="content">
		${body}
	</div>
</body>
</html>  