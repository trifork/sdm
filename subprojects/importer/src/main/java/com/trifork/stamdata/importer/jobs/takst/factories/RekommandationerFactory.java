package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Rekommandationer;


public class RekommandationerFactory extends AbstractFactory
{

	private static void setFieldValue(Rekommandationer obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setRekommandationsgruppe(toLong(value));
			break;
		case 1:
			obj.setDrugID(toLong(value));
			break;
		case 2:
			obj.setVarenummer(toLong(value));
			break;
		case 3:
			obj.setRekommandationsniveau(value);
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
			return 4;
		case 2:
			return 15;
		case 3:
			return 21;
		default:
			return -1;
		}
	}


	private static int getLength(int fieldNo)
	{
		switch (fieldNo) {
		case 0:
			return 4;
		case 1:
			return 11;
		case 2:
			return 6;
		case 3:
			return 25;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 4;
	}


	public static String getLmsName()
	{
		return "LMS29";
	}


	public static ArrayList<Rekommandationer> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Rekommandationer> list = new ArrayList<Rekommandationer>();
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


	private static Rekommandationer parse(String line)
	{
		Rekommandationer obj = new Rekommandationer();
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
