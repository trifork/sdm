package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.importer.jobs.FileParser;
import com.trifork.stamdata.importer.jobs.sikrede.model.*;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.Persister;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.trifork.stamdata.Preconditions.checkNotNull;

public class SikredeParser implements FileParser {
    private static final String FILENAME_DATE_FORMAT = "yyyyMMdd";
    private static final int CPR_LENGTH = 10;
    private static final int YDERNUMMER_LENGTH = 6;
    private static final int DATEFORMAT_LENGTH = 8;
    private static final int KODE_LENGTH = 1;

    @Override
    public String getIdentifier() {
        return "sikrede";
    }

    @Override
    public String getHumanName() {
        return "\"Sikrede\" Parser";
    }

    @Override
    public boolean ensureRequiredFileArePresent(File[] input) {
        // 1. CHECK THAT ALL FILES ARE PRESENT

        checkNotNull(input);

        return (input.length > 0);

    }

    @Override
    public void importFiles(File[] files, Persister persister) throws Exception {
        // 1. CHECK VERSIONS
        //
        // The first time a dataset it imported we have no previous
        // version and just accept any version.
        //
        // For subsequent versions we make sure the files contain the correct version.
        // The versions should be in sequence.

        Connection connection = persister.getConnection();
        ResultSet rows = connection.createStatement().executeQuery("SELECT MAX(ValidFrom) as version FROM Sikrede");

        // There will always be a next here, but it might be null.
        rows.next();
        Timestamp previousVersion = rows.getTimestamp("version");

        DateTime currentVersion = getDateFromFilename(files[0].getName());

        if (previousVersion != null && !currentVersion.isAfter(previousVersion.getTime())) {
            throw new Exception("The version of " + getIdentifier() + " that was placed for import was out of order. current_version='" + previousVersion + "', new_version='" + currentVersion + "'.");
        }


        // 2. PARSE THE DATA
        //
        // Put each data type in to a dataset and let the persister
        // store theme in the database.
        //
        // Use @Output and @Id annotations on your domain classes to
        // tell the persister what to do.

        for (File file : files) {
            SikredeDataset sikrede = parse(file, currentVersion);
            for (Dataset<? extends CPREntity> dataset : sikrede.getDatasets()) {
                persister.persistDeltaDataset(dataset);
            }
        }

    }

    private SikredeDataset parse(File file, DateTime version) throws Exception {
        SikredeDataset dataset = new SikredeDataset();
        LineIterator lineIterator = FileUtils.lineIterator(file, "UTF-8"); //TODO - Check actual encoding for file with CSC

        final int PREVIOUS_YDER_RELATION_OFFSET = 63;
        final int FUTURE_YDER_RELATION_OFFSET = 102;
        while (lineIterator.hasNext()) {
            String line = lineIterator.nextLine();
            if (!"10".equalsIgnoreCase(cut(line, 1, 2))) {
                continue;// Ignore lines that are not of post type 10
            }

            Sikrede sikrede = parseSikrede(line);
            dataset.addEntity(sikrede);
            dataset.setValidFrom(sikrede.getSslGyldigFra()); //TODO usikker på om dette er korrekt
            dataset.addEntity(parsePatientYderRelation(line, 13, sikrede.getCpr(), SikredeYderRelation.YderType.current));

            if (hasYderRelation(line, PREVIOUS_YDER_RELATION_OFFSET)) {
                /* Previous yder relation is present */
                dataset.addEntity(parsePatientYderRelation(line, PREVIOUS_YDER_RELATION_OFFSET, sikrede.getCpr(), SikredeYderRelation.YderType.previous));
            }

            if (hasYderRelation(line, FUTURE_YDER_RELATION_OFFSET)) {
                /* Future yder relation is present */
                dataset.addEntity(parsePatientYderRelation(line, FUTURE_YDER_RELATION_OFFSET, sikrede.getCpr(), SikredeYderRelation.YderType.future));
            }

            if (hasSaerligSundhedskort(line)) {
                SaerligSundhedskort saerligSundhedskort = parseSaerligSundhedsKort(line, sikrede.getCpr());
                dataset.addEntity(saerligSundhedskort);
            }
        }

        return dataset;
    }

    /**
     * Previous and Future yderRelation are optional - if they are not present the record will consist of all zeros
     *
     * @param line
     * @param offset
     * @return true if the YderRelation record does not contain all zeros, false otherwise.
     */
    private boolean hasYderRelation(String line, int offset) {
        final int YDER_RECORD_LENGTH = 39;
        return !StringUtils.repeat("0", YDER_RECORD_LENGTH).equalsIgnoreCase(cut(line, offset, YDER_RECORD_LENGTH));
    }

