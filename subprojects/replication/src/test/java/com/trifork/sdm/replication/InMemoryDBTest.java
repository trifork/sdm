package com.trifork.sdm.replication;


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

/**
 * This class it a template I have made for when/if we want to
 * use an in-memory db (H2 in this case).
 * 
 * This would require:
 * - That we can access the SQL scripts from the build environment at runtime.
 * - That we get some nice fixtures (test data) loaded into the yaml files.
 */
public class InMemoryDBTest
{
	private static String file = "test.yml";

	private static IDataSet dataSet;
	@SuppressWarnings("unused")
	private static Injector injector;

	private static Server server;


	@BeforeClass
	public static void init() throws Exception
	{
		ClassLoader classLoader = InMemoryDBTest.class.getClassLoader();

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
	}


	@AfterClass
	public static void destroy()
	{
		server.stop();
	}


	@Before
	public void setUp() throws Exception
	{
		// Prepare the database.

		IDatabaseConnection connection = getConnection();
		CLEAN_INSERT.execute(connection, dataSet);

		// Get the unit under test.

		// ...
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
