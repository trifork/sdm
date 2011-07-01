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

package com.trifork.stamdata.importer.parsers.autorisationsregister;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.parsers.autorisationsregister.model.Autorisationsregisterudtraek;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;
import com.trifork.stamdata.importer.persistence.AuditingPersister;


public class AutImporter implements FileImporterControlledIntervals
{
	@Override
	public boolean checkRequiredFiles(List<File> files)
	{
		// It doesn't seem like we know anything about
		// what the required files are. Therefore we just
		// make sure that there are some.

		checkNotNull(files);
		checkArgument(files.size() > 0, "You must not call this method with an empty set of files. This makes no sense.");

		return true;
	}

	@Override
	public void run(List<File> files) throws FileImporterException
	{
		Connection connection = null;

		try
		{
			connection = MySQLConnectionManager.getConnection();
			AuditingPersister dao = new AuditingPersister(connection);
			doImport(files, dao);
			connection.commit();
		}
		catch (SQLException e)
		{
			throw new FileImporterException("Error using database during import of autorisationsregisteret.", e);
		}
		finally
		{
			MySQLConnectionManager.close(connection);
		}
	}

	/**
	 * Import Autorisationsregister-files using the Dao
	 *
	 * @param files, the files from which the Autorisationer should be parsed
	 * @param dao, the dao to which Autorisationer should be saved
	 * @throws SQLException If something goes wrong in the DAO
	 * @throws FileImporterException If importing fails
	 */
	public void doImport(List<File> files, AuditingPersister dao) throws FileImporterException
	{
		for (File file : files)
		{
			Date date = parseDate(file.getName());

			try
			{
				Autorisationsregisterudtraek dataset = AutorisationsregisterParser.parse(file, date);
				dao.persistCompleteDataset(dataset);
			}
			catch (Exception e)
			{
				throw new FileImporterException("Error reading file: " + file, e);
			}
		}
	}

	private Date parseDate(String filename) throws FileImporterException
	{
		try
		{
			return getDateFromFilename(filename);
		}
		catch (ParseException e)
		{
			throw new FileImporterException("Filename format is invalid! Date could not be extracted.", e);
		}
	}

	public Date getDateFromFilename(String filename) throws ParseException
	{
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
			return formatter.parse(filename.substring(0, 8));
		}
		catch (IndexOutOfBoundsException e)
		{
			throw new ParseException("Filename was too short to contain a date.", 8);
		}
	}

	/**
	 * Largest gap observed was 15 days from 2008-10-18 to 2008-11-01
	 */
	@Override
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Calendar cal = Calendar.getInstance();

		if (lastImport != null)
		{
			cal.setTime(lastImport);
		}

		cal.add(Calendar.MONTH, 1);

		return cal.getTime();
	}
}
