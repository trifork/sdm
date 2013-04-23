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
package dk.nsi.stamdata.replication.tools;

import com.google.common.io.Files;
import com.google.inject.Inject;
import dk.nsi.stamdata.replication.TestTableCreator;
import dk.nsi.stamdata.replication.webservice.GuiceTestRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import static junit.framework.Assert.assertTrue;

@RunWith(GuiceTestRunner.class)
public class SchemaGeneratorTest
{
    @Inject
    TestTableCreator testTableCreator;

    String jdbcUrl;

    @Before
    public void setUp() throws Exception
    {
        Properties properties = loadProperties();
        jdbcUrl = properties.getProperty("db.connection.jdbcURL");
        String username = properties.getProperty("db.connection.username");
        String password = properties.getProperty("db.connection.password");
        if (username != null) {
            jdbcUrl = jdbcUrl + "?user="+username+"&password=";
            if (password != null) {
                jdbcUrl = jdbcUrl + password;
            }
        }
    }

    @Test
    @Ignore
    public void testThatWeCanGenerateTestSchema() throws Exception
    {
        long id = testTableCreator.createAndWhiteListForTestView();
        //
        File tempDir = Files.createTempDir();
        String args[] = new String[2];
        args[0] = jdbcUrl;
        args[1] = tempDir.getAbsolutePath();
        DynamicSchemaGenerator.main(args);

        // Make sure we got a schema for our test table
        boolean found = false;
        File[] files = tempDir.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().contains("testreg")) {
                String[] list = file.list();
                if ("testtype_v1.xsd".equals(list[0])) {
                    found = true;
                }
            }
        }
        assertTrue(found);
        FileUtils.deleteDirectory(tempDir);
        //
        testTableCreator.removeView(id);
    }

    private static Properties loadProperties() throws Exception {
        Properties properties = new Properties();
        ClassLoader loader = SchemaGeneratorTest.class.getClassLoader();
        URL url = loader.getResource("test-config.properties");
        assert url != null;
        properties.load(url.openStream());
        return properties;
    }
}
