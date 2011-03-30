<div class="new_link">
	<img src="${contextRoot}/images/add.gif" /> <a href="${contextRoot}/admin/clients/new">Opret ny Serviceaftager</a>
</div>
<h2>Liste over Serviceaftager</h2>
<p id="info">
	Serviceaftagere er systemer der har tiladelse til at tilgå registre. Hvilke registre og data i de respektive registre
	en serviceaftager kan konfigureres ved at vælge navnet i listen herunder.  
</p>
<ul id="users">
	<#list clients as client>
	<li><a href="${contextRoot}/admin/clients?id=${client.id}">${client.name}</a></li>
	</#list>
</ul>
