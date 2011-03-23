package dk.trifork.sdm.tools;

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
 * @author Jan Buchholdt (jbu@trifork.com)
 */
public class SplitCPRFile {

	public static void main(String[] args) throws Exception {

		int fileSplit = args.length - 1;
		
		if (fileSplit < 2 || fileSplit > 9) {
			System.err.println("Usage: java SplitCPRFile <infile> <ourfile1> <ourfile2> .. <ourfileN> where N < 10");
			System.exit(-1);
		}
		
		String name = args[0];
		BufferedWriter[] outputs = new BufferedWriter[fileSplit];

		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "ISO-8859-1"));

		for (int i = 0; i < fileSplit; ++i) {
			outputs[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[i + 1]), "ISO-8859-1"));
		}

		// First line in all files
		String line = reader.readLine();
		for (int i = 0; i < fileSplit; ++i) {
			outputs[i].write(line + "\r\n");
		}

		while (reader.ready()) {
			line = reader.readLine();
			String lastCpr = line.substring(12, 13);
			int nb = Integer.parseInt(lastCpr);
			outputs[nb % fileSplit].write(line + "\r\n");
		}

		for (int i = 0; i < fileSplit; ++i) {
			outputs[i].close();
		}
	}
}
