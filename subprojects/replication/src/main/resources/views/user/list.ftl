<h2>Administratorer</h2>
<a href="${contextRoot}/admin/users/new">Opret ny administrator</a>
<ul id="users">
	<#list users as user>
	<li><a href="${contextRoot}/admin/users?id=${user.id}">${user.name}</a></li>
	</#list>
</ul>
