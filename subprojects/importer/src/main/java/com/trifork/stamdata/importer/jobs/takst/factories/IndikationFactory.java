package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Indikation;


public class IndikationFactory extends AbstractFactory
{

	private static void setFieldValue(Indikation obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setIndikationskode(toLong(value));
			break;
		case 1:
			obj.setIndikationstekstTotal(value);
			break;
		case 2:
			obj.setIndikationstekstLinie1(value);
			break;
		case 3:
			obj.setIndikationstekstLinie2(value);
			break;
		case 4:
			obj.setIndikationstekstLinie3(value);
			break;
		case 5:
			obj.setAktivInaktiv(value);
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
			return 7;
		case 2:
			return 85;
		case 3:
			return 111;
		case 4:
			return 137;
		case 5:
			return 163;
		default:
			return -1;
		}
	}


	private static int getLength(int fieldNo)
	{
		switch (fieldNo) {
		case 0:
			return 7;
		case 1:
			return 78;
		case 2:
			return 26;
		case 3:
			return 26;
		case 4:
			return 26;
		case 5:
			return 1;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 6;
	}


	private static String getLmsName()
	{
		return "LMS26";
	}


	public static ArrayList<Indikation> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Indikation> list = new ArrayList<Indikation>();
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


	private static Indikation parse(String line)
	{
		Indikation obj = new Indikation();
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
