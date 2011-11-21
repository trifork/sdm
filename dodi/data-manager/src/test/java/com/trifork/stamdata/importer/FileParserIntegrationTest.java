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
package com.trifork.stamdata.importer;

import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.Service;
import com.trifork.stamdata.importer.config.ConnectionManager;
import dk.nsi.stamdata.testing.TestServer;
import org.apache.commons.io.FileUtils;
import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static com.jayway.restassured.RestAssured.get;
import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public abstract class FileParserIntegrationTest
{
    private TestServer server;
    private int port = 8772;

    private final String parserId;
    private Connection connection;

    @Before
    public final void before() throws Exception
    {
        server = new TestServer().port(port).start();

        connection = new ConnectionManager().getConnection();

        // HACK: This should be generic

        Statement statement = connection.createStatement();
        statement.execute("TRUNCATE TABLE Yderregister");
        statement.execute("TRUNCATE TABLE KeyValueStore");
        statement.execute("TRUNCATE TABLE Import");
        statement.execute("TRUNCATE TABLE YderregisterPerson");
        statement.execute("TRUNCATE TABLE Sikrede");

        statement.close();
    }

    @After
    public final void after() throws Exception
    {
        server.stop();

        connection.close();
    }

    /**
     * @param parserId the unique id of the parser whose inbox to place the files in.
     */
    protected FileParserIntegrationTest(String parserId)
    {
        this.parserId = parserId;
    }

    /**
     * Copies files from a test resource folder and places them in a parser's inbox.
     *
     * @param inboxToCopy a directory containing the files to copy.
     * @throws IOException thrown if the files could not be copied.
     */
    protected void placeInInbox(File inboxToCopy, boolean useSubdirectory) throws IOException, InterruptedException
    {
        checkNotNull(parserId, "parserId");
        checkNotNull(inboxToCopy, "inboxToCopy");
        checkArgument(inboxToCopy.isDirectory(), "inboxToCopy must be a directory");

        File destination;

        if (useSubdirectory)
        {
            String subDirName = Instant.now().toString(ISODateTimeFormat.dateTime());
            destination = new File(getInboxPath(), subDirName);
        }
        else
        {
            destination = getInboxPath();
        }

        System.out.println(inboxToCopy.getAbsolutePath());
        System.out.println(destination.getAbsolutePath());

        FileUtils.copyDirectory(inboxToCopy, destination);

        assertThat(destination.exists(), is(true));

        Thread.sleep(10000);

        while (isInProgress()) { Thread.sleep(500); }
    }
    
    private File getInboxPath()
    {
        String inboxPath = get(getParserURL() + "/inbox").andReturn().print();
        return new File(inboxPath);
    }
    
    private String getParserURL()
    {
        return "http://127.0.0.1:" + port + "/parsers/" + parserId;
    }
    
    protected File getDirectory(String path)
    {
        File file = FileUtils.toFile(getClass().getClassLoader().getResource(path));

        Preconditions.checkArgument(file.isDirectory(), "the path does not point to a directory");

        return file;
    }
    
    protected boolean isInProgress()
    {
        return get(getParserURL()).jsonPath().<Boolean>get("inProgress");
    }

    protected boolean isLocked()
    {
        return get(getParserURL()).jsonPath().<Boolean>get("locked");
    }

    protected void assertRecordCount(String tableName, long count) throws SQLException
    {
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(format("SELECT COUNT(*) FROM %s", tableName));
        resultSet.next();
        assertThat(resultSet.getLong(1), is(count));

        statement.close();
    }
}
