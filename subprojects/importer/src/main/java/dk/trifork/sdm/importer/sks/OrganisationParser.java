package dk.trifork.sdm.importer.sks;

import dk.trifork.sdm.importer.exceptions.FileParseException;
import dk.trifork.sdm.importer.sks.model.Organisation;
import dk.trifork.sdm.importer.sks.model.Organisation.Organisationstype;
import dk.trifork.sdm.util.DateUtils;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;

public class OrganisationParser {
	private Logger logger = Logger.getLogger(getClass());
	private String line;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public OrganisationParser(String readLine) {
		this.line = readLine;
	}

    public Organisation getOrganisation()  {
		try {
			if (line.length() < 188) {
				logger.warn("Ignoring old format SKS afd line. Length: " + line.length() + " < 188. Line: " + line);
				return null;
			}
			char action = line.charAt(187);
			if (action == ' ') {
				logger.warn("Action/operationskode cannot be derived from line - This must be an old record -> Ignoring");
				return null;
			}
			else if (action == '2') {
				logger.warn("Received an SKS entry with operationskode = 2 (delete). Ignoring as PEM does.");
				return null;
			}
			else if (action == '1' || action == '3') {
				//logger.debug("Action is 1: create or 3: update. Handled the same way.");
				Organisation org = null;
				String type = line.substring(0, 3);
				if (type.equals("afd") || type.equals("shg") || type.equals("sgh")) {
					org = new Organisation((type.equals("afd")) ? Organisationstype.Afdeling : Organisationstype.Sygehus);
					org.setNummer(line.substring(3, 23).trim());
					//logger.debug("nummer: " + afd.getNummer());
					org.setValidFrom(DateUtils.toCalendar(sdf.parse(line.substring(23, 31))));
					org.setValidTo(DateUtils.toCalendar(sdf.parse(line.substring(39, 47))));
					org.setNavn(line.substring(47, 167).trim());
				} else {
					logger.warn("Received an SKS entry with no 'afd' or 'shg' prefixing SKS. Ignoring line:" + line);
				}
				return org;
			}
			else {
				String message = "Unkown operation code: " + action;
				throw new FileParseException(message);
			}
		} catch (Exception e) {
			logger.error("Caught exception while parsing afd line: '" + line + "'", e);
			return null;
		}
	}
}
