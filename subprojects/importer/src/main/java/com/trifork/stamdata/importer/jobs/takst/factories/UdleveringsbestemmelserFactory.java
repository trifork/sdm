package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Udleveringsbestemmelser;


public class UdleveringsbestemmelserFactory extends AbstractFactory
{

	private static void setFieldValue(Udleveringsbestemmelser obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setKode(value);
			break;
		case 1:
			obj.setUdleveringsgruppe(value);
			break;
		case 2:
			obj.setKortTekst(value);
			break;
		case 3:
			obj.setTekst(value);
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
			return 5;
		case 2:
			return 6;
		case 3:
			return 16;
		default:
			return -1;
		}
	}


	private static int getLength(int fieldNo)
	{
		switch (fieldNo) {
		case 0:
			return 5;
		case 1:
			return 1;
		case 2:
			return 10;
		case 3:
			return 50;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 5;
	}


	private static String getLmsName()
	{
		return "LMS18";
	}


	public static ArrayList<Udleveringsbestemmelser> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Udleveringsbestemmelser> list = new ArrayList<Udleveringsbestemmelser>();
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


	private static Udleveringsbestemmelser parse(String line)
	{
		Udleveringsbestemmelser obj = new Udleveringsbestemmelser();
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
