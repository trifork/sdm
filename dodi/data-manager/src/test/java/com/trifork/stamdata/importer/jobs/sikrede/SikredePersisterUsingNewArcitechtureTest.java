/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.jobs.sikrede;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;
import com.trifork.stamdata.importer.persistence.AuditingPersister;

public class SikredePersisterUsingNewArcitechtureTest {

    Connection connection;
    SikredeFields exampleSikredeFields;
    SikredePersisterUsingNewArchitecture sikredePersister;
    
    @Before
    public void setupSikredePersister() throws SQLException
    {
        // TODO: Add test with two identical field names
        exampleSikredeFields = SikredeFields.newSikredeFields(
                "Foo", SikredeType.NUMERICAL, 2,
                "Moo", SikredeType.ALFANUMERICAL, 5
                );
        
        connection = MySQLConnectionManager.getConnection();
        
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE SikredeGenerated");
        setupStatements.executeUpdate(SikredeSqlSchemaCreator.createSqlSchema(exampleSikredeFields));
        
        AuditingPersister persister = new AuditingPersister(connection);
        sikredePersister = new SikredePersisterUsingNewArchitecture(new SikredeSqlStatementCreator(exampleSikredeFields), persister);
    }

    @Test
    public void testSimplePersistense() throws SQLException 
    {
        SikredeRecordBuilder builder = new SikredeRecordBuilder(exampleSikredeFields);
        SikredeRecord record = builder.field("Foo", 42).field("Moo", "Far").build();
        sikredePersister.persist(record);
        connection.commit();
        
        // TODO: Changes me to fetcher
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM SikredeGenerated");
        resultSet.next();
        long numberOfFoundRecords = resultSet.getLong(1);
        assertEquals(1, numberOfFoundRecords);
    }

}
