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

package com.trifork.stamdata.importer.cpr;

import static com.trifork.stamdata.util.DateUtils.yyyy_MM_dd;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.config.Configuration;
import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.dao.AuditingPersister;
import com.trifork.stamdata.importer.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.cpr.model.CPRDataset;
import com.trifork.stamdata.importer.exceptions.FileImporterException;
import com.trifork.stamdata.importer.exceptions.FilePersistException;
import com.trifork.stamdata.model.Dataset;
import com.trifork.stamdata.model.StamdataEntity;
import com.trifork.stamdata.util.DateUtils;



public class CPRImporter implements FileImporterControlledIntervals {

	private static Logger logger = LoggerFactory.getLogger(CPRImporter.class);
	private Pattern personFilePattern;
	private Pattern personFileDeltaPattern;
	private int overdueHours;
	private boolean performAddressProtection;
	
	public CPRImporter() {
		personFilePattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person"));
		personFileDeltaPattern = Pattern.compile(Configuration.getString("spooler.cpr.file.pattern.person.delta"));
		overdueHours = Configuration.getInt("spooler.cpr.overduehours");
		performAddressProtection = Configuration.getBoolean("spooler.cpr.addressprotection");
		
	}
	
	public void run(List<File> files) throws FileImporterException {

		Connection connection = null;
		Collections.sort(files);
		try {
			connection = MySQLConnectionManager.getConnection();
			AuditingPersister dao = new AuditingPersister(connection);

			logger.debug("Starting to parse CPR file ");

			for (File personFile : files) {
				if (!isPersonerFile(personFile)) {
					throw new FilePersistException("File " + personFile.getAbsolutePath() + " is not a valid CPR file. Nothing is imported from the fileset");
				}
			}

			for (File personFile : files) {

				logger.debug("Starting parsing 'CPR person' file " + personFile.getAbsolutePath());

				CPRDataset cpr = CPRParser.parse(personFile);

				Calendar latestIKraft = getLatestIkraft(connection);
				Calendar previousFileValidFrom = cpr.getPreviousFileValidFrom();
				Calendar currentFileValidFrom = cpr.getValidFrom();
				if (isDeltaFile(personFile)) {
					// Check that the sequence is kept
					if (latestIKraft == null) {
						throw new FilePersistException("The file was a delta file, but there is no latestIKraft date");
					}
					else if (!cpr.getPreviousFileValidFrom().equals(latestIKraft)) {
						throw new FilePersistException("Forrige ikrafttrædelsesdato i personregisterfilen stemmer ikke overens med forrige ikrafttrædelsesdato i databasen. Dato i fil: [" + yyyy_MM_dd.format(cpr.getPreviousFileValidFrom().getTime()) + "]. Dato i database: " + yyyy_MM_dd.format(latestIKraft.getTime()));
					}
				}
				else {
					if(!previousFileValidFrom.equals(currentFileValidFrom)) {
						throw new FilePersistException("Filen er et totaludtræk, men forrige ikraftdato er ikke lig ikraftdato i filen");
					}
					if(latestIKraft != null && !latestIKraft.equals(currentFileValidFrom)) {
						throw new FilePersistException("Filen er et totaludtræk, men filens ikraftdato stemmer ikke med databasen");
					}
				}

				logger.debug("Persisting 'CPR person' file " + personFile.getAbsolutePath());

				for (Dataset<? extends StamdataEntity> dataset : cpr.getDatasets()) {
					dao.persistDeltaDataset(dataset);
				}

				addressProtection(connection);

				// Add latest 'ikraft' date to database if we are not importing
				// a full set.

				if (isDeltaFile(personFile)) {
					insertIkraft(cpr.getValidFrom(), connection);
				}

				logger.debug("Finish parsing 'CPR person' file " + personFile.getAbsolutePath());

				try {
					connection.commit();
				}
				catch (SQLException e) {
					throw new FileImporterException("Could not commit transaction.", e);
				}
				catch (Exception e) {
					throw new FileImporterException("Error during commit transaction", e);
				}
			}
		}
		catch (Exception e) {
			throw new FileImporterException("Error during import of CPR files.", e);
		}
		finally {
			MySQLConnectionManager.close(connection);
		}
	}

	private boolean isPersonerFile(File f) {
		return personFilePattern.matcher(f.getName()).matches();
	}

	private boolean isDeltaFile(File f) {
		return personFileDeltaPattern.matcher(f.getName()).matches();
	}

