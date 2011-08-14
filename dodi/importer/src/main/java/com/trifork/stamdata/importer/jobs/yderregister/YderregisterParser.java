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

package com.trifork.stamdata.importer.jobs.yderregister;

import java.io.File;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;

import javax.xml.parsers.*;

import org.joda.time.Period;
import org.slf4j.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.Persister;


public class YderregisterParser implements FileParserJob
{
	private static final Logger logger = LoggerFactory.getLogger(YderregisterParser.class);

	private static final String[] requiredFileExt = new String[] { "K05", "K40", "K45", "K1025", "K5094" };

	private static final String JOB_IDENTIFIER = "yderregister_parser";

	private final Period maxTimeGap;

	@Inject
	YderregisterParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}

	@Override
	public String getIdentifier()
	{
		return JOB_IDENTIFIER;
	}

	@Override
	public String getHumanName()
	{
		return "Yderregisteret Parser";
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run(File[] input, Persister persister, Connection connection, long changeset) throws Exception
	{
		String versionString = null;
		int version;

		for (File f : input)
		{
			String currentFileVersion;

			if (f.getName().endsWith("XML") && f.getName().length() >= 15)
			{
				currentFileVersion = f.getName().substring(10, 15);
			}
			else
			{
				logger.warn("Unknown file encountered. filename='{}' parser='{}'", f.getName(), getIdentifier());

				continue;
			}

			if (versionString == null)
			{
				versionString = currentFileVersion;
			}
			else if (!versionString.equals(currentFileVersion))
			{
				throw new Exception("Det blev forsøgt at importere yderregisterfiler med forskellige løbenumre. Løbenummeret fremgår af filnavnet.");
			}
		}

		if (versionString == null)
		{
			throw new Exception("Der blev ikke fundet yderregister filer med et løbenummer");
		}

		version = Integer.parseInt(versionString);

		// Verify the version

		int latestInDB = getLastVersion(persister.getConnection());

		if (latestInDB != 0 && latestInDB > version)
		{
			throw new Exception("Det blev forsøgt at indlæse et yderregister med et løbenummer, der er lavere end det seneste importerede løbenummer.");
		}

		setLastVersion(version, persister.getConnection());

		YderregisterDatasets dataset = parseYderregister(input);

		persister.persistCompleteDataset(dataset.getYderregisterDS());
		persister.persistCompleteDataset(dataset.getYderregisterPersonDS());
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		Map<String, File> fileMap = Maps.newHashMap();

		for (File f : input)
		{
			String fName = f.getName();
			if (fName.indexOf('.') != fName.lastIndexOf('.'))
			{
				fileMap.put(fName.substring(fName.indexOf('.') + 1, fName.lastIndexOf('.')), f);
			}
		}

		for (String reqFileExt : Arrays.asList(requiredFileExt))
		{
			if (!fileMap.containsKey(reqFileExt))
			{
				return false;
			}
		}

		return true;
	}

	public int getLastVersion(Connection connection) throws Exception
	{
		int latestInDB = 0;

		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery("SELECT MAX(Loebenummer) FROM YderLoebenummer");

		if (rs.next())
		{
			latestInDB = rs.getInt(1);
		}

		stm.close();

		return latestInDB;
	}

	public void setLastVersion(int version, Connection connection) throws Exception
	{
		Statement stm = connection.createStatement();
		stm.execute("INSERT INTO YderLoebenummer (Loebenummer) VALUES (" + version + "); ");
		stm.close();
	}

	public YderregisterDatasets parseYderregister(File[] files) throws Exception
	{
		YderRegisterEventHandler handler = new YderRegisterEventHandler();
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();

		for (File f : files)
		{
			try
			{
				if (f.getName().toLowerCase().endsWith("xml"))
				{
					parser.parse(f, handler);
				}
				else
				{
					logger.warn("Can only parse files with extension 'xml'! Ignoring: {}", f.getAbsolutePath());
				}
			}
			catch (Exception e)
			{
				throw new Exception("Error parsing data from file=" + f.getAbsolutePath(), e);
			}
		}

		return handler.getDataset();
	}

	protected class YderRegisterEventHandler extends DefaultHandler
	{
		protected static final String SUPPORTED_INTERFACE_VERSION = "S1040013";
		protected static final String EXPECTED_RECEIPIENT_ID = "B084";

		protected final DateFormat datoFormatter = new SimpleDateFormat("yyyyMMdd");

		protected static final String START_QNAME = "Start";
		protected static final String END_QNAME = "Slut";
		protected static final String YDER_QNAME = "Yder";
		protected static final String PERSON_QNAME = "Person";
		protected static final String OVRIGESPECIALER_QNAME = "OvrigeSpecialer";

		protected String opgDato;

		protected YderregisterDatasets dataset;

		@Override
		public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
		{
			if (START_QNAME.equals(qName))
			{
				if (opgDato == null)
				{
					opgDato = atts.getValue("OpgDato");
					dataset = new YderregisterDatasets(getDateFromOpgDato(opgDato));

				}
				else
				{
					if (!opgDato.equals(atts.getValue("OpgDato")))
					{
						throw new SAXException("The dates in the files differ. This is not allowed and the fileset is invalid.");
					}
				}

				String modtager = atts.getValue("Modt");
				String snitfladeID = atts.getValue("SnitfladeId");

				if (!modtager.trim().equals(EXPECTED_RECEIPIENT_ID))
				{
					throw new SAXException("Yder-register filen har forkert modtagerangivelse: " + modtager + " i stedet for" + EXPECTED_RECEIPIENT_ID + ".");
				}

				if (!SUPPORTED_INTERFACE_VERSION.equals(snitfladeID.trim()))
				{
					throw new SAXException("Yder-register filen har forkert snitfladeID: " + snitfladeID + " i stedet for " + SUPPORTED_INTERFACE_VERSION + ".");
				}
			}
			else if (YDER_QNAME.equals(qName))
			{
				String histId = atts.getValue("HistIdYder");
				String amtKode = atts.getValue("AmtKodeYder").trim();
				String ydernr = removeLeadingZeroes(atts.getValue("YdernrYder")).trim();
				String prakBetegn = atts.getValue("PrakBetegn").trim();
				String adresse = atts.getValue("AdrYder").trim();
				String postnr = atts.getValue("PostnrYder").trim();
				String postdist = atts.getValue("PostdistYder").trim();

				Date afgDato = null;
				Date tilgDato = null;

				String afgDatoString = "";
				String tilgDatoString = "";

				try
				{
					afgDatoString = atts.getValue("AfgDatoYder").trim();
					tilgDatoString = atts.getValue("TilgDatoYder").trim();

					if (!afgDatoString.trim().isEmpty())
					{
						afgDato = datoFormatter.parse(afgDatoString);
					}

					if (!tilgDatoString.trim().isEmpty())
					{
						tilgDato = datoFormatter.parse(tilgDatoString);
					}
				}
				catch (ParseException e)
				{
					throw new SAXException("Problems reading or parsing AfgDatoYder=" + afgDatoString + " or TilDatoYder=" + tilgDatoString, e);
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
			else if (PERSON_QNAME.equals(qName))
			{
				String histId = atts.getValue("HistIdPerson");
				String ydernr = removeLeadingZeroes(atts.getValue("YdernrPerson")).trim();
				String cpr = atts.getValue("CprNr");

				Date afgDato = null;
				Date tilgDato = null;

				String afgDatoString = "";
				String tilgDatoString = "";

				try
				{
					afgDatoString = atts.getValue("AfgDatoPerson").trim();
					tilgDatoString = atts.getValue("TilgDatoPerson").trim();

					if (!afgDatoString.trim().isEmpty())
					{
						afgDato = datoFormatter.parse(afgDatoString);
					}
					if (!tilgDatoString.trim().isEmpty())
					{
						tilgDato = datoFormatter.parse(tilgDatoString);
					}
				}
				catch (ParseException pe)
				{
					throw new SAXException("Problems reading or parsing AfgDatoPerson=" + afgDatoString + " or TilDatoPerson=" + tilgDatoString, pe);
				}

				Long rolleKode = Long.valueOf(atts.getValue("PersonrolleKode"));
				String rolleTekst = atts.getValue("PersonrolleTxt").trim();

				// Ignore empty CPR numbers. TODO (thb): Why? Ask Jan Buchholdt

				if (cpr != null && cpr.length() == 10)
				{
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
		}

		public YderregisterDatasets getDataset()
		{
			return dataset;
		}

		private String removeLeadingZeroes(String valueToStrip)
		{
			// Strips leading zeros but leaves one if the input is all zeros.
			// E.g. "0000" -> "0".

			return valueToStrip.replaceFirst("^0+(?!$)", "");
		}
	}

	public Date getDateFromOpgDato(String opgDato)
	{
		// TODO (thb): Use a SimpleDateFormat here. Why would you return null?
		// Shouldn't it throw an exception. Ask Jan Buchholdt.

		try
		{
			int year = new Integer(opgDato.substring(0, 4));
			int month = new Integer(opgDato.substring(4, 6));
			int date = new Integer(opgDato.substring(6, 8));
			return new GregorianCalendar(year, month - 1, date).getTime();
		}
		catch (NumberFormatException e)
		{
			return null;
		}
	}
}
