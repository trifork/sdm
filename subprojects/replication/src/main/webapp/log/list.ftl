<h2>Revisionslog</h2>
<p id="info">
	Loggen viser alle forespørgsler samt alle oprettelser og nedlæggelser af serviceaftagere og administratorer.
</p>
<table id="log">
	<tr>
		<th>Tid</th>
		<th>Indtog</th>
	</tr>
	<#list entries as entry>
	<tr>
		<td class="timestamp">${entry.createdAt?datetime}</td>
		<td>${entry.message}</td>
	</tr>
	</#list>
	<#if prevOffset??>
		<a style="float:left;" href="${contextRoot}/admin/log?offset=${prevOffset}">« Forige Side</a>
	</#if>
	<#if nextOffset??>
		<a style="float:right;" href="${contextRoot}/admin/log?offset=${nextOffset}">Næste Side »</a>
	</#if>
</table>
