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

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.parsers.dkma.ParserException;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.util.Files;
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.persistence.RecordSpecification.SikredeType;

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

import static org.mockito.Mockito.*;

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
            RecordSpecification recordSpecification = RecordSpecification.SIKREDE_FIELDS_SINGLETON;

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser,
                    recordSpecification, "CPRnr");

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File[] input = Files.toArray(setupExampleFile(recordSpecification));

            Persister mockPersister = mock(Persister.class);
            when(mockPersister.getConnection()).thenReturn(connection);

            sikredeParser.parse(input, mockPersister, null);
            
            assertNumberOfSikredeGeneratedRecordsInDatabaseIs(connection, 0);
        } 
        catch (SQLException e)
        {
            throw new AssertionError(e);
        }
        finally
        {
            if(connection != null)
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
            RecordSpecification recordSpecification = RecordSpecification.newSikredeFields(
                    "PostType", SikredeType.NUMERICAL, 2,
                    "Foo", SikredeType.ALFANUMERICAL, 10);

            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser,
                    recordSpecification, "Foo");

            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);

            File[] input = Files.toArray(setupExampleFile(recordSpecification,
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "1234567890"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "ABCDEFGHIJ"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "Bar"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "BarBaz")));

            Persister mockPersister = mock(Persister.class);
            when(mockPersister.getConnection()).thenReturn(connection);

            sikredeParser.parse(input, mockPersister, null);

            connection.commit();

            assertNumberOfSikredeGeneratedRecordsInDatabaseIs(connection, 4);
        } 
        catch (SQLException e)
        {
            throw new AssertionError(e);
        }
        finally
        {
            if(connection != null)
            {
                connection.rollback();
            }
        }
    }

    @Test(expected=ParserException.class)
    public void testIllegalStartRecord() throws Exception 
    {
        Connection connection = null;
        try 
        {
            RecordSpecification recordSpecification = RecordSpecification.newSikredeFields(
                    "PostType", SikredeType.NUMERICAL, 2,
                    "Foo", SikredeType.ALFANUMERICAL, 10);
            
            SingleLineRecordParser entryParser = new SingleLineRecordParser(recordSpecification);
            SikredeParser sikredeParser = new SikredeParser(entryParser,
                    recordSpecification, "Foo");
            
            connection = setupSikredeGeneratedDatabaseAndConnection(recordSpecification);
            
            File[] input = Files.toArray(setupExampleFileWithIllegalModtager(recordSpecification,
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "1234567890"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "ABCDEFGHIJ"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "Bar"),
                    SikredeRecordStringGenerator.sikredeRecordFromKeysAndValues("PostType", 10, "Foo", "BarBaz")));
            
            Persister mockPersister = mock(Persister.class);
            when(mockPersister.getConnection()).thenReturn(connection);
            
            sikredeParser.parse(input, mockPersister, null);
        } 
        catch (SQLException e)
        {
            throw new AssertionError(e);
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
        Connection connection = MySQLConnectionManager.getConnection();
        
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));
        
        return connection;
    }
    
    private File setupExampleFile(RecordSpecification recordSpecification, Record... records) throws IOException
    {
        SikredeRecordStringGenerator startStringGenerator = new SikredeRecordStringGenerator(SikredeParser.START_RECORD_RECORD_SPECIFICATION);
        SikredeRecordStringGenerator endStringGenerator = new SikredeRecordStringGenerator(SikredeParser.END_RECORD_RECORD_SPECIFICATION);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(startStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F053", "SnitfladeId", "S1061023"));
        builder.append('\n');

        SikredeRecordStringGenerator entryStringGenerator = new SikredeRecordStringGenerator(recordSpecification);
        for(Record record : records)
        {
            builder.append(entryStringGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }
        
        builder.append(endStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');

        File file = temporaryFolder.newFile("foo.txt");
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();        
        
        return file;
    }

    private File setupExampleFileWithIllegalModtager(RecordSpecification recordSpecification, Record... records) throws IOException
    {
        SikredeRecordStringGenerator startStringGenerator = new SikredeRecordStringGenerator(SikredeParser.START_RECORD_RECORD_SPECIFICATION);
        SikredeRecordStringGenerator endStringGenerator = new SikredeRecordStringGenerator(SikredeParser.END_RECORD_RECORD_SPECIFICATION);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(startStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F042", "SnitfladeId", "S1061023"));
        builder.append('\n');
        
        SikredeRecordStringGenerator entryStringGenerator = new SikredeRecordStringGenerator(recordSpecification);
        for(Record record : records)
        {
            builder.append(entryStringGenerator.stringFromIncompleteRecord(record));
            builder.append('\n');
        }
        
        builder.append(endStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');
        
        File file = temporaryFolder.newFile("foo.txt");
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParser.FILE_ENCODING);
        outputStreamWriter.write(builder.toString());
        outputStreamWriter.flush();
        fileOutputStream.close();        
        
        return file;
    }
    
    private void assertNumberOfSikredeGeneratedRecordsInDatabaseIs(Connection connection, int i) throws SQLException 
    {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM SikredeGenerated");
        resultSet.next();
        assertEquals(i, resultSet.getLong(1));
    }
}
