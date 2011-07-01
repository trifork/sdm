// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers.takst.model;

import com.trifork.stamdata.importer.model.Id;
import com.trifork.stamdata.importer.model.Output;


@Output
public class Firma extends TakstEntity
{

	private Long firmanummer; // Ref. t. LMS01, felt 13 - 14
	private String firmamaerkeKort; // P.t. tomt
	private String firmamaerkeLangtNavn;
	private String parallelimportoerKode;

	@Id
	@Output
	public Long getFirmanummer()
	{
		return this.firmanummer;
	}

	public void setFirmanummer(Long firmanummer)
	{
		this.firmanummer = firmanummer;
	}

	@Output
	public String getFirmamaerkeKort()
	{
		return this.firmamaerkeKort;
	}

	public void setFirmamaerkeKort(String firmamaerkeKort)
	{
		this.firmamaerkeKort = firmamaerkeKort;
	}

	@Output
	public String getFirmamaerkeLangtNavn()
	{
		return this.firmamaerkeLangtNavn;
	}

	public void setFirmamaerkeLangtNavn(String firmamaerkeLangtNavn)
	{
		this.firmamaerkeLangtNavn = firmamaerkeLangtNavn;
	}

	@Output
	public String getParallelimportoerKode()
	{
		return this.parallelimportoerKode;
	}

	public void setParallelimportoerKode(String parallelimportoerKode)
	{
		this.parallelimportoerKode = parallelimportoerKode;
	}

	public Long getKey()
	{
		return this.firmanummer;
	}

}
