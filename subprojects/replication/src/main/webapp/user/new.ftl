<h2>Opret Ny Administrator</h2>
<form action="${contextRoot}/admin/users" method="POST">
<p>
	<label for="name">Navn:</label>
	<span>
		Dette felt bruges udelukkende som refernece og behøver ikke matche
		navnet i certifikatet.
	</span>
	<input name="name" type="text" size="81" />
</p>
<p>
	<label for="subjectSerialNumber">MOCES Subject Serial Number:</label>
	<span>
		Subject Serial Number fra det certifikat brugeren skal<br />
		bruge for at logge ind, f.eks. <strong>CVR:12345678-RID:12345678</strong>.
	</span>
	<input name="subjectSerialNumber" type="text" size="81" />
</p>
<hr />
<p>
	<input type="submit" value="Gem" />
</p>
</form>
