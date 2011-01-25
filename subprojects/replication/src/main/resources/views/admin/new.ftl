<!DOCTYPE html>
<html>
<head>
	<title>SDM Replikering – Ny Administrator</title>
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
		<h2>Ny Administrator</h2>
		<form action="/admin/admins" method="POST">
		<p>
			<label for="name">Navn:</label>
			<input name="name" type="text" size="60" />
		</p>
		<p>
			<label for="cpr">CPR-nummer:</label>
			<input name="cpr" type="text" size="60" />
		</p>
		<p>
			<label for="firm">Organisation:</label>
			<select name="firm">
				<#list firms as firm>
				<option value="${firm}">${firm}</option>
				</#list>
			</select>
		</p>
		<hr />
		<p>
			<input type="submit" value="Gem" />
		</p>
		</form>
	</div>
</body>
</html>  