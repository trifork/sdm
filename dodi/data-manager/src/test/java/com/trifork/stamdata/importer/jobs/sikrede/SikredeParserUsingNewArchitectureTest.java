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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.jobs.sikrede.SikredeFields.SikredeType;
import com.trifork.stamdata.importer.persistence.Persister;
import com.trifork.stamdata.importer.util.Files;

import static org.mockito.Mockito.*;


public class SikredeParserUsingNewArchitectureTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    
    @Before
    public void setupParser()
    {
    }
  
    @Test
    public void testEmptyFile() throws Exception 
    {
        Connection connection = null;
        try 
        {
            SikredeFields sikredeFields = SikredeFields.SIKREDE_FIELDS_SINGLETON;

            SikredeLineParser entryParser = new SikredeLineParser(sikredeFields);
            SikredeSqlStatementCreator statementCreator = new SikredeSqlStatementCreator(sikredeFields);
            SikredeParserUsingNewArchitecture sikredeParser = new SikredeParserUsingNewArchitecture(entryParser,
                    statementCreator);

            connection = setupSikredeGeneratedDatabaseAndConnection(sikredeFields);

            File[] input = Files.toArray(setupExampleFile(sikredeFields));

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
            SikredeFields sikredeFields = SikredeFields.newSikredeFields(
                    "PostType", SikredeType.NUMERICAL, 2, 
                    "Foo", SikredeType.ALFANUMERICAL, 10);

            SikredeLineParser entryParser = new SikredeLineParser(sikredeFields);
            SikredeSqlStatementCreator statementCreator = new SikredeSqlStatementCreator(sikredeFields);
            SikredeParserUsingNewArchitecture sikredeParser = new SikredeParserUsingNewArchitecture(entryParser,
                    statementCreator);

            connection = setupSikredeGeneratedDatabaseAndConnection(sikredeFields);

            File[] input = Files.toArray(setupExampleFile(sikredeFields,
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
    
    private Connection setupSikredeGeneratedDatabaseAndConnection(SikredeFields sikredeFields) throws SQLException
    {
        Connection connection = MySQLConnectionManager.getConnection();
        
        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS SikredeGenerated");
        setupStatements.executeUpdate(SikredeSqlSchemaCreator.createSqlSchema(sikredeFields));
        
        return connection;
    }
    
    private File setupExampleFile(SikredeFields sikredeFields, SikredeRecord... records) throws IOException
    {
        SikredeRecordStringGenerator startStringGenerator = new SikredeRecordStringGenerator(SikredeParserUsingNewArchitecture.startRecordSikredeFields);
        SikredeRecordStringGenerator endStringGenerator = new SikredeRecordStringGenerator(SikredeParserUsingNewArchitecture.endRecordSikredeFields);
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(startStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 0, "Modt", "F053", "SnitfladeId", "S1061023"));
        builder.append('\n');

        SikredeRecordStringGenerator entryStringGenerator = new SikredeRecordStringGenerator(sikredeFields);
        for(SikredeRecord sikredeRecord: records)
        {
            builder.append(entryStringGenerator.stringFromIncompleteRecord(sikredeRecord));
            builder.append('\n');
        }
        
        builder.append(endStringGenerator.stringRecordFromIncompleteSetOfFields("PostType", 99, "AntPost", records.length));
        builder.append('\n');

        File file = temporaryFolder.newFile("foo.txt");
        
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, SikredeParserUsingNewArchitecture.FILE_ENCODING);
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
