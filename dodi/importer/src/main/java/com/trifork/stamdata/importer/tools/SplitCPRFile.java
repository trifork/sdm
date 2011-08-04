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

package com.trifork.stamdata.importer.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


/**
 * Helper program that splits CPR data files into smaller files. This is used
 * when doing an initial load for CPR registry. The dataset is too large to keep
 * in memory, and currently everything is kept in memory during an import.
 *
 * @author Jan Buchholdt <jbu@trifork.com>
 */
public class SplitCPRFile
{
	private static final String END_RECORD = "999";
	private static final String LINE_ENDING = "\r\n";
	private static final String FILE_ENCODING = "ISO-8859-1";

	public static void main(String[] args) throws Exception
	{
		int fileSplit = args.length - 1;

		if (fileSplit < 2 || fileSplit > 9)
		{
			System.err.println("Usage: java SplitCPRFile <infile> <outfile1> <outfile2> .. <outfileN> where N < 10");
			System.exit(-1);
		}

		String name = args[0];
		BufferedWriter[] outputs = new BufferedWriter[fileSplit];

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), FILE_ENCODING));

		for (int i = 0; i < fileSplit; ++i)
		{
			outputs[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[i + 1]), FILE_ENCODING));
		}

		// First line in all files.
		String line = reader.readLine();
		for (int i = 0; i < fileSplit; ++i)
		{
			outputs[i].write(line + LINE_ENDING);
		}

		while (reader.ready())
		{
			line = reader.readLine();
			if (line.startsWith(END_RECORD))
			{
				break;
			}
			String lastCpr = line.substring(12, 13);
			int nb = Integer.parseInt(lastCpr);
			outputs[nb % fileSplit].write(line + LINE_ENDING);
		}

		// Last line in all files
		for (int i = 0; i < fileSplit; ++i)
		{
			outputs[i].write(line + LINE_ENDING);
		}

		for (int i = 0; i < fileSplit; ++i)
		{
			outputs[i].close();
		}
	}
}
