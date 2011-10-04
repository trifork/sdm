package com.trifork.stamdata.importer.jobs.sikrede;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Persister;


public class SikredeParser implements FileParser
{
    private static final String _ASSIGNED_DOCTOR_QUERY_START = "REPLACE INTO AssignedDoctor (patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo, reference) SELECT UPPER(SHA1(cpr)) as patientCpr, ydernummer as doctorOrganisationIdentifier, ydernummerIkraftDato as assignedFrom, ydernummerUdlobDato as assignedTo, CONCAT('Stamdata_Sikrede', ' ', modifieddate) as Reference from SikredeYderRelation WHERE CPR IN (";
    private static final String ASSIGNED_DOCTOR_QUERY = _ASSIGNED_DOCTOR_QUERY_START + StringUtils.repeat("?,", 19) + "?)"; //Query with 20 parameters

	private static final int CURRENT_YDER_RELATION_OFFSET = 13;

	private static final Logger logger = LoggerFactory.getLogger(SikredeParser.class);
	
	private static final String FILE_ENCODING = "ISO-8859-1";
	
	private static final int SSK_FIELD_OFFSET = 452;
	private static final int SSK_FIELD_LENGTH = 272;
	private static final int CPR_LENGTH = 10;
	private static final int YDERNUMMER_LENGTH = 6;
	private static final int DATEFORMAT_LENGTH = 8;
	private static final int KODE_LENGTH = 1;
	

	/* TODO: Check actual encoding for file with CSC. Also change the encoding of the test files to match. */

	private static final String FILENAME_DATE_FORMAT = "yyyyMMdd";

	private static final String PATTERN_DATE = "yyyyMMdd";
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat(PATTERN_DATE);
	
	private static final String PATTERN_DATE_WITH_SEPARATORS = "yyyy-MM-dd";
	private final SimpleDateFormat dateFormatterWithSeparators = new SimpleDateFormat(PATTERN_DATE_WITH_SEPARATORS);


	@Override
	public String getIdentifier()
	{
		return "sikrede";
	}

	@Override
	public String getHumanName()
	{
		return "\"Sikrede\" Parser";
	}

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		// 1. CHECK THAT ALL FILES ARE PRESENT

		checkNotNull(input);

