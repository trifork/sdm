<h2>Ny Klient</h2>
<form action="${contextRoot}/admin/clients" method="POST">
<p>
	<label for="name">Systemnavn:</label>
	<input name="name" type="text" size="60" />
</p>
<p>
	<label for="certificate_id">CVR-nummer:</label>
	<input name="certificate_id" type="text" size="60" />
</p>
<hr />
<p>
	<input type="submit" value="Gem" />
</p>
</form>
