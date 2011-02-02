<h2>Audit Log</h2>
<table id="log">
	<tr>
		<th>Tid</th>
		<th>Message</th>
	</tr>
	<#list entries as entry>
	<tr>
		<td>${entry.createdAt?datetime}</td>
		<td>${entry.message}</td>
	</tr>
	</#list>
</table>
