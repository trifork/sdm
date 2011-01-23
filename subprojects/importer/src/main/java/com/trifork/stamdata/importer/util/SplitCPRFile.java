package com.trifork.stamdata.importer.util;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;


public class SplitCPRFile
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{

		int fileSplit = args.length - 1;

		if (fileSplit < 2 || fileSplit > 9)
		{
			System.err
					.println("Use this as: java SplitCPRFile <infile> <ourfile1> <ourfile2> .. <ourfileN> where N < 10");
			System.exit(-1);
		}

		String name = args[0];
		BufferedWriter[] outputs = new BufferedWriter[fileSplit];

		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "ISO-8859-1"));

			for (int i = 0; i < fileSplit; ++i)
			{
				outputs[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[i + 1]), "ISO-8859-1"));
			}

			// First line in all files
			String line = reader.readLine();
			for (int i = 0; i < fileSplit; ++i)
			{
				outputs[i].write(line + "\r\n");
			}

			while (reader.ready())
			{
				line = reader.readLine();
				String lastCpr = line.substring(12, 13);
				int nb = Integer.parseInt(lastCpr);
				outputs[nb % fileSplit].write(line + "\r\n");
			}

			for (int i = 0; i < fileSplit; ++i)
			{
				outputs[i].close();
			}

			// FIXME: Proper exception handling.
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
