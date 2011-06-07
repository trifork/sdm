import com.trifork.stamdata.client.security.TwoWaySslSecurityHandler;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class LookupIntegrationTest {
    @BeforeClass
    public static void beforeClass() {
        new IntegrationTestTwoWaySslSecurityHandler();
    }

    @Test
    public void worksForSimplePerson() throws Exception {
        Set<String> texts = readStringsForPerson("0708610089");

        assertTrue("No CPR number in output: " + texts, texts.contains("0708610089"));
    }

    @Test
    public void worksForUmyndiggoerelse() throws Exception {
        Set<String> texts = readStringsForPerson("0709614126");

        assertTrue("No CPR number in output: " + texts, texts.contains("URN:CPR:0904414131"));
    }

    private Set<String> readStringsForPerson(String cpr) throws Exception {
        URLConnection connection = new URL("https://localhost:8444/lookup/person/" + cpr).openConnection();
        XMLInputFactory readerFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = readerFactory.createXMLEventReader(connection.getInputStream(), "UTF-8");
        Set<String> result = new HashSet<String>();
        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters()) {
                    result.add(event.asCharacters().getData());
                }
            }
        } finally {
            reader.close();
        }
        return result;
    }


    private String convertStreamToString(InputStream is)
            throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }


    static class IntegrationTestTwoWaySslSecurityHandler extends TwoWaySslSecurityHandler {
        @Override
        protected String getTrustStorePassword() {
            return "Test1234";
        }

        @Override
        protected String getTrustStorePath() {
            return "classpath:/truststore.jks";
        }

        @Override
        protected String getKeyStorePassword() {
            return "Test1234";
        }

        @Override
        protected String getKeyStorePath() {
            return "classpath:/keystore.jks";
        }
    }
}
