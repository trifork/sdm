package dk.trifork.sdm.importer.autorisationsregister;

import dk.trifork.sdm.importer.autorisationsregister.model.Autorisation;
import dk.trifork.sdm.importer.autorisationsregister.model.Autorisationsregisterudtraek;

import java.io.*;
import java.util.Calendar;

public class AutorisationsregisterParser {

	public static Autorisationsregisterudtraek parse(File file, Calendar validFrom) throws IOException {
		Autorisationsregisterudtraek dataset = new Autorisationsregisterudtraek(validFrom);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "ISO8859-15"));
		while (reader.ready())
			dataset.addEntity(new Autorisation(reader.readLine())); 
		return dataset;
	}

}
