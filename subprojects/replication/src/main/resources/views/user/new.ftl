<h2>Ny Administrator</h2>
<form action="${contextRoot}/admin/users" method="POST">
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
