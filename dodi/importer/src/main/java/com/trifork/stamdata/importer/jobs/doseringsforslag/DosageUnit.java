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

import com.trifork.stamdata.importer.persistence.*;


@Output
public class DosageUnit extends DosageRecord
{

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Unik kode for doseringsenheden. Obligatorisk. Heltal, 4 cifre.
	private int code;

	// Doseringenhedens tekst i ental. Obligatorisk. Streng, 100 tegn.
	private String textSingular;

	// Doseringsenhedens tekst i flertal. Obligatorisk. Streng, 100 tegn.
	private String textPlural;

	@Id
	@Output
	public int getCode()
	{

		return code;
	}

	@Output
	public long getReleaseNumber()
	{

		return releaseNumber;
	}

	@Output
	public String getTextSingular()
	{

		return textSingular;
	}

	@Output
	public String getTextPlural()
	{

		return textPlural;
	}
}
