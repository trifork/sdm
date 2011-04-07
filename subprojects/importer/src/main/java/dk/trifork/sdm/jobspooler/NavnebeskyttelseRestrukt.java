package dk.trifork.sdm.jobspooler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.importer.cpr.model.CPRDataset;
import dk.trifork.sdm.importer.cpr.model.Klarskriftadresse;
import dk.trifork.sdm.importer.cpr.model.Navneoplysninger;

public class NavnebeskyttelseRestrukt implements Job {
    private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run() throws JobException {
		// Check that elements in table 'AdresseBeskyttelse' haven't expired 
        Connection connection = null;
        try {
            logger.debug("Starting checking for expired name and address protection");
            connection = MySQLConnectionManager.getConnection();
            
            Statement stm = connection.createStatement();
			ResultSet rs = stm.executeQuery(createSelectExpiredNameProtectionSQL());
			
			CPRDataset cprDS = new CPRDataset();
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			now.set(Calendar.MILLISECOND, 0);
			cprDS.setValidFrom(now);
			
			int nbExpired = 0;
			String sqlIN = "";
			
			while(rs.next()) {
				++nbExpired;
				Navneoplysninger no = new Navneoplysninger();
				Klarskriftadresse ka = new Klarskriftadresse(); 
				
				String cPR = rs.getString("CPR");
				no.setCpr(cPR);
				ka.setCpr(cPR);
				sqlIN += (sqlIN.isEmpty()) ? cPR : ", " + cPR;
				String fornavn = rs.getString("Fornavn");
				no.setFornavn(fornavn);
				String mellemnavn = rs.getString("Mellemnavn");
				no.setMellemnavn(mellemnavn);
				String efternavn = rs.getString("Efternavn");
				no.setEfternavn(efternavn);
				String coNavn = rs.getString("CoNavn");
				ka.setCoNavn(coNavn);
				String lokalitet = rs.getString("Lokalitet");
				ka.setLokalitet(lokalitet);
				String vejnavn = rs.getString("Vejnavn");
				ka.setVejNavn(vejnavn);
				String bygningsnummer = rs.getString("Bygningsnummer");
				ka.setBygningsNummer(bygningsnummer);
				String husnummer= rs.getString("Husnummer");
				ka.setHusNummer(husnummer);
				String etage = rs.getString("Etage");
				ka.setEtage(etage);
				String sideDoerNummer = rs.getString("SideDoerNummer");
				ka.setSideDoerNummer(sideDoerNummer);
				String bynavn = rs.getString("Bynavn");
				ka.setByNavn(bynavn);
				Long postnummer = rs.getLong("Postnummer");
				ka.setPostNummer(postnummer);
				String postDistrikt = rs.getString("PostDistrikt");
				ka.setPostDistrikt(postDistrikt);
				Long vejKode = rs.getLong("VejKode");
				ka.setVejKode(vejKode);
				Long kommuneKode = rs.getLong("KommuneKode");
				ka.setKommuneKode(kommuneKode);
				
				cprDS.addEntity(no);
				cprDS.addEntity(ka);
			}

			if (nbExpired > 0) {
	            logger.debug("Persisting " + nbExpired + " expired name and address protection records");
	            AuditingPersister dao = new AuditingPersister(connection);
	            dao.persistDeltaDataset(cprDS.getDataset(Navneoplysninger.class));
	            dao.persistDeltaDataset(cprDS.getDataset(Klarskriftadresse.class));
	            
	            connection.createStatement().execute(createDeleteExpiredNameProtectionSQL(sqlIN));
	            
	            connection.commit();
			}
            
        } catch (Exception e)  {
        	throw new JobException("Caught an exception during restoring expired nameprotection. Message = " + e.getMessage());
        } finally {
            MySQLConnectionManager.close(connection);
        }

	}
	
	static private String createSelectExpiredNameProtectionSQL() {
		String SQL = "SELECT * FROM " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse " +
				"WHERE NavneBeskyttelseSletteDato < now()";
		
		return SQL;
	}

	static private String createDeleteExpiredNameProtectionSQL(String inList) {
		String SQL = "DELETE FROM " + MySQLConnectionManager.getHousekeepingDBName() + ".AdresseBeskyttelse " +
				"WHERE CPR IN (" + inList + ")";
		
		return SQL;
	}
}
