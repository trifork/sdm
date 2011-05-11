<h2>Ny Serviceaftager</h2>
<form action="${contextRoot}/admin/clients" method="POST">
<p>
	<label for="name">Systemnavn:</label>
	<span>Dette felt bruges kun som reference og behøver ikke matche CVR-nummeret.</span>
	<input name="name" type="text" size="60" />
</p>
<p>
	<label for="certificate_id">VOCES Subject Serial Number:</label>
	<div>
		NB. Dette felt skal matche Subject SerialNumber i clientens virksomhedscertifikat.<br />
		ADVARSEL: Hvis der anvendes DGWS som sikkerhed må der ikke registreres flere klienter med samme CVR-nummer.<br />
		<b>F.eks. CVR:1234568-UID:1234</b>
	</div>
	<input name="certificate_id" type="text" size="80" />
</p>
<hr />
<p>
	<input type="submit" value="Gem" />
</p>
</form>
