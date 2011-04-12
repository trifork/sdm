<h2>Opret Ny Administrator</h2>
<form action="${contextRoot}/admin/users" method="POST">
<p id="info">
	Det er vigtigt at CPR- og CVR-nummer passer sammen og matcher
	til det medarbejder certifikat som er tilknyttet brugeren.
</p>
<p>
	<label for="name">Navn:</label>
	<span>
		Dette felt bruges udelukkende som refernece og behøver ikke matche
		CPR-nummeret.
	</span>
	<input name="name" type="text" size="60" />
</p>
<p>
	<label for="cpr">CPR-nummer:</label>
	<input name="cpr" type="text" size="60" />
</p>
<p>
	<label for="firm">Organisation (CVR):</label>
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
