package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Tilskudsintervaller;


public class TilskudsintervallerFactory extends AbstractFactory
{

	private static void setFieldValue(Tilskudsintervaller obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setType(toLong(value));
			break;
		case 1:
			obj.setNiveau(toLong(value));
			break;
		case 2:
			obj.setNedreGraense(toLong(value));
			break;
		case 3:
			obj.setOevreGraense(toLong(value));
			break;
		case 4:
			obj.setProcent(toDouble(value));
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
			return 2;
		case 2:
			return 3;
		case 3:
			return 11;
		case 4:
			return 19;
		default:
			return -1;
		}
	}


	private static int getLength(int fieldNo)
	{
		switch (fieldNo) {
		case 0:
			return 2;
		case 1:
			return 1;
		case 2:
			return 8;
		case 3:
			return 8;
		case 4:
			return 5;
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
		return "LMS23";
	}


	public static ArrayList<Tilskudsintervaller> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Tilskudsintervaller> list = new ArrayList<Tilskudsintervaller>();
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


	private static Tilskudsintervaller parse(String line)
	{
		Tilskudsintervaller obj = new Tilskudsintervaller();
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
