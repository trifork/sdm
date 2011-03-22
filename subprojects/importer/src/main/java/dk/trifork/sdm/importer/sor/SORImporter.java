package dk.trifork.sdm.importer.sor;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.mysql.AuditingPersister;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class SORImporter implements FileImporterControlledIntervals {
	private static Logger logger = Logger.getLogger(SORImporter.class);
	
    @Override
    public boolean checkRequiredFiles(List<File> files) {
        if (files.size() == 0)
            return false;
        boolean xmlpresent = false;
        for (File file : files) {
            if (file.getName().endsWith(".xml"))
            	xmlpresent = true;
        }
        return xmlpresent;
    }

    @Override
    public void run(List<File> files) throws FileImporterException {
        Connection connection = null;
        try {
            connection = MySQLConnectionManager.getConnection();
            AuditingPersister dao = new AuditingPersister(connection);
            for (File file : files) {
            	SORDataSets dataSets = SORParser.parse(file);
                dao.persistCompleteDataset(dataSets.getPraksisDS());
                dao.persistCompleteDataset(dataSets.getYderDS());
                dao.persistCompleteDataset(dataSets.getSygehusDS());
                dao.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
                dao.persistCompleteDataset(dataSets.getApotekDS());
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.error("Cannot rollback", e1);
            }
            String mess = "Error using database during import of autorisationsregister";
            logger.error(mess, e);
            throw new FileImporterException(mess, e);
        } finally {
            MySQLConnectionManager.close(connection);
        }
    }

    /**
     * Should be updated every day 
     */
    @Override
    public Calendar getNextImportExpectedBefore(Calendar lastImport) {
        Calendar cal;
        if (lastImport == null)
            cal = Calendar.getInstance();
        else cal = ((Calendar) lastImport.clone());
        cal.add(Calendar.DATE, 3);
        return cal;
	}

}
