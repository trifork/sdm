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

package com.trifork.stamdata.importer.jobs.cpr;

import static com.google.common.base.Preconditions.*;
import static com.trifork.stamdata.importer.util.Dates.*;
import static org.slf4j.helpers.MessageFormatter.*;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.Date;
import java.util.regex.*;

import org.apache.commons.io.*;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.*;

import com.google.common.base.*;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.trifork.stamdata.importer.jobs.FileParserJob;
import com.trifork.stamdata.importer.persistence.*;


public class CPRParser implements FileParserJob
{
	private static final Logger logger = LoggerFactory.getLogger(CPRParser.class);

	private static final String FILE_ENCODING = "ISO-8859-1";

	private static final String DELTA_CPR_FILE_FILE_ENDING = "01";
	private static final String JOB_IDENTIFIER = "cpr_parser";

	private static final int END_RECORD = 999;
	private static final String EMPTY_DATE_STRING = "000000000000";

	private static final boolean haltOnDateErrors = true;

	private static final Pattern datePattern = Pattern.compile("([\\d]{4})-([\\d]{2})-([\\d]{2})");
	private static final Pattern timestampPattern = Pattern.compile("([\\d]{4})([\\d]{2})([\\d]{2})([\\d]{2})([\\d]{2})");

	private final Period maxTimeGap;

	@Inject
	CPRParser(@Named(JOB_IDENTIFIER + "." + MAX_TIME_GAP) String maxTimeGap)
	{
		this.maxTimeGap = Period.minutes(Integer.parseInt(maxTimeGap));
	}

	@Override
	public String getIdentifier()
	{
		return JOB_IDENTIFIER;
	}

	@Override
	public boolean checkFileSet(File[] input)
	{
		return true; // TODO: Check if the required files are there.
	}

	@Override
	public Period getMaxTimeGap()
	{
		return maxTimeGap;
	}

	@Override
	public void run(File[] input, Persister persister) throws Exception
	{
		Preconditions.checkNotNull(input);
		Preconditions.checkNotNull(persister);

		// Check that the sequence is kept.

		Connection connection = persister.getConnection();

		for (File personFile : input)
		{
			logger.info("Parsing 'CPR person' file. filename=" + personFile.getAbsolutePath());

			CPRDataset cpr = parseFile(personFile);

			// HACK: Don't use the connection this way. @see
			// Persister#getConnection()

			Date previousVersion = getLatestVersion(connection);

			if (previousVersion == null)
			{
				logger.debug("Count not find any previous versions of CPR. Asuming an initial import and skipping sequence checks.");
			}
			else if (!cpr.getPreviousFileValidFrom().equals(previousVersion))
			{
				throw new Exception("CPR file out of sequence. file_date=" + cpr.getPreviousFileValidFrom() + " database_date=" + previousVersion);
			}

			for (Dataset<? extends Record> dataset : cpr.getDatasets())
			{
				persister.persist(dataset);
			}

			// Add the new 'version' date to database,
			// if it is a delta file. Full imports don't have a version.

			// TODO: Should we reset the version if we get a new full
			// import, when we already have data?

			if (personFile.getName().endsWith(DELTA_CPR_FILE_FILE_ENDING))
			{
				insertVersion(cpr.getValidFrom(), connection);
			}
		}
	}

	static public Date getLatestVersion(Connection connection) throws SQLException
	{
		Statement stm = connection.createStatement();
		ResultSet rs = stm.executeQuery("SELECT MAX(IkraftDato) AS Ikraft FROM PersonIkraft");
		rs.next();
		return rs.getTimestamp(1);
	}

	void insertVersion(Date date, Connection con) throws SQLException
	{
		PreparedStatement updateRegistryVersion = con.prepareStatement("INSERT INTO PersonIkraft (IkraftDato) VALUES (?)");
		updateRegistryVersion.setObject(1, date);
		updateRegistryVersion.execute();
		updateRegistryVersion.close();
	}

	@Override
	public String getHumanName()
	{
		return "CPR Parser";
	}

