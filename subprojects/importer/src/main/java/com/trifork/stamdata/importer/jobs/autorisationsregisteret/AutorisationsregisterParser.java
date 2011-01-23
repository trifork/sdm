package com.trifork.stamdata.importer.jobs.autorisationsregisteret;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.StringTokenizer;

import com.trifork.stamdata.registre.autorisation.Autorisation;


public class AutorisationsregisterParser
{

	public Autorisationsregisterudtraek parse(File file, Date validFrom) throws IOException
	{

		Autorisationsregisterudtraek dataset = new Autorisationsregisterudtraek(validFrom);

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
				"ISO8859-15"));

		while (reader.ready())
		{
			dataset.addRecord(autorisationEntity(reader.readLine()));
		}

		return dataset;
	}


	static public Autorisation autorisationEntity(String line)
	{

		Autorisation res = new Autorisation();

		StringTokenizer stringTokenizer = new StringTokenizer(line, ";");
		res.setNummer(stringTokenizer.nextToken());
		res.setCpr(stringTokenizer.nextToken());
		res.setEfternavn(stringTokenizer.nextToken());
		res.setFornavn(stringTokenizer.nextToken());
		res.setUddKode(stringTokenizer.nextToken());

		return res;
	}
}
