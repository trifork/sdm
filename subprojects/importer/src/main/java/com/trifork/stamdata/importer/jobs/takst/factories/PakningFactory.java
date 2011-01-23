package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Pakning;


public class PakningFactory extends AbstractFactory
{

	private static void setFieldValue(Pakning obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setDrugid(toLong(value));
			break;
		case 1:
			obj.setVarenummer(toLong(value));
			break;
		case 2:
			obj.setAlfabetSekvensnr(toLong(value));
			break;
		case 3:
			obj.setVarenummerForDelpakning(toLong(value));
			break;
		case 4:
			obj.setAntalDelpakninger(toLong(value));
			break;
		case 5:
			obj.setPakningsstoerrelseKlartekst(value);
			break;
		case 6:
			obj.setPakningsstoerrelseNumerisk(toLong(value));
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
			obj.setAntalDDDPrPakning(toLong(value));
			break;
		case 14:
			obj.setOpbevaringstidNumerisk(toLong(value));
			break;
		case 15:
			obj.setOpbevaringstidEnhed(value);
			break;
		case 16:
			obj.setOpbevaringsbetingelser(value);
			break;
		case 17:
			obj.setOprettelsesdato(toLong(value));
			break;
		case 18:
			obj.setDatoForSenestePrisaendring(toLong(value));
			break;
		case 19:
			obj.setUdgaaetDato(toLong(value));
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
			obj.setPakningsdistributoer(toLong(value));
			break;
		default:
			break;
		}
	}


	private static int getOffset(int fieldNo)
	{
		switch (fieldNo) {
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


	private static int getLength(int fieldNo)
	{
		switch (fieldNo) {
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


	private static int getNumberOfFields()
	{
		return 25;
	}


	private static String getLmsName()
	{
		return "LMS02";
	}


	public static ArrayList<Pakning> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Pakning> list = new ArrayList<Pakning>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "CP865"));
			while (reader.ready())
			{
				String line = reader.readLine();
				if (line.length() > 0)
				{
					list.add(parse(line));
				}
			}
			return list;
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (Exception e)
			{
				logger.warn("Could not close FileReader");
			}
		}
	}


	private static Pakning parse(String line)
	{
		Pakning obj = new Pakning();
		for (int fieldNo = 0; fieldNo < getNumberOfFields(); fieldNo++)
		{
			if (getLength(fieldNo) > 0)
			{
				// System.out.print("Getting field "+fieldNo+" from"+getOffset(fieldNo)+" to "+(getOffset(fieldNo)+getLength(fieldNo)));
				String value = line.substring(getOffset(fieldNo),
						getOffset(fieldNo) + getLength(fieldNo)).trim();
				// System.out.println(": "+value);
				setFieldValue(obj, fieldNo, value);
			}
		}
		return obj;
	}
}