	public static CPRDataset parseFile(File f) throws Exception
	{
		LineIterator lineIterator = FileUtils.lineIterator(f, FILE_ENCODING);

		boolean endRecordReached = false;
		CPRDataset dataset = new CPRDataset();

		int currentLine = 1;

		while (lineIterator.hasNext())
		{
			String line = lineIterator.nextLine();

			if (line.length() > 0)
			{
				int recordType = getRecordType(line);

				if (recordType == END_RECORD)
				{
					endRecordReached = true;
				}
				else if (endRecordReached)
				{
					throw new Exception(format("End record encountered in the middle of a CPR file. filename={}, line_number={}", f.getName(), currentLine));
				}
				else
				{
					parseLine(recordType, line, dataset);
				}
			}
			else
			{
				logger.debug("Ignoring empty line in CPR file. filename={}, line_number={}", f.getName(), currentLine);
			}

			currentLine++;
		}

		if (!endRecordReached)
		{
			throw new Exception("End record is missing from the CPR file. filename=" + f.getName());
		}

		return dataset;
	}

	static void parseLine(int recordType, String line, CPRDataset cpr) throws Exception, ParseException
	{
		switch (recordType)
		{
		case 0:
			cpr.setValidFrom(getValidFrom(line));
			Date forrigeIKraftdato = getForrigeIkraftDato(line);
			if (forrigeIKraftdato != null)
			{
				cpr.setPreviousFileValidFrom(forrigeIKraftdato);
			}
			break;
		case 1:
			cpr.addEntity(personoplysninger(line));
			break;
		case 2:
			// Ignored
			break;
		case 3:
			cpr.addEntity(klarskriftadresse(line));
			break;
		case 4:
			String beskyttelseskode = cut(line, 13, 17);
			if (beskyttelseskode.equals("0001"))
			{
				cpr.addEntity(navneBeskyttelse(line));
			}
			break;
		case 8:
			cpr.addEntity(navneoplysninger(line));
			break;
		case 14:
			cpr.addEntity(barnRelation(line));
			break;
		case 16:
			cpr.addEntity(foraeldremyndighedRelation(line));
			break;
		case 17:
			cpr.addEntity(umyndiggoerelseVaergeRelation(line));
			break;
		}
	}

	static UmyndiggoerelseVaergeRelation umyndiggoerelseVaergeRelation(String line) throws Exception
	{
		UmyndiggoerelseVaergeRelation u = new UmyndiggoerelseVaergeRelation();

		u.setCpr(cut(line, 3, 13));
		u.setUmyndigStartDato(parseDate(DK_yyyy_MM_dd, line, 13, 23));
		u.setUmyndigStartDatoMarkering(cut(line, 23, 24));
		u.setUmyndigSletteDato(parseDate(DK_yyyy_MM_dd, line, 24, 34));
		u.setType(cut(line, 34, 38));
		u.setRelationCpr(cut(line, 38, 48));
		u.setRelationCprStartDato(parseDate(DK_yyyy_MM_dd, line, 48, 58));
		u.setVaergesNavn(cut(line, 58, 92).trim());
		u.setVaergesNavnStartDato(parseDate(DK_yyyy_MM_dd, line, 92, 102));
		u.setRelationsTekst1(cut(line, 102, 136).trim());
		u.setRelationsTekst2(cut(line, 136, 170).trim());
		u.setRelationsTekst3(cut(line, 170, 204).trim());
		u.setRelationsTekst4(cut(line, 204, 238).trim());
		u.setRelationsTekst5(cut(line, 238, 272).trim());

		return u;
	}

	static ForaeldreMyndighedRelation foraeldremyndighedRelation(String line) throws Exception
	{
		ForaeldreMyndighedRelation f = new ForaeldreMyndighedRelation();

		f.setCpr(cut(line, 3, 13));
		f.setType(cut(line, 13, 17));
		f.setForaeldreMyndighedStartDato(parseDate(DK_yyyy_MM_dd, line, 17, 27));
		f.setForaeldreMyndighedMarkering(cut(line, 27, 28));
		f.setForaeldreMyndighedSlettedato(parseDate(DK_yyyy_MM_dd, line, 28, 38));
		f.setRelationCpr(cut(line, 38, 48));
		f.setRelationCprStartDato(parseDate(DK_yyyy_MM_dd, line, 48, 58));

		return f;
	}

