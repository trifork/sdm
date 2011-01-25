package com.trifork.sdm.replication.gateway;


import static org.dbunit.operation.DatabaseOperation.*;

import java.io.File;
import java.io.InputStream;
import java.sql.*;

import org.apache.commons.io.FileUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.yaml.YamlDataSet;
import org.h2.tools.Server;
import org.junit.*;

import com.google.inject.*;
import com.trifork.sdm.replication.ProductionModule;
import com.trifork.sdm.replication.gateway.RequestProcessor;
import com.trifork.sdm.replication.settings.SOAP;


public class GatewayServletTest
{
	private static String file = "test.yml";

	private static IDataSet dataSet;
	private static Injector injector;

	private RequestProcessor processor;
	private static Server server;


	@BeforeClass
	public static void init() throws Exception
	{
		/*
		ClassLoader classLoader = GatewayServletTest.class.getClassLoader();

		// Load the test data.

		InputStream inputStream = classLoader.getResourceAsStream(file);
		dataSet = new YamlDataSet(inputStream);
		inputStream.close();

		// Populate the dependency graph.

		Module testModule = new ProductionModule();
		injector = Guice.createInjector(testModule);

		// Prepare the database.

		server = Server.createWebServer("-web", "-webPort", "11111");
		server.run();

		Connection connection = getJdbcConnection();

		File schemaFile = FileUtils.toFile(classLoader.getResource("schema.sql"));
		String schemaSQL = FileUtils.readFileToString(schemaFile);

		Statement statement = connection.createStatement();
		statement.execute(schemaSQL);

		statement.close();
		connection.close();
		*/
	}


	@AfterClass
	public static void destroy()
	{
		//server.stop();
	}


	@Before
	public void setUp() throws Exception
	{
		/*
		// Prepare the database.

		IDatabaseConnection connection = getConnection();
		CLEAN_INSERT.execute(connection, dataSet);

		// Get the unit under test.

		processor = injector.getInstance(Key.get(RequestProcessor.class, SOAP.class));
		*/
	}


	@Test
	public void should_require_resource_param() throws Exception
	{

	}


	protected static IDatabaseConnection getConnection() throws Exception
	{
		return new DatabaseConnection(getJdbcConnection());
	}


	protected static Connection getJdbcConnection() throws Exception
	{
		// Comparability with MySQL SQL.
		String properties = ";MODE=MySQL";

		// Don't drop the database when no connections.
		properties += ";DB_CLOSE_DELAY=-1";

		// Use the database as in-memory.
		String uri = "jdbc:h2:mem:test" + properties;

		Class.forName("org.h2.Driver");
		Connection connection = DriverManager.getConnection(uri);

		return connection;
	}
}
