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
public class TilskudsprisgrupperPakningsniveau extends TakstEntity
{

	private Long tilskudsprisGruppe;
	private Long varenummer; // Ref. t. LMS02

	@Output
	public Long getTilskudsprisGruppe()
	{
		return this.tilskudsprisGruppe;
	}

	public void setTilskudsprisGruppe(Long tilskudsprisGruppe)
	{
		this.tilskudsprisGruppe = tilskudsprisGruppe;
	}

	@Id
	@Output
	public Long getVarenummer()
	{
		return this.varenummer;
	}

	public void setVarenummer(Long varenummer)
	{
		this.varenummer = varenummer;
	}

	public Long getKey()
	{
		return varenummer;
	}

}
