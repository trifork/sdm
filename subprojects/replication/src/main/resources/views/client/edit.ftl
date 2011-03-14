<h2>Serviceaftager: ${client.name}</h2>
<form action="${contextRoot}/admin/clients?id=${client.id}" method="POST">
<input type="hidden" name="id" value="${client.id}" />
<p>
	<b>Cvr-nummer: ${client.cvrNumber}</b>
</p>
<p>
	<h3>Serviceaftager har adgang til:</h3><hr />
	<#list entities as e>
	<input type="checkbox" name="entity_${e}" ${permissions?seq_contains(e)?string("checked", "")} /> ${e} <br />
	</#list>
</p>
<hr />
<p>
	<input type="hidden" value="PUT" id="method" name="method" />
	<input type="submit" value="Gem" onClick="javacript: document.getElementById('method').value = 'PUT';" /> 
	<input class="delete" type="submit" value="Slet" onClick="javacript: document.getElementById('method').value = 'DELETE'; return confirm_entry();" />
</p>
</form>
