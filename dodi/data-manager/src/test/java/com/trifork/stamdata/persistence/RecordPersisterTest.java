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
package com.trifork.stamdata.persistence;

import static com.trifork.stamdata.persistence.RecordSpecification.field;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.inject.Provider;
import com.trifork.stamdata.importer.config.ConnectionManager;

public class RecordPersisterTest
{
    private Connection connection;
    private RecordSpecification recordSpecification;
    private RecordPersister persister;
    private Instant transactionTime;
    private RecordFetcher fetcher;

    @Before
    public void setUp() throws SQLException
    {
        // TODO: Add test with two identical field names
        recordSpecification = RecordSpecification.createSpecification("SikredeTest", "Moo",
                field("Foo", 2).numerical(),
                field("Moo", 5)
        );
        
        connection = new ConnectionManager().getConnection();
        createSikredeFieldsTableOnDatabase(connection, recordSpecification);
        transactionTime = new DateTime(2011, 5, 29, 0, 0, 0).toInstant();
        persister = new RecordPersister(connection, transactionTime);
        Provider provider = Mockito.mock(Provider.class);
        Mockito.when(provider.get()).thenReturn(connection);
        fetcher = new RecordFetcher(provider);
    }

    @After
    public void tearDown() throws SQLException
    {
        ConnectionManager.closeQuietly(connection);
    }
    
    @Test
    public void testAddingTheSameRecordTwiceButWithNeverTimestamp() throws SQLException 
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record recordA = builder.field("Foo", 42).field("Moo", "Bar").build();
        Record recordB = builder.field("Foo", 23).field("Moo", "Bar").build();

        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        RecordPersister persisterIn2000 = new RecordPersister(connection, theYear2000.toInstant());
        persisterIn2000.persist(recordA, recordSpecification);
        connection.commit();

        DateTime theYear2010 = theYear2000.plusYears(10);
        RecordPersister persisterIn2010 = new RecordPersister(connection, theYear2010.toInstant());
        persisterIn2010.persist(recordB, recordSpecification);
        connection.commit();

        Record record = fetcher.fetchCurrent("Bar", recordSpecification);
        assertThat((Integer) record.get("Foo"), is(23));
    }

    @Test
    public void testAddingTwoDifferentRecordsDontEffectEachOther() throws SQLException 
    {
        Record recordA = new RecordBuilder(recordSpecification).field("Foo", 42).field("Moo", "Far").build();
        Record recordB = new RecordBuilder(recordSpecification).field("Foo", 23).field("Moo", "Bar").build();
        
        persister.persist(recordA, recordSpecification);
        connection.commit();
        
        persister.persist(recordB, recordSpecification);
        connection.commit();

        PreparedStatement countStmt = connection.prepareStatement("SELECT Count(*) FROM " + recordSpecification.getTable() + " WHERE validTo IS NULL");
        ResultSet countResultSet = countStmt.executeQuery();
        countResultSet.next();
        assertEquals(2, countResultSet.getLong(1));

        Record recordAExpected = fetcher.fetchCurrent("Far", recordSpecification);
        Record recordBExpected = fetcher.fetchCurrent("Bar", recordSpecification);

        assertEquals(recordB, recordBExpected);
        assertEquals(recordA, recordAExpected);
    }

    @Ignore("Left for future release")
    @Test(expected=IllegalArgumentException.class)
    public void testAddingTheSameRecordWithAnEarlierTimestampRaisesAnException() throws SQLException
    {
        RecordBuilder builder = new RecordBuilder(recordSpecification);
        Record record = builder.field("Foo", 42).field("Moo", "Far").build();

        DateTime theYear2000 = new DateTime(2000, 1, 1, 0, 0);
        DateTime theYear2010 = theYear2000.plusYears(10);

        RecordPersister persisterIn2010 = new RecordPersister(connection, theYear2010.toInstant());
        persisterIn2010.persist(record, recordSpecification);
        connection.commit();

        RecordPersister persisterIn2000 = new RecordPersister(connection, theYear2000.toInstant());
        persisterIn2000.persist(record, recordSpecification);
        connection.commit();
    }

    @Test
    public void testInsertStatementString() 
    {
        String expected = "INSERT INTO SikredeTest (Foo, Moo, ValidFrom, ModifiedDate) VALUES (?, ?, ?, ?)";
        String actual = persister.createInsertStatementSql(recordSpecification);
        assertEquals(expected, actual);
    }

    private void createSikredeFieldsTableOnDatabase(Connection connection, RecordSpecification recordSpecification) throws SQLException
    {
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
    }
}
