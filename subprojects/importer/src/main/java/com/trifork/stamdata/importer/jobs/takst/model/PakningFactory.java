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

import com.trifork.stamdata.importer.jobs.takst.FixedLengthParserConfiguration;



public class PakningFactory implements FixedLengthParserConfiguration<Pakning>
{
	@Override
	public String getFilename()
	{
		return "lms02.txt";
	}

	@Override
	public int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 11;
		case 1:
			return 6;
		case 2:
			return 3;
		case 3:
			return 6;
		case 4:
			return 3;
		case 5:
			return 30;
		case 6:
			return 8;
		case 7:
			return 2;
		case 8:
			return 4;
		case 9:
			return 5;
		case 10:
			return 5;
		case 11:
			return 2;
		case 12:
			return 5;
		case 13:
			return 9;
		case 14:
			return 2;
		case 15:
			return 1;
		case 16:
			return 1;
		case 17:
			return 8;
		case 18:
			return 8;
		case 19:
			return 8;
		case 20:
			return 1;
		case 21:
			return 1;
		case 22:
			return 1;
		case 24:
			return 6;
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
			return 17;
		case 3:
			return 20;
		case 4:
			return 26;
		case 5:
			return 29;
		case 6:
			return 59;
		case 7:
			return 67;
		case 8:
			return 69;
		case 9:
			return 73;
		case 10:
			return 78;
		case 11:
			return 83;
		case 12:
			return 85;
		case 13:
			return 90;
		case 14:
			return 99;
		case 15:
			return 101;
		case 16:
			return 102;
		case 17:
			return 103;
		case 18:
			return 111;
		case 19:
			return 119;
		case 20:
			return 127;
		case 21:
			return 128;
		case 22:
			return 129;
		case 24:
			return 131;
		default:
			return -1;
		}
	}

	@Override
	public void setFieldValue(Pakning obj, int fieldNo, String value)
	{
		switch (fieldNo)
		{
		case 0:
			obj.setDrugid(Long.parseLong(value));
			break;
		case 1:
			obj.setVarenummer(Long.parseLong(value));
			break;
		case 2:
			obj.setAlfabetSekvensnr(Long.parseLong(value));
			break;
		case 3:
			obj.setVarenummerForDelpakning(Long.parseLong(value));
			break;
		case 4:
			obj.setAntalDelpakninger(Long.parseLong(value));
			break;
		case 5:
			obj.setPakningsstoerrelseKlartekst(value);
			break;
		case 6:
			obj.setPakningsstoerrelseNumerisk(Long.parseLong(value));
			break;
		case 7:
			obj.setPakningsstoerrelseEnhed(value);
			break;
		case 8:
			obj.setEmballagetype(value);
			break;
		case 9:
			obj.setUdleveringsbestemmelse(value);
			break;
		case 10:
			obj.setUdleveringSpeciale(value);
			break;
		case 11:
			obj.setMedicintilskudskode(value);
			break;
		case 12:
			obj.setKlausulForMedicintilskud(value);
			break;
		case 13:
			obj.setAntalDDDPrPakning(Long.parseLong(value));
			break;
		case 14:
			obj.setOpbevaringstidNumerisk(Long.parseLong(value));
			break;
		case 15:
			obj.setOpbevaringstidEnhed(value);
			break;
		case 16:
			obj.setOpbevaringsbetingelser(value);
			break;
		case 17:
			obj.setOprettelsesdato(Long.parseLong(value));
			break;
		case 18:
			obj.setDatoForSenestePrisaendring(Long.parseLong(value));
			break;
		case 19:
			obj.setUdgaaetDato(Long.parseLong(value));
			break;
		case 20:
			obj.setBeregningskodeAIPRegpris(value);
			break;
		case 21:
			obj.setPakningOptagetITilskudsgruppe(value);
			break;
		case 22:
			obj.setFaerdigfremstillingsgebyr(value);
			break;
		case 24:
			obj.setPakningsdistributoer(Long.parseLong(value));
			break;
		default:
			break;
		}
	}
}
