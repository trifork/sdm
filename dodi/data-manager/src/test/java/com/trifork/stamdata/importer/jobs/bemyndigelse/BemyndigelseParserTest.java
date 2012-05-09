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
package com.trifork.stamdata.importer.jobs.bemyndigelse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.BemyndigelseRecordSpecs;

public class BemyndigelseParserTest {

    private KeyValueStore keyValueStore;

    @Before
    public void setUp() throws Exception {
        keyValueStore = mock(KeyValueStore.class);

    }

    @Test
    public void parseXML() {

        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(Bemyndigelser.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Bemyndigelser bemyndigelser = (Bemyndigelser) jaxbUnmarshaller.unmarshal(file);

            assertEquals("v00001", bemyndigelser.getVersion());
            assertEquals("075052201", bemyndigelser.getTimestamp());

        } catch (JAXBException e) {
            e.printStackTrace();
            fail();
        }

    }

    @Test
    public void testParseFile() throws Exception {
        Connection connection = null;

        try {
            RecordSpecification recordSpecification = BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC;
            BemyndigelseParser parser = new BemyndigelseParser(keyValueStore);

            connection = setupDatabaseConnection(recordSpecification);
            File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/20120329_102310000_v1.bemyndigelse.xml"));

            parser.process(file, new RecordPersister(connection, Instant.now()));
            
            assertNumberOfBemyndigelserRecordsIsInDatabase(connection, 2, recordSpecification);
            
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + recordSpecification.getTable());
            while(rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                for(int i = 1; i< count; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i));
                    System.out.print(":");
                    System.out.println(rs.getString(i));
                }
            }
            
            
        } finally {
            if (connection != null) {
                connection.rollback();
            }
        }
    }

    @Test
    public void testParseFiles() throws Exception {
        Connection connection = null;

        try {
            RecordSpecification recordSpecification = BemyndigelseRecordSpecs.ENTRY_RECORD_SPEC;
            BemyndigelseParser parser = new BemyndigelseParser(keyValueStore);

            connection = setupDatabaseConnection(recordSpecification);
            File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/bemyndigelse/valid/"));

            parser.process(file, new RecordPersister(connection, Instant.now()));
            
            assertNumberOfBemyndigelserRecordsIsInDatabase(connection, 3, recordSpecification);
            
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM " + recordSpecification.getTable());
            while(rs.next()) {
                int count = rs.getMetaData().getColumnCount();
                for(int i = 1; i< count; i++) {
                    System.out.print(rs.getMetaData().getColumnName(i));
                    System.out.print(":");
                    System.out.println(rs.getString(i));
                }
            }
            
            
        } finally {
            if (connection != null) {
                connection.rollback();
            }
        }
    }

    private Connection setupDatabaseConnection(RecordSpecification recordSpecification) throws SQLException {
        Connection connection = new ConnectionManager().getConnection();

        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + recordSpecification.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(recordSpecification));

        return connection;
    }

    private void assertNumberOfBemyndigelserRecordsIsInDatabase(Connection connection, int i, RecordSpecification spec) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM " + spec.getTable());
        resultSet.next();
        assertEquals(i, resultSet.getLong(1));
    }

}
