// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.parsers.takst.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class PakningskombinationerFactory extends AbstractFactory
{

	private static void setFieldValue(Pakningskombinationer obj, int fieldNo, String value)
	{
		if ("".equals(value)) value = null;
		switch (fieldNo)
		{
		case 0:
			obj.setVarenummerOrdineret(toLong(value));
			break;
		case 1:
			obj.setVarenummerSubstitueret(toLong(value));
			break;
		case 2:
			obj.setVarenummerAlternativt(toLong(value));
			break;
		case 3:
			obj.setAntalPakninger(toLong(value));
			break;
		case 4:
			obj.setEkspeditionensSamledePris(toLong(value));
			break;
		case 5:
			obj.setInformationspligtMarkering(value);
			break;
		default:
			break;
		}
	}

	private static int getOffset(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 0;
		case 1:
			return 6;
		case 2:
			return 12;
		case 3:
			return 18;
		case 4:
			return 20;
		case 5:
			return 29;
		default:
			return -1;
		}
	}

	private static int getLength(int fieldNo)
	{
		switch (fieldNo)
		{
		case 0:
			return 6;
		case 1:
			return 6;
		case 2:
			return 6;
		case 3:
			return 2;
		case 4:
			return 9;
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

	public static String getLmsName()
	{
		return "LMS32";
	}

	public static ArrayList<Pakningskombinationer> read(String rootFolder) throws IOException
	{

		File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

		ArrayList<Pakningskombinationer> list = new ArrayList<Pakningskombinationer>();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "CP865"));
			while (reader.ready())
			{
				String line = reader.readLine();
				if (line.trim().length() > 0)
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

	private static Pakningskombinationer parse(String line)
	{
		Pakningskombinationer obj = new Pakningskombinationer();
		for (int fieldNo = 0; fieldNo < getNumberOfFields(); fieldNo++)
		{
			if (getLength(fieldNo) > 0)
			{
				// System.out.print("Getting field "+fieldNo+" from"+getOffset(fieldNo)+" to "+(getOffset(fieldNo)+getLength(fieldNo)));
				String value = line.substring(getOffset(fieldNo), getOffset(fieldNo) + getLength(fieldNo)).trim();
				// System.out.println(": "+value);
				setFieldValue(obj, fieldNo, value);
			}
		}
		return obj;
	}
}