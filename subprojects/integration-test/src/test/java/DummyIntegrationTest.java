import static org.junit.Assert.*;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;

import org.junit.Test;

import com.trifork.stamdata.client.security.TwoWaySslSecurityHandler;

public class DummyIntegrationTest {
	@Test
	public void dummy() throws Exception {
		Set<String> texts = new HashSet<String>();
		
		new DummyTwoWaySslSecurityHandler();
		URLConnection connection = new URL("https://localhost:8444/lookup/person/0708610089").openConnection();
		XMLInputFactory readerFactory = XMLInputFactory.newInstance();
		XMLEventReader reader = readerFactory.createXMLEventReader(connection.getInputStream(), "UTF-8");
		try {
			while (reader.hasNext()) {
				XMLEvent event = reader.nextEvent();
				if (event.isCharacters()) {
					texts.add(event.asCharacters().getData());
				}
			}
		} finally {
			reader.close();
		}
		
		assertTrue("No CPR number in output: " + texts, texts.contains("0708610089"));
	}


	static class DummyTwoWaySslSecurityHandler extends TwoWaySslSecurityHandler {
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