		return (input.length > 0);
	}

	@Override
	public void importFiles(File[] files, Persister persister) throws Exception
	{
		// 1. CHECK VERSIONS
		//
		// The first time a dataset it imported we have no previous
		// version and just accept any version.
		//
		// For subsequent versions we make sure the files contain the correct
		// version.
		// The versions should be in sequence.

		Connection connection = persister.getConnection();
		ResultSet rows = connection.createStatement().executeQuery("SELECT MAX(ValidFrom) AS version FROM Sikrede");

		// There will always be a next here, but it might be null.
		rows.next();
		Timestamp previousVersion = rows.getTimestamp("version");

		DateTime currentVersion = getDateFromFilename(files[0].getName());

		if (previousVersion != null && !currentVersion.isAfter(previousVersion.getTime()))
		{
			throw new Exception("The version of " + getIdentifier() + " that was placed for import was out of order. current_version=" + previousVersion + ", new_version=" + currentVersion + ".");
		}

		// 2. PARSE THE DATA
		//
		// Put each data type in to a dataset and let the persister
		// store them in the database.
        Set<String> cprs = new HashSet<String>();
		for (File file : files)
		{
			SikredeDataset sikrede = parse(file, currentVersion);

			for (Dataset<? extends CPREntity> dataset : sikrede.getDatasets())
			{
				persister.persistDeltaDataset(dataset);
                Collection<? extends CPREntity> datasetEntities = dataset.getEntities();
                for (CPREntity datasetEntity : datasetEntities) {
                    cprs.add(datasetEntity.getCpr());
                }
			}
		}

        syncAssignedDoctorTable(cprs.toArray(new String[cprs.size()]), persister);

	}

    private void syncAssignedDoctorTable(String[] cprs, Persister persister) throws SQLException {
        PreparedStatement preparedStatement = persister.getConnection().prepareStatement(ASSIGNED_DOCTOR_QUERY);
        int numberOfParams = StringUtils.countMatches(ASSIGNED_DOCTOR_QUERY, "?");
        int iterations  = cprs.length / numberOfParams;

        int cprIdx = 0;

        //First handle all the CPRs that fits the prepared statement (ASSIGNED_DOCTOR_QUERY) - this way the number of server roundtrips are minimized since we can execute updates for 20 CPRs at a time
        for (int i = 0; i < iterations; i++) {
            for (int paramIdx = 1; paramIdx <= numberOfParams; paramIdx++, cprIdx++) {
                preparedStatement.setString(paramIdx, cprs[cprIdx]);
            }
            preparedStatement.executeUpdate();
        }

        //Handle the remaining CPRs that does not fit into the ASSIGNED_DOCTOR_QUERY prepared statement.
        //Create new query with the proper number of params (always less than the number of params in ASSIGNED_DOCTOR_QUERY)
        int remainder = cprs.length % numberOfParams;
        String remainderQuery = _ASSIGNED_DOCTOR_QUERY_START + StringUtils.repeat("?,", remainder-1) + "?)";
        preparedStatement = persister.getConnection().prepareStatement(remainderQuery);
        numberOfParams = StringUtils.countMatches(remainderQuery, "?");

        for (int paramIdx = 1; paramIdx <= numberOfParams; paramIdx++, cprIdx++) {
            preparedStatement.setString(paramIdx, cprs[cprIdx]);
        }
        preparedStatement.executeUpdate();
    }

    private SikredeDataset parse(File file, DateTime version) throws Exception
	{
		SikredeDataset dataset = new SikredeDataset();
		
		LineIterator lineIterator = FileUtils.lineIterator(file, FILE_ENCODING); 

		final int PREVIOUS_YDER_RELATION_OFFSET = 63;
		final int FUTURE_YDER_RELATION_OFFSET = 102;
		
		final String START_POST_TYPE = "00";
		final String SIKRET_POST_TYPE = "10";
		final String END_POST_TYPE = "99";

		while (lineIterator.hasNext())
		{
			String line = lineIterator.nextLine();
			
			String postType = cut(line, 1, 2);
			
			// There are 3 types of posts in a "Sikrede" file.
			
			if (START_POST_TYPE.equals(postType))
			{
				// From the start post we can grab the valid from date.
				
				DateTimeFormatter dateFormat = DateTimeFormat.forPattern("yyyyMMdd");
				
				String dateString = cut(line, 3, 8);
				DateTime validFrom = dateFormat.parseDateTime(dateString);
				dataset.setValidFrom(validFrom.toDate());
			}
			else if (SIKRET_POST_TYPE.equals(postType))
			{
				// The sikret posts contain the 'meat' of the registry.
				// Each line can contain information about the current,
				// and potentially information about previous and future
				// assigned doctors.
				
				Sikrede sikrede = parseSikrede(line);
				dataset.addEntity(sikrede);

                SikredeYderRelation nuvaerendeYderRelation = parsePatientYderRelation(line, CURRENT_YDER_RELATION_OFFSET, sikrede.getCpr(), SikredeYderRelation.YderType.current);

				if (hasYderRelation(line, PREVIOUS_YDER_RELATION_OFFSET))
				{
                    SikredeYderRelation forrigeYderRelation = parsePatientYderRelation(line, PREVIOUS_YDER_RELATION_OFFSET, sikrede.getCpr(), SikredeYderRelation.YderType.previous);
                    forrigeYderRelation.setYdernummerUdlobDato(nuvaerendeYderRelation.getYdernummerIkraftDato());
                    dataset.addEntity(forrigeYderRelation);
				}
				
				if (hasYderRelation(line, FUTURE_YDER_RELATION_OFFSET))
				{
                    SikredeYderRelation fremtidigYderRelation = parsePatientYderRelation(line, FUTURE_YDER_RELATION_OFFSET, sikrede.getCpr(), SikredeYderRelation.YderType.future);
                    nuvaerendeYderRelation.setYdernummerUdlobDato(fremtidigYderRelation.getYdernummerIkraftDato());
                    dataset.addEntity(fremtidigYderRelation);
				}
				dataset.addEntity(nuvaerendeYderRelation);

				if (hasSaerligSundhedskort(line))
				{
					SaerligSundhedskort saerligSundhedskort = parseSaerligSundhedsKort(line, sikrede.getCpr());
					dataset.addEntity(saerligSundhedskort);
				}
			}
			else if (END_POST_TYPE.equals(postType))
			{
				logger.info("Parsing of \"Sikrede\" file completed.");
			}
			else
			{
				logger.debug("Unsupported post type encountered in \"Sikrede\" parser. post_type={}", postType);
			}
		}

		return dataset;
	}

	/**
	 * Previous and Future yderRelation are optional - if they are not present
	 * the record will consist of all zeros.
	 * 
	 * @param line
	 * @param offset
	 * @return true if the YderRelation record does not contain all zeros, false
	 *         otherwise.
	 */
	private boolean hasYderRelation(String line, int offset)
	{
		final int YDER_RECORD_LENGTH = 39;
		return !StringUtils.repeat("0", YDER_RECORD_LENGTH).equalsIgnoreCase(cut(line, offset, YDER_RECORD_LENGTH));
	}

	/**
	 * SaerligSundhedskort is optional, if it is not present, the SSK record
	 * will consist of 272 spaces.
	 * 
	 * @param line
	 * @return true if the SSK record is NOT 272 spaces. False otherwise
	 */
	private boolean hasSaerligSundhedskort(String line)
	{
		return cut(line, SSK_FIELD_OFFSET, SSK_FIELD_LENGTH) != null;
	}

	/**
	 * Helper method to cut a string into parsable bits. NOTE: offset starts
	 * with 1 so we can use identical offsets to the ones used in the input
	 * format specification.
	 * 
	 * @param line
	 * @param offset First character offset is 1
	 * @param length
	 * @return null if the line parameter is null, or a trimmed string starting
	 *         from the offset character of input parameter line. The resulting
	 *         string can never be longer than specified in the length
	 *         parameter.
	 */
	private String cut(String line, int offset, int length)
	{
		String substring = line.substring(offset - 1, offset + length - 1);
		return StringUtils.trimToNull(substring);
	}

	private Sikrede parseSikrede(String line) throws Exception
	{
		Sikrede sikrede = new Sikrede();
		sikrede.setCpr(parseCpr(line, 3));

		sikrede.setKommunekode(cut(line, 52, 3));
		sikrede.setKommunekodeIKraftDato(yearMonthDayDate(line, 55));

		sikrede.setFoelgeskabsPerson(parseCpr(line, 145)); // Optional field

		sikrede.setStatus(cut(line, 155, 2));
		sikrede.setBevisIkraftDato(yearMonthDayDate(line, 157));

		sikrede.setForsikringsinstans(cut(line, 724, 21));
		sikrede.setForsikringsinstansKode(cut(line, 745, 10));
		sikrede.setForsikringsnummer(cut(line, 755, 15));
		sikrede.setSslGyldigFra(yearMonthDayWithSeparatorsDate(line, 770));
		sikrede.setSslGyldigTil(yearMonthDayWithSeparatorsDate(line, 780));
		sikrede.setSikredesSocialeLand(cut(line, 790, 47));
		sikrede.setSikredesSocialeLandKode(cut(line, 837, 2));

		if (logger.isDebugEnabled()) logger.debug(sikrede.toString());
		
		return sikrede;
	}

	private String parseCpr(String line, int offset)
	{
		return cut(line, offset, CPR_LENGTH);
	}

	private SikredeYderRelation parsePatientYderRelation(String line, int offset, String cpr, SikredeYderRelation.YderType type) throws ParseException
	{
		SikredeYderRelation yderRelation = new SikredeYderRelation();
		
		yderRelation.setCpr(cpr);
		
		yderRelation.setType(type);
		
		yderRelation.setYdernummer(cut(line, offset, YDERNUMMER_LENGTH));
		
		int position = 0;
		
		position = position + YDERNUMMER_LENGTH;
		yderRelation.setYdernummerIkraftDato(yearMonthDayDate(line, offset + position));
		
		position = position + DATEFORMAT_LENGTH;
		yderRelation.setYdernummerRegistreringDato(yearMonthDayDate(line, offset + position));
		
		position = position + DATEFORMAT_LENGTH;
		yderRelation.setSikringsgruppeKode(cut(line, offset + position, KODE_LENGTH));
		
		position = position + KODE_LENGTH;
		yderRelation.setGruppeKodeIkraftDato(yearMonthDayDate(line, offset + position));
		
		position = position + DATEFORMAT_LENGTH;
		yderRelation.setGruppekodeRegistreringDato(yearMonthDayDate(line, offset + position));

		if (logger.isDebugEnabled()) logger.debug(yderRelation.toString());
		
		return yderRelation;
	}

	private SaerligSundhedskort parseSaerligSundhedsKort(String line, String cpr) throws ParseException
	{
		SaerligSundhedskort sundhedskort = new SaerligSundhedskort();
		sundhedskort.setCpr(cpr);
		sundhedskort.setAdresseLinje1(cut(line, 452, 40));
		sundhedskort.setAdresseLinje2(cut(line, 492, 40));
		sundhedskort.setBopelsLand(cut(line, 532, 40));
		sundhedskort.setBopelsLandKode(cut(line, 572, 2));
		sundhedskort.setEmailAdresse(cut(line, 574, 50));
		sundhedskort.setFamilieRelationCpr(parseCpr(line, 624));
		sundhedskort.setFoedselsDato(yearMonthDayWithSeparatorsDate(line, 634));
		sundhedskort.setSskGyldigFra(yearMonthDayWithSeparatorsDate(line, 644));
		sundhedskort.setSskGyldigTil(yearMonthDayWithSeparatorsDate(line, 654));
		sundhedskort.setMobilNummer(cut(line, 664, 20));
		sundhedskort.setPostnummerBy(cut(line, 684, 40));

		if (logger.isDebugEnabled()) logger.debug(sundhedskort.toString());
		
		return sundhedskort;
	}

	private Date yearMonthDayDate(String line, int offset) throws ParseException
	{
		String dateString = cut(line, offset, DATEFORMAT_LENGTH);
		if (!isZeroPaddedDate(dateString))
		{
			return dateFormatter.parse(dateString);
		}
		
		// TODO (thb): Flemming when does this happen and should we not throw an exception?

		return null;
	}

	private Date yearMonthDayWithSeparatorsDate(String line, int offset) throws ParseException
	{
		String dateString = cut(line, offset, PATTERN_DATE_WITH_SEPARATORS.length());
		
		if (dateString != null)
		{
			return dateFormatterWithSeparators.parse(dateString);
		}
		
		// TODO (thb): Flemming when does this happen and should we not throw an exception?
		
		return null;
	}

	/**
	 * 00000000 is when date (only dates with format yyyyMMdd can be 0-padded -
	 * so null should be stored in the database.
	 * 
	 * @param dateString a yyyyMMdd formatted String
	 * @return true if the value of dateString equals dateFormatLength-zeroes.
	 *         False otherwise.
	 */
	private boolean isZeroPaddedDate(String dateString)
	{
		return StringUtils.repeat("0", DATEFORMAT_LENGTH).equals(dateString);
	}

	protected DateTime getDateFromFilename(String filename)
	{
		DateTimeFormatter formatter = DateTimeFormat.forPattern(FILENAME_DATE_FORMAT);
		return formatter.parseDateTime(filename.substring(0, 8));
	}
}
