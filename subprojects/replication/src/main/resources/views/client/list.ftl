<h2>Klienter</h2>
<a href="${contextRoot}/admin/clients/new">Opret ny klient</a>
<ul id="users">
	<#list clients as client>
	<li><a href="${contextRoot}/admin/clients?id=${client.id}">${client.name}</a></li>
	</#list>
</ul>
