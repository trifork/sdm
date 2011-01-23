package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.TilskudsprisgrupperPakningsniveau;


public class TilskudsprisgrupperPakningsniveauFactory extends AbstractFactory
{

	private static void setFieldValue(TilskudsprisgrupperPakningsniveau obj, int fieldNo,
			String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setTilskudsprisGruppe(toLong(value));
			break;
		case 1:
			obj.setVarenummer(toLong(value));
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
			return 6;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 3;
	}


	private static String getLmsName()
	{
		return "LMS07";
	}


	public static ArrayList<TilskudsprisgrupperPakningsniveau> read(String rootFolder)
			throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");
		ArrayList<TilskudsprisgrupperPakningsniveau> list = new ArrayList<TilskudsprisgrupperPakningsniveau>();
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


	private static TilskudsprisgrupperPakningsniveau parse(String line)
	{
		TilskudsprisgrupperPakningsniveau obj = new TilskudsprisgrupperPakningsniveau();
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
