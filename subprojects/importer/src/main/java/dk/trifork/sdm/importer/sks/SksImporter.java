package dk.trifork.sdm.importer.sks;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.StamdataVersionedDao;
import dk.trifork.sdm.dao.mysql.MySQLTemporalDao;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.importer.exceptions.FileImporterException;
import dk.trifork.sdm.importer.sks.model.Organisation;
import dk.trifork.sdm.model.Dataset;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

public class SksImporter implements FileImporterControlledIntervals {
    private final Logger logger = Logger.getLogger(getClass());

    @Override
    /*
      * SKS files usually arrive monthly
      */
    public Calendar getNextImportExpectedBefore(Calendar lastImport) {
        Calendar cal;
        if (lastImport == null)
            cal = Calendar.getInstance();
        else cal = ((Calendar) lastImport.clone());
        cal.add(Calendar.DATE, 45);
        return cal;
    }

    @Override
    public boolean checkRequiredFiles(List<File> files) {
    	boolean present = false;
        for (File file : files) {
            if (file.getName().toUpperCase().endsWith(".TXT")) 
                present = true;
        }
        return present;
    }

    @Override
    public void run(List<File> files) throws FileImporterException {
        Connection con = null;
        try {
            con = MySQLConnectionManager.getConnection();
            StamdataVersionedDao dao = new MySQLTemporalDao(con);
            for (File file : files) {
                if (file.getName().toUpperCase().endsWith(".TXT")) {
                    Dataset<Organisation> dataset = SksParser.parseOrganisationer(file);
                    logger.debug("Done parsing " + dataset.getEntities().size() + " from file: " + file.getName());
                    dao.persistDeltaDataset(dataset);
                } else {
                    logger.warn("Ignoring file, which neither matches *.TXT. File: " + file.getAbsolutePath());
                }
            }
            try {
                con.commit();
            } catch (SQLException e) {
                throw new FileImporterException("could not commit transaction", e);
            }
        } finally {
            MySQLConnectionManager.close(con);
        }

    }

}
