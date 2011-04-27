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

package dk.trifork.sdm.importer.yderregister;

import dk.trifork.sdm.importer.exceptions.FileParseException;
import dk.trifork.sdm.importer.yderregister.model.Yderregister;
import dk.trifork.sdm.importer.yderregister.model.YderregisterDatasets;
import dk.trifork.sdm.importer.yderregister.model.YderregisterPerson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class YderregisterParser {

	private static final Logger logger = LoggerFactory.getLogger(YderregisterParser.class);

	public YderregisterDatasets parseYderregister(List<File> files) throws FileParseException {

		YderRegisterEventHandler handler = new YderRegisterEventHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		File currentFile = null;
		try {
			SAXParser parser = factory.newSAXParser();

			for (File f : files) {
				currentFile = f;
				if (f.getName().toUpperCase().endsWith("XML")) {
					parser.parse(f, handler);
				}
				else {
					logger.warn("Can only parse files with extension 'XML'! Ignoring: " + f.getAbsolutePath());
				}
			}
		}
		catch (Exception e) {
			String errorMessage = "Error parsing data from file ";
			if (currentFile != null) errorMessage += currentFile.getAbsolutePath();
			logger.error(errorMessage, e);
			throw new FileParseException(errorMessage, e);
		}
		return handler.getDataset();
	}


	protected class YderRegisterEventHandler extends DefaultHandler {

		protected static final String S1040013 = "S1040013";
		protected static final String B084 = "B084";

		protected final DateFormat datoFormatter = new SimpleDateFormat("yyyyMMdd");

		protected static final String START_QNAME = "Start";
		protected static final String END_QNAME = "Slut";
		protected static final String YDER_QNAME = "Yder";
		protected static final String PERSON_QNAME = "Person";
		protected static final String OVRIGESPECIALER_QNAME = "OvrigeSpecialer";

		protected String opgDato;

		private YderregisterDatasets dataset;

		public YderRegisterEventHandler() {

		}

		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {

			if (START_QNAME.equals(qName)) {

				if (opgDato == null) {
					opgDato = atts.getValue("OpgDato");
					dataset = new YderregisterDatasets(getDateFromOpgDato(opgDato));

				}
				else {
					if (!opgDato.equals(atts.getValue("OpgDato"))) {
						throw new SAXException("Dates in the files differs. This is not allowed and the files are invalid");
					}
				}

				String modtager = atts.getValue("Modt");
				String snitfladeID = atts.getValue("SnitfladeId");

				if (!modtager.trim().equals(B084)) {
					throw new SAXException("Yder-register filen har forkert modtagerangivelse: " + modtager + " i stedet for" + B084 + ".");
				}
				if (!snitfladeID.trim().equals(S1040013)) {
					throw new SAXException("Yder-register filen har forkert snitfladeID: " + snitfladeID + " i stedet for " + S1040013 + ".");
				}

			}
			else if (YDER_QNAME.equals(qName)) {
				String histId = atts.getValue("HistIdYder");
				String amtKode = atts.getValue("AmtKodeYder").trim();
				// Long amtKodeLong = (amtKode != null) ? new
				// Long(Long.parseLong(amtKode)) : null;

				String ydernr = removeLeadingZeroes(atts.getValue("YdernrYder")).trim();
				String prakBetegn = atts.getValue("PrakBetegn").trim();
				String adresse = atts.getValue("AdrYder").trim();
				String postnr = atts.getValue("PostnrYder").trim();
				String postdist = atts.getValue("PostdistYder").trim();

				Date afgDato = null;
				Date tilgDato = null;
				String afgDatoString = "";
				String tilgDatoString = "";
				try {
					afgDatoString = atts.getValue("AfgDatoYder").trim();
					tilgDatoString = atts.getValue("TilgDatoYder").trim();

					if (!afgDatoString.trim().isEmpty()) {
						afgDato = datoFormatter.parse(afgDatoString);
					}
					if (!tilgDatoString.trim().isEmpty()) {
						tilgDato = datoFormatter.parse(tilgDatoString);
					}

				}
				catch (ParseException pe) {
					throw new SAXException("Problems reading or parsing AfgDatoYder=" + afgDatoString + " or TilDatoYder=" + tilgDatoString, pe);
				}

				String hvdSpecKode = atts.getValue("HvdSpecKode").trim();
				String hvdSpecTekst = atts.getValue("HvdSpecTxt").trim();
				String telefon = atts.getValue("HvdTlf").trim();
				String email = atts.getValue("EmailYder").trim();
				String www = atts.getValue("WWW").trim();

				Yderregister yder = new Yderregister();

				yder.setHistID(histId);
				yder.setAmtNummer(Integer.parseInt(amtKode));
				yder.setNummer(ydernr);
				yder.setNavn(prakBetegn);
				yder.setVejnavn(adresse);
				yder.setPostnummer(postnr);
				yder.setBynavn(postdist);
				yder.setAfgangDato(afgDato);
				yder.setTilgangDato(tilgDato);
				yder.setHovedSpecialeKode(hvdSpecKode);
				yder.setHovedSpecialeTekst(hvdSpecTekst);
				yder.setTelefon(telefon);
				yder.setEmail(email);
				yder.setWww(www);

				dataset.addYderregister(yder);

			}
			else if (PERSON_QNAME.equals(qName)) {
				String histId = atts.getValue("HistIdPerson");
				String ydernr = removeLeadingZeroes(atts.getValue("YdernrPerson")).trim();
				String cpr = atts.getValue("CprNr");

				Date afgDato = null;
				Date tilgDato = null;
				String afgDatoString = "";
				String tilgDatoString = "";
				try {
					afgDatoString = atts.getValue("AfgDatoPerson").trim();
					tilgDatoString = atts.getValue("TilgDatoPerson").trim();

					if (!afgDatoString.trim().isEmpty()) {
						afgDato = datoFormatter.parse(afgDatoString);
					}
					if (!tilgDatoString.trim().isEmpty()) {
						tilgDato = datoFormatter.parse(tilgDatoString);
					}

				}
				catch (ParseException pe) {
					throw new SAXException("Problems reading or parsing AfgDatoPerson=" + afgDatoString + " or TilDatoPerson=" + tilgDatoString, pe);
				}

				Long rolleKode = Long.valueOf(atts.getValue("PersonrolleKode"));
				String rolleTekst = atts.getValue("PersonrolleTxt").trim();

				// Ignorer tomme CPR numre
				if (cpr != null && cpr.length() == 10) {
					YderregisterPerson yderPerson = new YderregisterPerson();
					yderPerson.setAfgangDato(afgDato);
					yderPerson.setTilgangDato(tilgDato);
					yderPerson.setCpr(cpr);
					yderPerson.setHistIdPerson(histId);
					yderPerson.setPersonrolleKode(rolleKode);
					yderPerson.setPersonrolleTxt(rolleTekst);
					yderPerson.setNummer(ydernr);
					dataset.addYderregisterPerson(yderPerson);
				}
			}
			else if (OVRIGESPECIALER_QNAME.equals(qName)) {
				// // HRA: Ignore
				// String kode = atts.getValue("SpecKode");
				// String text = atts.getValue("SpecTxt");
			}
		}

		public void endElement(String uri, String localName, String qName) throws SAXException {

			if (YDER_QNAME.equals(qName)) {
			}
		}

		public YderregisterDatasets getDataset() {

			return dataset;
		}

		private String removeLeadingZeroes(String s) {

			if (s != null && s.length() > 0) {
				s = s.trim();
				int i = 0;
				while ((i + 1) <= s.length() && s.charAt(i) == '0') {
					i++;
				}
				s = s.substring(i);
			}
			return s;
		}

	}

	public Calendar getDateFromOpgDato(String opgDato) {

		try {
			int year = new Integer(opgDato.substring(0, 4));
			int month = new Integer(opgDato.substring(4, 6));
			int date = new Integer(opgDato.substring(6, 8));
			return new GregorianCalendar(year, month - 1, date);
		}
		catch (NumberFormatException e) {
			return null;
		}
	}

}
