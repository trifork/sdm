package com.trifork.sdm.replication.client;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

import javax.inject.Inject;

import com.trifork.sdm.replication.db.JdbcConnectionFactory;
import com.trifork.sdm.replication.db.JdbcConnectionFactory.DB;
import com.trifork.sdm.replication.settings.DuplicateDB;
import com.trifork.sdm.replication.settings.MainDB;
import com.trifork.stamdata.NamingConvention;
import com.trifork.stamdata.Record;


public class DatabaseComparer {

	private final JdbcConnectionFactory factory;
	private final String sourceSchema;
	private final String destinationSchema;


	@Inject
	DatabaseComparer(JdbcConnectionFactory factory, @MainDB String sourceSchema, @DuplicateDB String destinationSchema) {

		this.factory = factory;
		this.sourceSchema = sourceSchema;
		this.destinationSchema = destinationSchema;
	}


	public List<String> compareResource(Class<? extends Record> entitySet) throws SQLException {

		String tableName = NamingConvention.getTableName(entitySet);
		String pidColumn = NamingConvention.getPIDName(entitySet);

		// Compile a list of columns to compare.

		String columnList = "";
		String sourceColumnList = "";
		String destinationColumnList = "";

		for (Method method : NamingConvention.getColumns(entitySet, 1)) {

			String columnName = NamingConvention.getColumnName(method);

			columnList += columnList.isEmpty() ? columnName : String.format(", %s", columnName);
			sourceColumnList += sourceColumnList.isEmpty() ? "S." + columnName : String.format(", S.%s", columnName);
			destinationColumnList += destinationColumnList.isEmpty() ? "D." + columnName : String.format(", D.%s", columnName);
		}

		String comparisonSQL = String.format(

		"SELECT MIN(tbl_name) AS tbl_name, %1$s, %2$s " + 
		"FROM (" +
		"SELECT 'original' as tbl_name, S.%1$s, %3$s " + "	FROM %6$s.%5$s AS S" + "	UNION ALL" + "	SELECT 'replica' as tbl_name, D.%1$s, %4$s " + "	FROM %7$s.%5$s AS D " + ") AS alias_table " + "GROUP BY %1$s, %2$s  " + "HAVING COUNT(*) = 1 " + "ORDER BY %1$s",

		pidColumn, columnList, sourceColumnList, destinationColumnList, tableName, sourceSchema, destinationSchema);

		Connection connection = factory.create(DB.SdmDB);

		Statement statement = connection.createStatement();
		ResultSet differences = statement.executeQuery(comparisonSQL);

		List<String> results = new ArrayList<String>();

		while (differences.next()) {
			results.add(formatDifferences(differences, tableName));
		}

		statement.close();
		connection.close();

		return results;
	}


	private String formatDifferences(ResultSet resultSet, String tableName) throws SQLException {

		// TODO: This whole class is messy. And if rows are missing we get strange error messages.
		
		String message = String.format("\nReplication error in table '%s':\n", tableName);
		
		Map<String, String> source = new HashMap<String, String>();
		
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
			
			source.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
		}
		
		resultSet.next();
		
		Map<String, String> destination = new HashMap<String, String>();
		
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
			
			destination.put(resultSet.getMetaData().getColumnName(i), resultSet.getString(i));
		}
		
		for (Map.Entry<String, String> entry : source.entrySet()) {
		
			// Skip the table name.
			if (entry.getKey().equals("tbl_name")) continue;
			
			String pid = resultSet.getString(tableName + "PID");
			
			if (!destination.get(entry.getKey()).equals(entry.getValue())) {
				message += String.format("ID(%s) COLUMN(%s) EXPECTED(%s) WAS(%s).\n", pid, entry.getKey(), entry.getValue(), destination.get(entry.getKey()));
			}
		}
		
		return message;
	}
}
