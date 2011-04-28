<h2>Admin: ${user.name}</h2>
<form action="${contextRoot}/admin/users?id=${user.id}" method="POST">
<input type="hidden" name="id" value="${user.id}" />
<p>
	CPR-nummer: ${user.cpr}
</p>
<p>
	CVR-nummer: ${user.cvr}
</p>
<p>
	RID: ${user.rid}
</p>
<hr />
<p>
	<input type="hidden" name="method" value="DELETE" />
	<input class="delete" type="submit" value="Slet" onClick="javacript: return confirm_entry();" />
</p>
</form>