	@Override
	public boolean checkRequiredFiles(List<File> files) {

		// TODO: Filter non wanted files based on filenames
		// return findPersonerFile(files).size() > 0;

		return true;
	}

	/**
	 * If no cpr in ${cpr.spooler.overduehours} days, fire alarm Maximum gap observed is 7 days without
	 * cpr during christmas 2008.
	 */
	@Override
	public Calendar getNextImportExpectedBefore(Calendar lastImport) {

		Calendar cal;
		if (lastImport == null)
			cal = Calendar.getInstance();
		else
			cal = ((Calendar) lastImport.clone());
		cal.add(Calendar.HOUR, overdueHours);
		return cal;
	}

	static public Calendar getLatestIkraft(Connection con) throws FilePersistException {

		try {
			Statement stm = con.createStatement();
			ResultSet rs = stm.executeQuery("SELECT max(IkraftDato) AS Ikraft FROM PersonIkraft");
			if (rs.first()) return DateUtils.toCalendar(rs.getTimestamp(1));
			return null;
		}
		catch (SQLException sqle) {
			throw new FilePersistException("Der opstod en fejl under fremsøgning af seneste ikrafttrædelsesdato fra databasen.", sqle);
		}
	}

	public static void insertIkraft(Calendar calendar, Connection con) throws FilePersistException {

		try {
			logger.debug("Inserting " + yyyy_MM_dd.format(calendar.getTime()) + " as new 'IkraftDato'");
			Statement stm = con.createStatement();
			String query = "INSERT INTO PersonIkraft (IkraftDato) VALUES ('" + DateUtils.toMySQLdate(calendar) + "');";
			stm.execute(query);
		}
		catch (SQLException sqle) {
			throw new FilePersistException("Der opstod en fejl under indsættelse af ny ikrafttrædelsesdato til databasen.", sqle);
		}
	}

	void addressProtection(Connection con) throws FilePersistException {
		if (performAddressProtection) {
			try {
				// Copy name and addresses to the 'AdresseBeskyttelse' table
				con.createStatement().execute(createProtectedNameAndAddressesSQL());
				// Hide names and addresses for all citizens with name and
				// address
				// protection
				con.createStatement().execute(createHideNameAndAddressesSQL());

			} catch (SQLException sqle) {
				throw new FilePersistException(
						"Der opstod en fejl under indsættelse af ny ikrafttrædelsesdato til databasen.",
						sqle);
			}
		}
	}

	static private String createProtectedNameAndAddressesSQL() {

		String SQL = "REPLACE INTO " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse " + "(CPR, Fornavn, Mellemnavn, Efternavn, CoNavn, Lokalitet, Vejnavn, Bygningsnummer, Husnummer, Etage, " + "SideDoerNummer, Bynavn, Postnummer, PostDistrikt, NavneBeskyttelseStartDato, " + "NavneBeskyttelseSletteDato, VejKode, KommuneKode) " + "(SELECT CPR, Fornavn, Mellemnavn, Efternavn, CoNavn, Lokalitet, Vejnavn, Bygningsnummer, " + "Husnummer, Etage, SideDoerNummer, Bynavn, Postnummer, PostDistrikt, NavneBeskyttelseStartDato, " + "NavneBeskyttelseSletteDato, VejKode, KommuneKode " + "FROM Person " + whereNameAndAddressesSQL() + " ORDER BY validTo)";

		return SQL;
	}

	static private String createHideNameAndAddressesSQL() {

		String SQL = "UPDATE Person SET " + "Fornavn='Navnebeskyttet', " + "Mellemnavn='Navnebeskyttet', " + "Efternavn='Navnebeskyttet', " + "CoNavn='Navnebeskyttet', " + "Lokalitet='Adressebeskyttet', " + "Vejnavn='Adressebeskyttet', " + "Bygningsnummer='99', " + "Husnummer='99', " + "Etage='99', " + "SideDoerNummer='', " + "Bynavn='Adressebeskyttet', " + "Postnummer='9999', " + "PostDistrikt='Adressebeskyttet', " + "VejKode='99', " + "KommuneKode='999', " + "ModifiedBy='SDM2-AddressAndNameProtection' " + whereNameAndAddressesSQL();

		return SQL;
	}

	static private String whereNameAndAddressesSQL() {

		String SQL = "WHERE NavneBeskyttelseStartDato < now() AND " + "(NavneBeskyttelseSletteDato > now() OR ISNULL(NavneBeskyttelseSletteDato)) AND " + "Lokalitet <> 'Adressebeskyttet'";

		return SQL;
	}

}
