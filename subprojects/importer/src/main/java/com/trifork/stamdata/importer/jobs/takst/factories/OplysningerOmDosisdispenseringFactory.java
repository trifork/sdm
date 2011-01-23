package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.OplysningerOmDosisdispensering;


public class OplysningerOmDosisdispenseringFactory extends AbstractFactory
{

	private static void setFieldValue(OplysningerOmDosisdispensering obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setDrugid(toLong(value));
			break;
		case 1:
			obj.setVarenummer(toLong(value));
			break;
		case 2:
			obj.setLaegemidletsSubstitutionsgruppe(value);
			break;
		case 3:
			obj.setMindsteAIPPrEnhed(toLong(value));
			break;
		case 4:
			obj.setMindsteRegisterprisEnh(toLong(value));
			break;
		case 5:
			obj.setTSPPrEnhed(toLong(value));
			break;
		case 6:
			obj.setKodeForBilligsteDrugid(value);
			break;
		case 7:
			obj.setBilligsteDrugid(toLong(value));
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
			return 21;
		case 4:
			return 30;
		case 5:
			return 39;
		case 6:
			return 48;
		case 7:
			return 49;
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
			return 4;
		case 3:
			return 9;
		case 4:
			return 9;
		case 5:
			return 9;
		case 6:
			return 1;
		case 7:
			return 11;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 8;
	}


	private static String getLmsName()
	{
		return "LMS24";
	}


	public static ArrayList<OplysningerOmDosisdispensering> read(String rootFolder)
			throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<OplysningerOmDosisdispensering> list = new ArrayList<OplysningerOmDosisdispensering>();
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


	private static OplysningerOmDosisdispensering parse(String line)
	{
		OplysningerOmDosisdispensering obj = new OplysningerOmDosisdispensering();
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
