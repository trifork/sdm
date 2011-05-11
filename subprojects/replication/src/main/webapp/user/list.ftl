<div class="new_link">
	<img src="${contextRoot}/images/add.gif" /> <a href="${contextRoot}/admin/users/new">Opret ny Administrator</a>
</div>
<h2>Liste over Administratorer</h2>
<p id="info">
	Administratorer er personer der har tilladelse til tilgå denne administrationsside.
	De kan dermed også styre hvem der har adgang til hvilke registre.
	Alle administratorer har også mulighed for tilføje nye serviceaftagere og nye administratore.
</p>
<ul id="users">
	<#list users as user>
	<li><a href="${contextRoot}/admin/users?id=${user.id}">${user.name}</a></li>
	</#list>
</ul>
