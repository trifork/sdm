package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.Medicintilskud;


public class MedicintilskudFactory extends AbstractFactory
{

	private static void setFieldValue(Medicintilskud obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setKode(value);
			break;
		case 1:
			obj.setKortTekst(value);
			break;
		case 2:
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
			return 2;
		case 2:
			return 12;
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
			return 10;
		case 2:
			return 50;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 4;
	}


	private static String getLmsName()
	{
		return "LMS16";
	}


	public static ArrayList<Medicintilskud> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Medicintilskud> list = new ArrayList<Medicintilskud>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "CP865"));
			while (reader.ready())
			{
				String line = reader.readLine();
				if (line.length() > 0)
				{
					Medicintilskud next = parse(line);
					if (next.getKode() != null && !next.getKode().isEmpty()) list.add(next);
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


	private static Medicintilskud parse(String line)
	{
		Medicintilskud obj = new Medicintilskud();
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
