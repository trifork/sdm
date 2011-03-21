package dk.trifork.sdm.config;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;


public class Configuration {

	private static Logger logger = Logger.getLogger(Configuration.class);
	private static Configuration defaultInstance = new Configuration();

	private Properties properties;

	public Configuration() {

		try {
			InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties");

			properties = new Properties();
			properties.load(input);

			for (String propertyKey : properties.stringPropertyNames()) {
				logger.info("Property '" + propertyKey + "' = " + ((propertyKey.indexOf("pwd") >= 0) ? "****" : getProperty(propertyKey)));
			}
		}
		catch (Exception e) {
			logger.error("Error loading config.properties not found.");
		}
	}

	public Configuration(InputStream file) throws IOException {

		properties = new Properties();
		properties.load(file);
	}

	public String getNotNullProperty(String key) {

		String value = properties.getProperty(key);
		if (value == null) {
			throw new RuntimeException("no value found for property key: " + key);
		}
		return value;
	}

	public int getIntProperty(String key) {

		return Integer.parseInt(getNotNullProperty(key));
	}

	public Date getDateProperty(String key) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return sdf.parse(getNotNullProperty(key));
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getString(String key) {

		String s = defaultInstance.getProperty(key);
		return s;
	}

	public static Integer getInt(String key) {

		return Integer.parseInt(defaultInstance.getProperty(key));
	}

	private String getProperty(String key) {

		return properties.getProperty(key);
	}

	public static void setDefaultInstance(Configuration conf) {

		// Only for unit tests
		defaultInstance = conf;
	}
}