	static BarnRelation barnRelation(String line)
	{
		BarnRelation b = new BarnRelation();
		b.setCpr(cut(line, 3, 13));
		b.setBarnCpr(cut(line, 13, 23));

		return b;
	}

	static Navneoplysninger navneoplysninger(String line) throws Exception
	{
		Navneoplysninger n = new Navneoplysninger();
		n.setCpr(cut(line, 3, 13));
		n.setFornavn(cut(line, 13, 63).trim());
		n.setFornavnMarkering(cut(line, 63, 64));
		n.setMellemnavn(cut(line, 64, 104).trim());
		n.setMellemnavnMarkering(cut(line, 104, 105));
		n.setEfternavn(cut(line, 105, 145).trim());
		n.setEfternavnMarkering(cut(line, 145, 146));
		n.setStartDato(parseDate(DK_yyyyMMddHHmm, line, 146, 158));
		n.setStartDatoMarkering(cut(line, 158, 159));
		n.setAdresseringsNavn(cut(line, 159, 193).trim());
		return n;
	}

	static NavneBeskyttelse navneBeskyttelse(String line) throws Exception
	{
		NavneBeskyttelse n = new NavneBeskyttelse();
		n.setCpr(cut(line, 3, 13));
		n.setNavneBeskyttelseStartDato(parseDate(DK_yyyy_MM_dd, line, 17, 27));
		n.setNavneBeskyttelseSletteDato(parseDate(DK_yyyy_MM_dd, line, 27, 37));
		return n;
	}

	static Klarskriftadresse klarskriftadresse(String line) throws Exception
	{
		checkNotNull(line);
		
		Klarskriftadresse k = new Klarskriftadresse();
		k.setCpr(cut(line, 3, 13));
		k.setAdresseringsNavn(cut(line, 13, 47).trim());
		k.setCoNavn(cut(line, 47, 81).trim());
		k.setLokalitet(cut(line, 81, 115).trim());

		// FIXME (thb): Is this the correct field?

		k.setVejnavnTilAdresseringsNavn(cut(line, 115, 149).trim());

		k.setByNavn(cut(line, 149, 183).trim());
		k.setPostNummer(parseLong(line, 183, 187));
		k.setPostDistrikt(cut(line, 187, 207).trim());
		k.setKommuneKode(parseLong(line, 207, 211));
		k.setVejKode(parseLong(line, 211, 215));
		k.setHusNummer(removeLeadingZeros(cut(line, 215, 219).trim()));
		k.setEtage(removeLeadingZeros(cut(line, 219, 221).trim()));
		k.setSideDoerNummer(cut(line, 221, 225).trim());
		k.setBygningsNummer(cut(line, 225, 229).trim());
		k.setVejNavn(cut(line, 229, 249).trim());

		return k;
	}

	static Personoplysninger personoplysninger(String line) throws Exception
	{
		checkNotNull(line);
		
		Personoplysninger person = new Personoplysninger();

		person.setCpr(cut(line, 3, 13));
		person.setGaeldendeCpr(cut(line, 13, 23).trim());
		person.setStatus(cut(line, 23, 25));
		person.setStatusDato(parseDate(DK_yyyyMMddHHmm, line, 25, 37));
		person.setStatusMakering(cut(line, 37, 38));
		person.setKoen(cut(line, 38, 39));
		person.setFoedselsdato(parseDate(DK_yyyy_MM_dd, line, 39, 49));
		person.setFoedselsdatoMarkering(cut(line, 49, 50));
		person.setStartDato(parseDate(DK_yyyy_MM_dd, line, 50, 60));
		person.setStartDatoMarkering(cut(line, 60, 61));
		person.setSlutdato(parseDate(DK_yyyy_MM_dd, line, 61, 71));
		person.setSlutDatoMarkering(cut(line, 71, 72));
		person.setStilling(cut(line, 72, 106).trim());

		return person;
	}

	/**
	 * Gets the record type of a line in the CPR file.
	 */
	private static int getRecordType(String line) throws Exception
	{
		return readInt(line, 0, 3);
	}

	private static String cut(String line, int beginIndex, int endIndex)
	{
		String result = "";

		if (line.length() > beginIndex)
		{
			int end = line.length() < endIndex ? line.length() : endIndex;
			result = line.substring(beginIndex, end);
		}

		return result;
	}

