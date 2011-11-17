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

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.parsers.ParserException;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.RecordFieldType;

import com.trifork.stamdata.specs.SikredeRecordSpecs;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;


public class SikredeParserTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testEmptyFile() throws Exception 
    {
        Connection connection = null;

        try
        {
            RecordSpecification recordSpecification = SikredeRecordSpecs.ENTRY_RECORD_SPEC;

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser, recordSpecification);

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File input = setupExampleFile(recordSpecification);

            sikredeParser.process(input, new RecordPersister(connection, Instant.now()));
            
            assertNumberOfSikredeGeneratedRecordsInDatabaseIs(connection, 0, recordSpecification);
        }
        finally
        {
            if (connection != null)
            {
                connection.rollback();
            }
        }
    }

    @Test
    public void testAbleToInsertFourRecords() throws Exception 
    {
        Connection connection = null;

        try 
        {
            RecordSpecification recordSpecification = RecordSpecification.createSpec("TestTable", "Foo",
                    "PostType", RecordFieldType.NUMERICAL, 2,
                    "Foo", RecordFieldType.ALPHANUMERICAL, 10);

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser, recordSpecification);

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File inbox = setupExampleFile(recordSpecification,
                    RecordGenerator.createRecord("PostType", 10, "Foo", "1234567890"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "ABCDEFGHIJ"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "Bar"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "BarBaz"));

            sikredeParser.process(inbox, new RecordPersister(connection, Instant.now()));

            connection.commit();

            assertNumberOfSikredeGeneratedRecordsInDatabaseIs(connection, 4, recordSpecification);
        }
        finally
        {
            if (connection != null)
            {
                connection.rollback();
            }
        }
    }

    @Test(expected = ParserException.class)
    public void testIllegalStartRecord() throws Exception 
    {
        Connection connection = null;

        try
        {
            RecordSpecification recordSpecification = RecordSpecification.createSpec("TestTable", "Foo",
                    "PostType", RecordFieldType.NUMERICAL, 2,
                    "Foo", RecordFieldType.ALPHANUMERICAL, 10);
            
            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser, recordSpecification);
            
            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);
            
            File inbox = setupExampleFileWithIllegalReceiverId(recordSpecification,
                    RecordGenerator.createRecord("PostType", 10, "Foo", "1234567890"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "ABCDEFGHIJ"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "Bar"),
                    RecordGenerator.createRecord("PostType", 10, "Foo", "BarBaz"));
            
            sikredeParser.process(inbox, new RecordPersister(connection, Instant.now()));
        }
        finally
        {
            if(connection != null)
            {
                connection.rollback();
            }
        }
    }
    
    private Connection setupSikredeGeneratedDatabaseAndConnection(RecordSpecification recordSpecification) throws SQLException
    {
        Connection connection = new ConnectionManager().getConnection();
        
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
        
        return connection;
    }
    
    private File setupExampleFile(RecordSpecification recordSpecification, Record... records) throws IOException
    {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F053", "SnitfladeId", "S1061023"));
        builder.append('\n');

        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records)
        {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }
        
        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');

        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();        
        
        return inbox;
    }

    private File setupExampleFileWithIllegalReceiverId(RecordSpecification recordSpecification, Record... records) throws IOException
    {
        RecordGenerator startGenerator = new RecordGenerator(SikredeRecordSpecs.START_RECORD_SPEC);
        RecordGenerator endGenerator = new RecordGenerator(SikredeRecordSpecs.END_RECORD_SPEC);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(startGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F042", "SnitfladeId", "S1061023"));
        builder.append('\n');
        
        RecordGenerator entryGenerator = new RecordGenerator(recordSpecification);

        for (Record record : records)
        {
            builder.append(entryGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }
        
        builder.append(endGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');
        
        File inbox = temporaryFolder.newFolder("foo");
        File file = temporaryFolder.newFile("foo/foo.txt");
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();        
        
        return inbox;
    }
    
    private void assertNumberOfSikredeGeneratedRecordsInDatabaseIs(Connection connection, int i, RecordSpecification spec) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM " + spec.getTable());
        resultSet.next();
        assertEquals(i, resultSet.getLong(1));
    }
}
