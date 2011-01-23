package com.trifork.stamdata.importer.jobs;


import static com.trifork.stamdata.importer.persistence.ConnectionFactory.Databases.HOUSEKEEPING;
import static com.trifork.stamdata.importer.persistence.ConnectionFactory.Databases.SDM;
import static java.lang.String.format;
import static org.slf4j.LoggerFactory.getLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.cpr.CPRDataset;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;
import com.trifork.stamdata.importer.persistence.MySQLTemporalDao;
import com.trifork.stamdata.registre.cpr.Klarskriftadresse;
import com.trifork.stamdata.registre.cpr.Navneoplysninger;


/**
 * Restored unlisted named to the CPR tables after a unlisting
 * (navnebeskyttelse) has expired.
 */
public class NavnebeskyttelseRestrukt implements Runnable
{
	private static final Logger LOGGER = getLogger(NavnebeskyttelseRestrukt.class);

	private final String SELECT_SQL;
	private final String DELETE_SQL;

	private final ConnectionFactory factory;


	public NavnebeskyttelseRestrukt(ConnectionFactory factory)
	{
		this.factory = factory;

		SELECT_SQL = format("SELECT * FROM AdresseBeskyttelse WHERE NavneBeskyttelseSletteDato < NOW()");
		DELETE_SQL = format("DELETE FROM AdresseBeskyttelse WHERE CPR = ?");
	}


	public void run()
	{
		Connection connection = null;

		try
		{
			connection = factory.getConnection(false, SDM);

			PreparedStatement deleteStatement = connection.prepareStatement(DELETE_SQL);

			// Find all expired records.
			Statement stm = connection.createStatement();
			ResultSet expiredRecords = stm.executeQuery(SELECT_SQL);

			CPRDataset cprDS = new CPRDataset();

			// TODO (thb): Why is it we set the millisecs to 0?

			Calendar now = Calendar.getInstance();
			now.set(Calendar.MILLISECOND, 0);
			cprDS.setValidFrom(now.getTime());

			while (expiredRecords.next())
			{
				// Restore the address and name of the person
				// to its original value.

				Navneoplysninger navneoplysninger = new Navneoplysninger();
				Klarskriftadresse klarskriftadresse = new Klarskriftadresse();

				String cpr = expiredRecords.getString("CPR");
				navneoplysninger.setCpr(cpr);
				klarskriftadresse.setCpr(cpr);

				String fornavn = expiredRecords.getString("Fornavn");
				navneoplysninger.setFornavn(fornavn);

				String mellemnavn = expiredRecords.getString("Mellemnavn");
				navneoplysninger.setMellemnavn(mellemnavn);

				String efternavn = expiredRecords.getString("Efternavn");
				navneoplysninger.setEfternavn(efternavn);

				String coNavn = expiredRecords.getString("CoNavn");
				klarskriftadresse.setCoNavn(coNavn);

				String lokalitet = expiredRecords.getString("Lokalitet");
				klarskriftadresse.setLokalitet(lokalitet);

				String vejnavn = expiredRecords.getString("Vejnavn");
				klarskriftadresse.setVejNavn(vejnavn);

				String bygningsnummer = expiredRecords.getString("Bygningsnummer");
				klarskriftadresse.setBygningsNummer(bygningsnummer);

				String husnummer = expiredRecords.getString("Husnummer");
				klarskriftadresse.setHusNummer(husnummer);

				String etage = expiredRecords.getString("Etage");
				klarskriftadresse.setEtage(etage);

				String sideDoerNummer = expiredRecords.getString("SideDoerNummer");
				klarskriftadresse.setSideDoerNummer(sideDoerNummer);

				String bynavn = expiredRecords.getString("Bynavn");
				klarskriftadresse.setByNavn(bynavn);

				Long postnummer = expiredRecords.getLong("Postnummer");
				klarskriftadresse.setPostNummer(postnummer);

				String postDistrikt = expiredRecords.getString("PostDistrikt");
				klarskriftadresse.setPostDistrikt(postDistrikt);

				Long vejKode = expiredRecords.getLong("VejKode");
				klarskriftadresse.setVejKode(vejKode);

				Long kommuneKode = expiredRecords.getLong("KommuneKode");
				klarskriftadresse.setKommuneKode(kommuneKode);

				cprDS.addEntity(navneoplysninger);
				cprDS.addEntity(klarskriftadresse);

				// Queue the person to be deleted from the register.

				deleteStatement.setString(1, cpr);
				deleteStatement.addBatch();
			}

			// Store all the updated info.
			MySQLTemporalDao dao = new MySQLTemporalDao(connection);
			dao.persistDeltaDataset(cprDS.getNavneoplysninger());
			dao.persistDeltaDataset(cprDS.getKlarskriftadresse());

			// Delete the processed records from the register.

			factory.setCatalog(connection, HOUSEKEEPING);
			deleteStatement.executeBatch();

			// Commit the changes.

			connection.commit();
		}
		catch (FilePersistException e)
		{
			LOGGER.error("Exception thrown while restoring unlisted names.", e);
		}
		catch (SQLException e)
		{
			LOGGER.error("Database exception exception while restoring unlisted names.", e);
		}
		finally
		{
			factory.close(connection);
		}
	}
}
