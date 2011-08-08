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

package com.trifork.stamdata.importer.jobs.takst.model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.math.NumberUtils;

import com.trifork.stamdata.importer.jobs.takst.FixedLengthParserConfiguration;

public class LaegemiddelFactory implements FixedLengthParserConfiguration<Laegemiddel>
{
	@Override
	public String getFilename()
	{
		return "lms01.txt";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 11;
		case 1:
			return 2;
		case 2:
			return 2;
		case 3:
			return 9;
		case 4:
			return 5;
		case 5:
			return 30;
		case 6:
			return 20;
		case 7:
			return 7;
		case 8:
			return 7;
		case 9:
			return 20;
		case 10:
			return 10;
		case 11:
			return 3;
		case 12:
			return 6;
		case 13:
			return 6;
		case 14:
			return 8;
		case 15:
			return 8;
		case 16:
			return 1;
		case 17:
			return 1;
		case 21:
			return 4;
		case 22:
			return 1;
		case 23:
			return 8;
		case 24:
			return 8;
		default:
			return -1;
		}
	}

	@Override
	public int getNumberOfFields()
	{
		return 25;
	}

	@Override
	public int getOffset(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 0;
		case 1:
			return 11;
		case 2:
			return 13;
		case 3:
			return 15;
		case 4:
			return 24;
		case 5:
			return 29;
		case 6:
			return 59;
		case 7:
			return 79;
		case 8:
			return 86;
		case 9:
			return 93;
		case 10:
			return 113;
		case 11:
			return 123;
		case 12:
			return 126;
		case 13:
			return 132;
		case 14:
			return 138;
		case 15:
			return 146;
		case 16:
			return 154;
		case 17:
			return 155;
		case 21:
			return 159;
		case 22:
			return 163;
		case 23:
			return 164;
		case 24:
			return 172;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(Laegemiddel obj, int fieldNo, String value) throws Exception
	{
		switch (fieldNo)
		{
		case 0:
			obj.setDrugid(NumberUtils.createLong(value));
			break;
		case 1:
			obj.setVaretype(value);
			break;
		case 2:
			obj.setVaredeltype(value);
			break;
		case 3:
			obj.setAlfabetSekvensplads(value);
			break;
		case 4:
			obj.setSpecNummer(NumberUtils.createLong(value));
			break;
		case 5:
			obj.setNavn(value);
			break;
		case 6:
			obj.setLaegemiddelformTekst(value);
			break;
		case 7:
			obj.setFormKode(value);
			break;
		case 8:
			obj.setKodeForYderligereFormOplysn(value);
			break;
		case 9:
			obj.setStyrkeKlarTekst(value);
			break;
		case 10:
			obj.setStyrkeNumerisk(NumberUtils.createLong(value));
			break;
		case 11:
			obj.setStyrkeEnhed(value);
			break;
		case 12:
			obj.setMTIndehaver(NumberUtils.createLong(value));
			break;
		case 13:
			obj.setRepraesentantDistributoer(NumberUtils.createLong(value));
			break;
		case 14:
			obj.setATC(value);
			break;
		case 15:
			obj.setAdministrationsvej(value);
			break;
		case 16:
			boolean hasWarning = "J".equalsIgnoreCase(value);
			obj.setTrafikadvarsel(hasWarning);
			break;
		case 17:
			obj.setSubstitution(value);
			break;
		case 21:
			obj.setLaegemidletsSubstitutionsgruppe(value);
			break;
		case 22:
			boolean suitedForDosageDispensation = "D".equalsIgnoreCase(value);
			obj.setEgnetTilDosisdispensering(suitedForDosageDispensation);
			break;
		case 23:
			if (value != null)
			{
				Date date = new SimpleDateFormat("yyyyMMdd").parse(value);
				obj.setDatoForAfregistrAfLaegemiddel(date);
			}
			break;
		case 24:
			if (value != null)
			{
				Date date = new SimpleDateFormat("yyyyMMdd").parse(value);
				obj.setKarantaenedato(date);
			}
			break;
		}
	}
}
