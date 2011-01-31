<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Ny Bruger</title>
	<link type="text/css" rel="stylesheet" href="${contextRoot}/style.css" />
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
		<h2>Ny Klient</h2>
		<form action="${contextRoot}/admin/clients" method="POST">
		<p>
			<label for="name">Navn:</label>
			<input name="name" type="text" size="60" />
		</p>
		<p>
			<label for="certificate_id">Certifikat ID:</label>
			<input name="certificate_id" type="text" size="60" />
		</p>
		<hr />
		<p>
			<input type="submit" value="Gem" />
		</p>
		</form>
	</div>
</body>
</html>  