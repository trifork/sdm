package com.trifork.stamdata.importer.jobs.sikrede;

import com.trifork.stamdata.importer.config.MySQLConnectionManager;
import com.trifork.stamdata.importer.persistence.AuditingPersister;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;

public class SikredeIntegrationTest {
    private Connection con;

    @Before
    public void cleanDatabase() throws Exception
    {
        con = MySQLConnectionManager.getConnection();

        Statement statement = con.createStatement();
        statement.execute("truncate table Sikrede");
        statement.execute("truncate table SikredeYderRelation");
        statement.execute("truncate table SaerligSundhedskort");
    }

    @After
    public void tearDown() throws Exception
    {
        con.rollback();
        con.close();
    }

    @Test
    public void canImportPersonWithSaerligSunhedskort() throws Exception {
        importFile("data/sikrede/20110701.SIKREDE_SSK");

        Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Sikrede");
		rs.next();
		assertEquals("Præcis én sygesikret forventes oprettet efter import",1, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM SaerligSundhedskort");
        rs.next();
        assertEquals("Præcis ét sundhedskort forventes efter import", 1, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM SikredeYderRelation");
        rs.next();
        assertEquals("Præcis én YderRelation forventes efter import", 1, rs.getInt(1));
    }

    @Test
    public void canImportSikredeWithoutSaerligSundhedskort() throws Exception {
        importFile("data/sikrede/20110701.SIKREDE");

        Statement stmt = con.createStatement();
		ResultSet rs = stmt.executeQuery("Select COUNT(*) from Sikrede");
		rs.next();
		assertEquals("Én sygesikret forventes oprettet efter import",1, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM SaerligSundhedskort");
        rs.next();
        assertEquals("Intet sundhedskort skal oprettes", 0, rs.getInt(1));

        rs = stmt.executeQuery("SELECT COUNT(*) FROM SikredeYderRelation");
        rs.next();
        assertEquals("current- previous and future YderRelation forventet efter import", 3, rs.getInt(1));
    }

    private void importFile(String fileName) throws Exception {
        SikredeParser parser = new SikredeParser();
        File file = FileUtils.toFile(getClass().getClassLoader().getResource(fileName));
        parser.importFiles(new File[] {file}, new AuditingPersister(con));
    }

}
