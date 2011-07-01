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

package com.trifork.stamdata.importer.parsers.sks;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.model.Dataset;
import com.trifork.stamdata.importer.parsers.FileImporterControlledIntervals;
import com.trifork.stamdata.importer.parsers.exceptions.FileImporterException;
import com.trifork.stamdata.importer.parsers.sks.model.Organisation;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import com.trifork.stamdata.importer.persistence.Persister;


public class SksImporter implements FileImporterControlledIntervals
{
	private static final Logger logger = LoggerFactory.getLogger(FileImporterControlledIntervals.class);

	/*
	 * SKS files usually arrive monthly
	 */
	@Override
	public Date getNextImportExpectedBefore(Date lastImport)
	{
		Calendar cal = Calendar.getInstance();

		if (lastImport != null)
		{
			cal.setTime(lastImport);
		}

		cal.add(Calendar.DATE, 45);
		return cal.getTime();
	}

	@Override
	public boolean checkRequiredFiles(List<File> files)
	{

		boolean present = false;
		for (File file : files)
		{
			if (file.getName().toUpperCase().endsWith(".TXT")) present = true;
		}
		return present;
	}

	@Override
	public void run(List<File> files) throws FileImporterException
	{
		Connection con = null;

		try
		{
			con = MySQLConnectionManager.getConnection();
			Persister dao = new AuditingPersister(con);
			for (File file : files)
			{
				if (file.getName().toUpperCase().endsWith(".TXT"))
				{
					Dataset<Organisation> dataset = SksParser.parseOrganisationer(file);
					logger.debug("Done parsing " + dataset.getEntities().size() + " from file: " + file.getName());
					dao.persistDeltaDataset(dataset);
				}
				else
				{
					logger.warn("Ignoring file, which neither matches *.TXT. File: " + file.getAbsolutePath());
				}
			}
			try
			{
				con.commit();
			}
			catch (SQLException e)
			{
				throw new FileImporterException("could not commit transaction", e);
			}
		}
		finally
		{
			MySQLConnectionManager.close(con);
		}
	}
}