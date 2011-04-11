// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package dk.trifork.sdm.importer.autorisationsregister;

import dk.trifork.sdm.config.MySQLConnectionManager;
import dk.trifork.sdm.dao.AuditingPersister;
import dk.trifork.sdm.importer.FileImporterControlledIntervals;
import dk.trifork.sdm.importer.autorisationsregister.model.Autorisationsregisterudtraek;
import dk.trifork.sdm.importer.exceptions.FileImporterException;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutImporter implements FileImporterControlledIntervals {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public boolean checkRequiredFiles(List<File> files) {
        if (files.size() == 0)
            return false;
        for (File file : files) {
            if (getDateFromInputFileName(file.getName()) == null)
                return false;
        }
        return true;
    }

    @Override
    public void run(List<File> files) throws FileImporterException {
        Connection connection = null;
        try {
            connection = MySQLConnectionManager.getConnection();
            AuditingPersister dao = new AuditingPersister(connection);
            doImport(files, dao);
            connection.commit();
        } catch (SQLException e) {
            String mess = "Error using database during import of autorisationsregister";
            logger.error(mess, e);
            throw new FileImporterException(mess, e);
        }
        finally {
            MySQLConnectionManager.close(connection);
        }
    }

    /**
     * Import Autorisationsregister-files using the Dao
     *
     * @param files, the files from which the Autorisationer should be parsed
     * @param dao,   the dao to which Autorisationer should be saved
     * @throws SQLException          If something goes wrong in the DAO
     * @throws FileImporterException If importing fails
     */
    void doImport(List<File> files, AuditingPersister dao) throws FileImporterException {
        for (File file : files) {
            Calendar date = getDateFromInputFileName(file.getName());
            if (date == null)
                throw new FileImporterException("Filename format is invalid! Date could not be extracted");
            try {
                Autorisationsregisterudtraek dataset = AutorisationsregisterParser.parse(file, date);
                dao.persistCompleteDataset(dataset);

            } catch (Exception e) {
                String mess = "Error reader autorisationsfil: " + file;
                logger.error(mess, e);
                throw new FileImporterException(mess, e);
            }

        }

    }


    /**
     * Extracts the date from the filename
     *
     * @param fileName
     * @return
     */
    public Calendar getDateFromInputFileName(String fileName) {
        try {
            int year = new Integer(fileName.substring(0, 4));
            int month = new Integer(fileName.substring(4, 6));
            int date = new Integer(fileName.substring(6, 8));
            return new GregorianCalendar(year, month - 1, date);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Largest gap observed was 15 days from 2008-10-18 to 2008-11-01
     */
    @Override
    public Calendar getNextImportExpectedBefore(Calendar lastImport) {
        Calendar cal;
        if (lastImport == null)
            cal = Calendar.getInstance();
        else cal = ((Calendar) lastImport.clone());
        cal.add(Calendar.MONTH, 1);
		return cal;
	}

}
