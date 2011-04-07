package dk.trifork.sdm.importer.cpr;

import static dk.trifork.sdm.util.DateUtils.yyyy_MM_dd;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.importer.cpr.model.CPRDataset;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.importer.exceptions.FilePersistException;
import dk.trifork.sdm.model.Dataset;
import dk.trifork.sdm.model.StamdataEntity;
import dk.trifork.sdm.util.DateUtils;


public class CPRImporter implements FileImporterControlledIntervals {

	private Logger logger = LoggerFactory.getLogger(getClass());

	public void run(List<File> files) throws FileImporterException {

		Connection connection = null;

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

				if (isDeltaFile(personFile)) {
					// Check that the sequence is kept
					Calendar latestIKraft = getLatestIkraft(connection);
					if (latestIKraft == null) {
						logger.warn("could not get latestIKraft from database. Asuming empty database and skipping import sequence checks.");
					}
					else if (!cpr.getPreviousFileValidFrom().equals(latestIKraft)) {
						throw new FilePersistException("Forrige ikrafttrædelsesdato i personregisterfilen stemmer ikke overens med forrige ikrafttrædelsesdato i databasen. Dato i fil: [" + yyyy_MM_dd.format(cpr.getPreviousFileValidFrom().getTime()) + "]. Dato i database: " + yyyy_MM_dd.format(latestIKraft.getTime()));
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

		return (f.getName().startsWith("D") && f.getName().indexOf(".L4311") == 7);
	}

	private boolean isDeltaFile(File f) {

		return (f.getName().startsWith("D") && f.getName().endsWith(".L431101"));
	}

	@Override
	public boolean checkRequiredFiles(List<File> files) {

		// TODO: Filter non wanted files based on filenames
		// return findPersonerFile(files).size() > 0;
		
		return true;
	}

	/**
	 * If no cpr in 12 days, fire alarm Maximum gap observed is 7 days without
	 * cpr during christmas 2008.
	 */
	@Override
	public Calendar getNextImportExpectedBefore(Calendar lastImport) {

		Calendar cal;
		if (lastImport == null)
			cal = Calendar.getInstance();
		else
			cal = ((Calendar) lastImport.clone());
		cal.add(Calendar.DATE, 12);
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

	void insertIkraft(Calendar calendar, Connection con) throws FilePersistException {

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

		try {
			// Copy name and addresses to the 'AdresseBeskyttelse' table
			con.createStatement().execute(createProtectedNameAndAddressesSQL());
			// Hide names and addresses for all citizens with name and address
			// protection
			con.createStatement().execute(createHideNameAndAddressesSQL());

		}
		catch (SQLException sqle) {
			throw new FilePersistException("Der opstod en fejl under indsættelse af ny ikrafttrædelsesdato til databasen.", sqle);
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
