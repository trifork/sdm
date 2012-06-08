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
package com.trifork.stamdata.importer.jobs.sor.relations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.helpers.DefaultValidationEventHandler;

import oio.sundhedsstyrelsen.organisation._1_0.SorTreeType;

import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.importer.config.ConnectionManager;
import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.persistence.RecordMySQLTableGenerator;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SorRelationsRecordSpecs;

public class SorRelationParserTest {

    private KeyValueStore keyValueStore;

    @Before
    public void setUp() throws Exception {
        keyValueStore = mock(KeyValueStore.class);

    }

    @Test
    public void parseXML() {

        File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/SOR_FULL.xml"));

        
        JAXBContext jaxbContext;
        try {
            System.setProperty("jaxb.debug", "true");

            jaxbContext = JAXBContext.newInstance(SorTreeType.class.getPackage().getName());
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            jaxbUnmarshaller.setEventHandler(new XMLValidationEventHandler());
            JAXBElement<SorTreeType> jaxbSOR = (JAXBElement<SorTreeType>) jaxbUnmarshaller.unmarshal(file);
            
            SorTreeType sor = jaxbSOR.getValue();
            
            assertNotNull(sor);
            assertNotNull(sor.getInstitutionOwnerEntity());
            assertEquals(6934, sor.getInstitutionOwnerEntity().size());
            
        } catch (JAXBException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testParseFile() throws Exception {
        Connection connection = null;

        try {
            RecordSpecification recordSpecification = SorRelationsRecordSpecs.RELATIONS_RECORD_SPEC;
            RecordSpecification shakYderSpecification = SorRelationsRecordSpecs.SHAK_YDER_RECORD_SPEC;

            SorRelationParser parser = new SorRelationParser(keyValueStore);

            connection = setupDatabaseConnection(recordSpecification, shakYderSpecification);
            File file = FileUtils.toFile(getClass().getClassLoader().getResource("data/sor/SOR_FULL.xml"));

            parser.process(file, new RecordPersister(connection, Instant.now()));
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT Count(*) FROM " + recordSpecification.getTable() + " WHERE sor_parent = '21000016006'");
            resultSet.next();
            assertEquals(24, resultSet.getLong(1));
            
            Statement statement2 = connection.createStatement();
            ResultSet resultSet2 = statement2.executeQuery("SELECT sor FROM " + shakYderSpecification.getTable() + " WHERE shak_yder = 'SHAK=804301'");
            resultSet2.next();
            assertEquals("278591000016002", resultSet2.getString(1));

            Statement statement3 = connection.createStatement();
            ResultSet resultSet3 = statement3.executeQuery("SELECT sor FROM " + shakYderSpecification.getTable() + " WHERE shak_yder = 'Yder=045756'");
            int count = 0;
            boolean hasSor1 = false;
            boolean hasSor2 = false;
            while (resultSet3.next()) {
                count++;
                String sor = resultSet3.getString(1);
                if(sor.equals("8301000016000")) {hasSor1 = true;}
                if(sor.equals("8311000016003")) {hasSor2 = true;}
            }
            assertEquals(2, count);
            assertTrue(hasSor1);
            assertTrue(hasSor2);
        } finally {
            if (connection != null) {
                connection.rollback();
            }
        }
    }

    
    private Connection setupDatabaseConnection(RecordSpecification sorRelations, RecordSpecification shakYder) throws SQLException {
        Connection connection = new ConnectionManager().getConnection();

        Statement setupStatements = connection.createStatement();
        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + sorRelations.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(sorRelations));

        setupStatements.executeUpdate("DROP TABLE IF EXISTS " + shakYder.getTable());
        setupStatements.executeUpdate(RecordMySQLTableGenerator.createSqlSchema(shakYder));

        return connection;
    }

    class XMLValidationEventHandler extends DefaultValidationEventHandler {
        
        @Override
        public boolean handleEvent(ValidationEvent event) {
            //System.out.println(event.toString());
            return true;
        }
        
    }
}
