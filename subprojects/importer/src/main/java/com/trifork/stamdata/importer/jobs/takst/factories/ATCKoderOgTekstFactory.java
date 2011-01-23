package com.trifork.stamdata.importer.jobs.takst.factories;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.trifork.stamdata.registre.takst.ATCKoderOgTekst;


public class ATCKoderOgTekstFactory extends AbstractFactory
{

	private static void setFieldValue(ATCKoderOgTekst obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo) {
		case 0:
			obj.setATCNiveau1(value);
			break;
		case 1:
			obj.setATCNiveau2(value);
			break;
		case 2:
			obj.setATCNiveau3(value);
			break;
		case 3:
			obj.setATCNiveau4(value);
			break;
		case 4:
			obj.setATCNiveau5(value);
			break;
		case 5:
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
			return 4;
		case 3:
			return 5;
		case 4:
			return 6;
		case 5:
			return 8;
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
			return 2;
		case 2:
			return 1;
		case 3:
			return 1;
		case 4:
			return 2;
		case 5:
			return 72;
		default:
			return -1;
		}
	}


	private static int getNumberOfFields()
	{
		return 7;
	}


	private static String getLmsName()
	{
		return "LMS12";
	}


	public static ArrayList<ATCKoderOgTekst> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<ATCKoderOgTekst> list = new ArrayList<ATCKoderOgTekst>();
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


	private static ATCKoderOgTekst parse(String line)
	{
		ATCKoderOgTekst obj = new ATCKoderOgTekst();
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