	private static int readInt(String line, int from, int to) throws Exception
	{
		try
		{
			return Integer.parseInt(cut(line, from, to));
		}
		catch (NumberFormatException e)
		{
			throw new Exception("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, e);
		}
	}

	private static Long parseLong(String line, int from, int to) throws Exception
	{
		try
		{
			return Long.parseLong(cut(line, from, to));
		}
		catch (Exception e)
		{
			throw new Exception("Der opstod en fejl under parsning af heltal i linien: [" + line + "], på positionen from: " + from + ", to: " + to, e);
		}
	}

	private static Date parseDate(DateTimeFormatter format, String line, int from, int to) throws ParseException, Exception
	{
		String dateString = cut(line, from, to);
		if (dateString != null && dateString.trim().length() == to - from && !dateString.equals(EMPTY_DATE_STRING))
		{
			return parseDateAndCheckValidity(dateString, format, line);
		}
		return null;
	}

	private static Date getValidFrom(String line) throws Exception
	{
		return DK_yyyyMMdd.parseDateTime(cut(line, 19, 27)).toDate();
	}

	private static Date getForrigeIkraftDato(String line) throws Exception
	{
		// TODO (thb): Why would you return null here if the line is less then
		// 25 chars?

		if (line.length() >= 25)
		{
			return DK_yyyyMMdd.parseDateTime(cut(line, 27, 35)).toDate();
		}

		return null;
	}

	private static String removeLeadingZeros(String str)
	{
		return CharMatcher.is('0').trimLeadingFrom(str);
	}

	static String fixWeirdDate(String date)
	{
		Matcher dateMatcher = datePattern.matcher(date);
		String fixedDate;

		if (dateMatcher.matches())
		{
			fixedDate = fixDate(dateMatcher);
		}
		else
		{
			Matcher timeMatcher = timestampPattern.matcher(date);
			if (timeMatcher.matches())
			{
				fixedDate = fixTime(timeMatcher);
			}
			else
			{
				logger.error("Unexpected date format={}", date);
				return date;
			}
		}

		if (logger.isDebugEnabled() && !fixedDate.equals(date))
		{
			logger.debug("Fixing CPR date from={} to={}", date, fixedDate);
		}

		return fixedDate;
	}

	private static String fixTime(Matcher timeMatcher)
	{
		int year, month, day, hours, minutes;
		year = Integer.parseInt(timeMatcher.group(1));
		month = Integer.parseInt(timeMatcher.group(2));
		day = Integer.parseInt(timeMatcher.group(3));
		hours = Integer.parseInt(timeMatcher.group(4));
		minutes = Integer.parseInt(timeMatcher.group(5));

		if (month == 0)
		{
			month = 1;
		}
		if (day == 0)
		{
			day = 1;
		}
		if (hours >= 24)
		{
			hours = 0;
		}
		if (minutes >= 60)
		{
			minutes = 0;
		}

		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		formatter.format("%04d%02d%02d%02d%02d", year, month, day, hours, minutes);
		return result.toString();
	}

	private static String fixDate(Matcher dateMatcher)
	{
		int year, month, day;

		year = Integer.parseInt(dateMatcher.group(1));
		month = Integer.parseInt(dateMatcher.group(2));
		day = Integer.parseInt(dateMatcher.group(3));

		if (month == 0)
		{
			month = 1;
		}
		if (day == 0)
		{
			day = 1;
		}

		StringBuilder result = new StringBuilder();
		Formatter formatter = new Formatter(result);
		formatter.format("%04d-%02d-%02d", year, month, day);
		return result.toString();
	}

	private static Date parseDateAndCheckValidity(String dateString, DateTimeFormatter format, String line) throws ParseException, Exception
	{
		dateString = fixWeirdDate(dateString);
		DateTime date = format.parseDateTime(dateString);
		String formattedDate = date.toString(format);

		if (!formattedDate.equals(dateString))
		{
			String errorMessage = "Ugyldig dato: " + dateString + " fra linjen [" + line + "]";

			if (haltOnDateErrors)
			{
				throw new Exception(errorMessage);
			}
			else
			{
				logger.error(errorMessage);
			}
		}

		return date.toDate();
	}
}
