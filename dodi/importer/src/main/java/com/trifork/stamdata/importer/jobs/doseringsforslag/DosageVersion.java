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

package com.trifork.stamdata.importer.jobs.doseringsforslag;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.*;


@Output
public class DosageVersion extends DosageRecord
{

	// daDate: Dato for Apotekerforeningens mærkevaretakst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date daDate;

	// lmsDate: Dato for Lægemiddelstyrelsens takst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date lmsDate;

	// Dato filen er released. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date releaseDate;

	// Unikt release nummer. Obligatorisk. Heltal, 15 cifre.
	protected long releaseNumber;

	@Output
	public Date getDaDate()
	{

		return daDate;
	}

	@Output
	public Date getLmsDate()
	{

		return lmsDate;
	}

	@Id
	@Output
	public Date getReleaseDate()
	{

		return releaseDate;
	}

	@Output
	public long getReleaseNumber()
	{

		return releaseNumber;
	}
}
