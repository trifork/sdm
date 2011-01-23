package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Indholdsstoffer;


public class IndholdsstofferFactory extends AbstractFactory
{

	private static void setFieldValue(Indholdsstoffer obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setDrugID(toLong(value));
			break;
		case 1:
			obj.setVarenummer(toLong(value));
			break;
		case 2:
			obj.setStofklasse(value);
			break;
		case 3:
			obj.setSubstansgruppe(value);
			break;
		case 4:
			obj.setSubstans(value);
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
			return 117;
		case 4:
			return 217;
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
			return 100;
		case 3:
			return 100;
		case 4:
			return 150;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 5;
	}


	public static String getLmsName()
	{
		return "LMS30";
	}


	public static ArrayList<Indholdsstoffer> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Indholdsstoffer> list = new ArrayList<Indholdsstoffer>();
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
				e.printStackTrace();
			}
		}
	}


	private static Indholdsstoffer parse(String line)
	{
		Indholdsstoffer obj = new Indholdsstoffer();
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
