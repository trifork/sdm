<h2>Audit Log</h2>
<table id="log">
	<tr>
		<th>Tid</th>
		<th>Message</th>
	</tr>
	<#list entries as entry>
	<tr>
		<td>${entry.createdAt}</td>
		<td>${entry.message}</td>
	</tr>
	</#list>
</table>
