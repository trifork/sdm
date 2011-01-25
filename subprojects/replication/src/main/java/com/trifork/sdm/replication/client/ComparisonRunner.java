package com.trifork.sdm.replication.client;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.*;
import com.trifork.sdm.replication.db.*;
import com.trifork.sdm.replication.db.JdbcConnectionFactory.DB;
import com.trifork.sdm.replication.settings.Host;
import com.trifork.sdm.replication.settings.Port;
import com.trifork.stamdata.*;


public class ComparisonRunner {

	private static Logger logger = LoggerFactory.getLogger(ComparisonRunner.class);

	private static final String KEY_STORE_PASSWORD = "Test1234";
	private static final String KEY_STORE_PATH = "/SealKeystore.jks";

	private static final String TEST_SYSTEM_NAME = "SOSITEST";
	private static final String TEST_CVR = "30808460";

	private static final long ONE_HOUR = 1000 * 60 * 60;


	public static void main(String[] args) throws Exception {

		logger.info("Comparison Program Started.");

		while (true) {

			try {
				doComparison();
			}
			catch (Throwable t) {
				logger.debug("Unexpected exception durring comparison.", t);
			}

			Thread.sleep(ONE_HOUR);
		}
	}


	private static void doComparison() throws Exception {

		Injector injector = Guice.createInjector(new DatabaseModule());

		String host = injector.getInstance(Key.get(String.class, Host.class));
		int port = injector.getInstance(Key.get(int.class, Port.class));

		// Setup the connections.

		URL stsURL = new URL(STSConnection.TEST_STS_URL);
		File keystoreFile = new File(ComparisonRunner.class.getResource(KEY_STORE_PATH).getPath());
		STSConnection stsConnection = new STSConnection(stsURL, TEST_SYSTEM_NAME, TEST_CVR, keystoreFile, KEY_STORE_PASSWORD);

		URL gatewayURL = new URL("http", host, port, "/gateway");
		GatewayConnection gatewayConnection = new GatewayConnection(gatewayURL);

		// Create the client.

		ReplicationClient client = new ReplicationClient(stsConnection, gatewayConnection);

		// Create a connection to the database.

		JdbcConnectionFactory factory = injector.getInstance(JdbcConnectionFactory.class);
		Connection connection = factory.create(DB.SdmDuplicateDB);

		// Create a persister to help store all data.

		JdbcXMLRecordPersister persister = injector.getInstance(JdbcXMLRecordPersister.class);

		// Create a comparison helper that does the actual comparison test.

		DatabaseComparer comparer = injector.getInstance(DatabaseComparer.class);

		// Loop through each resource and ensure that the data is replicated
		// correctly.

		int version = 1;

		for (Class<? extends Record> resource : EntityHelper.getAllResources()) {

			String resourceName = NamingConvention.getResourceName(resource);

			// Get the latest historyId for this record from the database.

			String historyId = getLatestHistoryId(resource, connection);

			URL nextPageURL = client.fetchURL(resourceName, historyId, version);

			// Replicate as long as we get a new nextPageURL.

			while (nextPageURL != null) {

				InputStream inputStream = client.replicate(nextPageURL);

				nextPageURL = persister.persist(inputStream, resource);
			}

			// Compare the data in the original table and the replicated table.

			List<String> differences = comparer.compareResource(resource);

			// Print out the result and any differences.

			if (differences.isEmpty()) {

				logger.info(resourceName + " (OK)");
			}
			else {

				logger.error(resourceName + " (ERROR)");

				for (String difference : differences) {

					logger.error(difference);
				}
			}
		}

		connection.close();
	}


	private static String getLatestHistoryId(Class<?> entitySet, Connection connection) throws SQLException {

		String tableName = NamingConvention.getTableName(entitySet);

		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT MAX(HistoryId) FROM " + tableName);

		String latestHistoryId = null;

		if (resultSet.next()) {

			latestHistoryId = resultSet.getString(1);
		}

		statement.close();

		return latestHistoryId;
	}
}