    /**
     * SaerligSundhedskort is optional, if it is not present, the SSK record will consist of 272 spaces.
     * @param line
     * @return true if the SSK record is NOT 272 spaces. False otherwise
     */
    private boolean hasSaerligSundhedskort(String line) {
        /*   */
        return cut(line, 452, 272) != null;
    }

    /**
     * Helper method to cut a string into parsable bits. NOTE: offset starts with 1 so we can use identical offsets to the ones used in the input format specification.
     * @param line
     * @param offset First character offset is 1
     * @param length
     * @return null if the line parameter is null, or a trimmed string starting from the offset character of input parameter line. The resulting string can never be longer than specified in the length parameter.
     */
    private String cut(String line, int offset, int length) {
        String substring = StringUtils.mid(line, offset - 1, length);
        return StringUtils.trimToNull(substring);
    }

    private Sikrede parseSikrede(String line) throws Exception {

        Sikrede sikrede = new Sikrede();
        sikrede.setCpr(parseCpr(line, 3));

        sikrede.setKommunekode(cut(line, 52, 3));
        sikrede.setKommunekodeIKraftDato(yearMonthDayDate(line, 55));

        sikrede.setFoelgeskabsPerson(parseCpr(line, 145)); //Optional field

        sikrede.setStatus(cut(line, 155, 2));
        sikrede.setBevisIkraftDato(yearMonthDayDate(line, 157));

        sikrede.setForsikringsinstans(cut(line, 724, 21));
        sikrede.setForsikringsinstansKode(cut(line, 745, 10));
        sikrede.setForsikringsnummer(cut(line, 755, 15));
        sikrede.setSslGyldigFra(yearMonthDayWithSeparatorsDate(line, 770));
        sikrede.setSslGyldigTil(yearMonthDayWithSeparatorsDate(line, 780));
        sikrede.setSikredesSocialeLand(cut(line, 790, 47));
        sikrede.setSikredesSocialeLandKode(cut(line, 837, 2));

        System.out.println(sikrede);
        return sikrede;
    }

    private String parseCpr(String line, int offset) {
        return cut(line, offset, CPR_LENGTH);
    }

    private SikredeYderRelation parsePatientYderRelation(String line, int offset, String cpr, SikredeYderRelation.YderType type) throws ParseException {
        SikredeYderRelation yderRelation = new SikredeYderRelation();
        int position = 0;
        yderRelation.setCpr(cpr);
        yderRelation.setType(type);
        yderRelation.setYdernummer(cut(line, offset, YDERNUMMER_LENGTH));
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

        System.out.println(yderRelation);
        return yderRelation;
    }

    private SaerligSundhedskort parseSaerligSundhedsKort(String line, String cpr) throws ParseException {
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

        System.out.println(sundhedskort);
        return sundhedskort;
    }

    private static final String PATTERN_DATE = "yyyyMMdd";
    private static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(PATTERN_DATE);

    private Date yearMonthDayDate(String line, int offset) throws ParseException {
        String dateString = cut(line, offset, PATTERN_DATE.length());
        if (!isZeroPaddedDate(dateString, PATTERN_DATE.length())) {
            return DATE_FORMATTER.parse(dateString);
        }

        return null;
    }

    private static final String PATTERN_DATE_WITH_SEPARATORS = "yyyy-MM-dd";
    private static SimpleDateFormat DATE_FORMATTER_WITH_SEPARATORS = new SimpleDateFormat(PATTERN_DATE_WITH_SEPARATORS);

    private Date yearMonthDayWithSeparatorsDate(String line, int offset) throws ParseException {
        String dateString = cut(line, offset, PATTERN_DATE_WITH_SEPARATORS.length());
        if (dateString != null) {
            return DATE_FORMATTER_WITH_SEPARATORS.parse(dateString);
        }
        return null;
    }

    /**
     * 00000000 is when date (only dates with format yyyyMMdd can be 0-padded - so null should be stored in the database
     *
     * @param dateString
     * @return true if the value of dateString equals dateFormatLength-zeroes. False otherwise.
     */
    private boolean isZeroPaddedDate(String dateString, int dateFormatLength) {
        return StringUtils.repeat("0", dateFormatLength).equalsIgnoreCase(dateString);
    }

    protected DateTime getDateFromFilename(String filename) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(FILENAME_DATE_FORMAT);
        return formatter.parseDateTime(filename.substring(0, 8));
    }
}
