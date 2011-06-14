
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

package com.trifork.stamdata.importer.autorisationsregister;


import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.autorisationsregister.model.Autorisationsregisterudtraek;
import com.trifork.stamdata.importer.exceptions.FileImporterException;
import com.trifork.stamdata.persistence.AuditingPersister;

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
