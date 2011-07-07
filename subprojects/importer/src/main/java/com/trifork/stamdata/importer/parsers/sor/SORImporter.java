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

package com.trifork.stamdata.importer.parsers.sor;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.FileParser;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;
import com.trifork.stamdata.importer.parsers.exceptions.FileParseException;
import com.trifork.stamdata.importer.persistence.AuditingPersister;


public class SORImporter implements FileParser
{
	private static Logger logger = LoggerFactory.getLogger(SORImporter.class);

	@Override
	public boolean ensureRequiredFileArePresent(File[] input)
	{
		if (input.length == 0) return false;
		
		boolean present = false;

		for (File file : input)
		{
			if (file.getName().toLowerCase().endsWith(".xml")) present = true;
		}

		return present;
	}

	@Override
	public void importFiles(File[] files, Connection connection) throws FileImporterException
	{
		try
		{
			connection = MySQLConnectionManager.getConnection();
			AuditingPersister dao = new AuditingPersister(connection);
			for (File file : files)
			{
				SORDataSets dataSets = parse(file);
				dao.persistCompleteDataset(dataSets.getPraksisDS());
				dao.persistCompleteDataset(dataSets.getYderDS());
				dao.persistCompleteDataset(dataSets.getSygehusDS());
				dao.persistCompleteDataset(dataSets.getSygehusAfdelingDS());
				dao.persistCompleteDataset(dataSets.getApotekDS());
			}

			connection.commit();
		}
		catch (SQLException e)
		{
			try
			{
				connection.rollback();
			}
			catch (SQLException e1)
			{
				logger.error("Cannot rollback", e1);
			}

			String mess = "Error using database during import of autorisationsregister";
			logger.error(mess, e);
			throw new FileImporterException(mess, e);
		}
		finally
		{
			MySQLConnectionManager.close(connection);
		}
	}

	/**
	 * Should be updated every day
	 */
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Calendar cal = Calendar.getInstance();

		if (lastImport != null)
		{
			cal.setTime(lastImport);
		}

		cal.add(Calendar.DATE, 3);

		return cal.getTime();
	}
	
	public static SORDataSets parse(File file) throws FileParseException
	{
		SORDataSets dataSets = new SORDataSets();
		SOREventHandler handler = new SOREventHandler(dataSets);
		SAXParserFactory factory = SAXParserFactory.newInstance();

		try
		{
			SAXParser parser = factory.newSAXParser();

			if (file.getName().toUpperCase().endsWith("XML"))
			{
				parser.parse(file, handler);
			}
			else
			{
				logger.warn("Can only parse files with extension 'XML'! Ignoring: " + file.getAbsolutePath());
			}
		}
		catch (Exception e)
		{
			String errorMessage = "Error parsing data from file: " + file.getAbsolutePath();
			throw new FileParseException(errorMessage, e);
		}

		return dataSets;
	}
	
	@Override
	public String getIdentifier()
	{
		return "sor";
	}
}
