package com.trifork.stamdata.importer.persistence;


import static java.lang.String.format;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.jobs.FilePersistException;
import com.trifork.stamdata.persistence.Dataset;
import com.trifork.stamdata.util.EntityHelper;
import com.trifork.stamdata.util.NamingConvention;
import com.trifork.stamdata.util.Record;


public class MySQLTemporalTable<T extends Record> implements RecordStorage<T>
{

	private final Logger logger = LoggerFactory.getLogger(getClass());


	public static class RecordVersion
	{

		public Object id;
		public Date validFrom;
	}


	private static long tgetOutputFieldName = 0;
	private static long tIdOfCurrentRowEquals = 0;

	public static final String APP_ID = "SDM 3.0";

	public ResultSet currentRS; // TODO: Make a getter?

	private int insertedRecords, updatedRecords, deletedRecords = 0;

	private List<String> notUpdatedColumns;
	private List<Method> outputMethods;
	private PreparedStatement selectByIdStmt;

	private Method idMethod;
	private String tablename;
	private Class<T> type;

	private PreparedStatement deleteStmt;
	private PreparedStatement insertAndUpdateRecordStmt;
	private PreparedStatement insertRecordStmt;
	private PreparedStatement updateRecordStmt;
	private PreparedStatement updateValidFromStmt;
	private PreparedStatement updateValidToStmt;

	protected Connection connection;


	protected MySQLTemporalTable(Connection connection, Class<T> type, String tableName) throws FilePersistException
	{

		this.tablename = tableName;
		this.type = type;
		this.connection = connection;

		this.idMethod = EntityHelper.getIdMethod(type);

		try
		{
			outputMethods = NamingConvention.getColumns(type);
			notUpdatedColumns = locateNotUpdatedColumns();
			insertRecordStmt = prepareInsertStatement();
			insertAndUpdateRecordStmt = prepareInsertAndUpdateStatement();
			updateRecordStmt = prepareUpdateStatement();
			selectByIdStmt = prepareSelectByIdStatement();
			updateValidToStmt = prepareUpdateValidtoStatement();
			updateValidFromStmt = prepareUpdateValidFromStatement();
			deleteStmt = prepareDeleteStatement();
		}
		catch (SQLException e)
		{
			throw new FilePersistException("An error occured while preparing statements for table '" + tableName
					+ "'. ", e);
		}
	}


	public MySQLTemporalTable(Connection con, Class<T> clazz) throws FilePersistException
	{

		this(con, clazz, Dataset.getEntityTypeDisplayName(clazz));
	}


	private boolean fieldEqualsCurrentRow(Method method, Record sde) throws Exception
	{

		long t0 = System.nanoTime();

		String columnName = NamingConvention.getColumnName(method);

		tgetOutputFieldName += System.nanoTime() - t0;

		Object o = method.invoke(sde);

		if (o instanceof String)
		{
			String value = currentRS.getString(columnName);
			// Null strings and empty strings are the same
			if (value == null && ((String) o).trim().isEmpty()) return true;
			if (!o.equals(value)) return false;
		}
		else if (o instanceof Integer)
		{
			Integer value = currentRS.getInt(columnName);
			if (!o.equals(value)) return false;

		}
		else if (o instanceof Long)
		{
			Long value = currentRS.getLong(columnName);
			if (!o.equals(value)) return false;

		}
		else if (o instanceof Double)
		{
			Double value = currentRS.getDouble(columnName);
			if (!o.equals(value)) return false;

		}
		else if (o instanceof Boolean)
		{
			Boolean value = (currentRS.getInt(columnName) == 0 ? Boolean.FALSE : Boolean.TRUE);
			if (!o.equals(value)) return false;

		}
		else if (o instanceof Date)
		{
			Timestamp ts = currentRS.getTimestamp(columnName);
			if (ts == null) return false;
			long millis = ts.getTime();
			if (millis != ((Date) o).getTime()) return false;

		}
		else if (o == null)
		{
			Object value = currentRS.getObject(columnName);
			if (value != null) return false;

		}
		else
		{
			String message = "method " + Dataset.getEntityTypeDisplayName(type) + "." + method.getName()
					+ " has unsupported returntype: " + o + ". DB mapping unknown";
			logger.error(message);
			throw new FilePersistException(message);
		}

		return true;
	}


	private PreparedStatement prepareInsertAndUpdateStatement() throws SQLException
	{

		// TODO: Use String Builder.

		String sql = "insert into " + tablename + " ("
				+ "CreatedBy, ModifiedBy, ModifiedDate, CreatedDate, ValidFrom, ValidTo";
		for (Method method : outputMethods)
		{
			sql += ", ";
			String name = NamingConvention.getColumnName(method);
			sql += name;
		}
		for (String notUpdateName : notUpdatedColumns)
		{
			sql += ", " + notUpdateName;
		}
		sql += ") values (";
		sql += "'" + APP_ID + "',"; // createdby
		sql += "'" + APP_ID + "',"; // modifiedby
		sql += "?,"; // modifieddate
		sql += "?,"; // createddate
		sql += "?,"; // validfrom
		sql += "?"; // validto

		for (int i = 0; i < outputMethods.size(); i++)
		{
			sql += ",?";
		}

		for (int i = 0; i < notUpdatedColumns.size(); i++)
		{
			sql += ",?";
		}

		sql += ")";

		// logger.debug("Preparing insert statement: " + sql);
		return connection.prepareStatement(sql);
	}


	@SuppressWarnings("unused")
	private PreparedStatement prepareInsertStatement() throws SQLException
	{

		String sql = "insert into " + tablename + " ("
				+ "CreatedBy, ModifiedBy, ModifiedDate, CreatedDate, ValidFrom, ValidTo";
		for (Method method : outputMethods)
		{
			sql += ", ";
			String name = NamingConvention.getColumnName(method);
			sql += name;
		}
		sql += ") values (";
		sql += "'" + APP_ID + "',"; // createdby
		sql += "'" + APP_ID + "',"; // modifiedby
		sql += "?,"; // modifieddate
		sql += "?,"; // createddate
		sql += "?,"; // validfrom
		sql += "?"; // validto

		for (Method method : outputMethods)
		{
			sql += ",?";
		}

		sql += ")";

		// logger.debug("Preparing insert statement: " + sql);

		return connection.prepareStatement(sql);
	}


	private PreparedStatement prepareSelectByIdStatement() throws FilePersistException
	{

		PreparedStatement pstmt;
		String pstmtString = null;

		try
		{
			pstmtString = "SELECT * FROM " + tablename + " WHERE " + Dataset.getIdOutputName(type)
					+ " = ? AND NOT (ValidTo < ? or ValidFrom > ?) ORDER BY ValidTo";

			// select where ids match and validity intervals overlap
			// logger.debug("Preparing select by id statement: " + pstmtString);

			pstmt = connection.prepareStatement(pstmtString);
		}
		catch (Exception e)
		{
			logger.error("An error occured while preparing the SelectById statement for type: "
					+ Dataset.getEntityTypeDisplayName(type));

			throw new FilePersistException("An error occured while preparing the SelectById statement for table: "
					+ tablename + ". sql: " + pstmtString, e);
		}

		return pstmt;
	}


	private PreparedStatement prepareUpdateStatement() throws SQLException
	{

		// TODO: Use string builder.

		String sql = "UPDATE " + tablename + " SET ModifiedBy = '" + APP_ID
				+ "', ModifiedDate = ?, ValidFrom = ?, ValidTo = ?";
		for (Method method : outputMethods)
		{
			sql += ", " + NamingConvention.getColumnName(method) + " = ?";
		}

		sql += " where " + Dataset.getIdOutputName(type) + " = ? and ValidFrom = ? and ValidTo = ?";
		// logger.debug("Preparing update statement: " + sql);
		return connection.prepareStatement(sql);
	}


	private PreparedStatement prepareUpdateValidFromStatement() throws FilePersistException
	{

		// TODO: Use string builder.

		String pstmtString = "update " + tablename + " set ValidFrom = ?, " + "ModifiedDate = ?, ModifiedBy='" + APP_ID
				+ "' where " + Dataset.getIdOutputName(type) + " = ? and ValidFrom = ?";

		// logger.debug("Preparing update statement: " + pstmtString);
		PreparedStatement pstmt;

		try
		{
			pstmt = connection.prepareStatement(pstmtString);
		}
		catch (SQLException e)
		{
			throw new FilePersistException("An error occured while preparing the Update ValidTo statement.", e);
		}

		return pstmt;
	}


	private PreparedStatement prepareUpdateValidtoStatement() throws FilePersistException
	{

		String pstmtString = "update " + tablename + " set ValidTo = ?, " + "ModifiedDate = ?, ModifiedBy='" + APP_ID
				+ "' where " + Dataset.getIdOutputName(type) + " = ? and ValidFrom = ?";

		PreparedStatement pstmt;

		try
		{
			pstmt = connection.prepareStatement(pstmtString);
		}
		catch (SQLException sqle)
		{
			throw new FilePersistException("An error occured while preparing the Update ValidTo statement.", sqle);
		}

		return pstmt;
	}


	private boolean setObjectOnPreparedStatement(PreparedStatement pstmt, int idx, Object o) throws SQLException
	{

		if (o instanceof String)
		{
			pstmt.setString(idx++, (String) o);
		}
		else if (o instanceof Integer)
		{
			pstmt.setInt(idx++, (Integer) o);
		}
		else if (o instanceof Long)
		{
			pstmt.setLong(idx++, (Long) o);
		}
		else if (o instanceof Double)
		{
			pstmt.setDouble(idx++, (Double) o);
		}
		else if (o instanceof Boolean)
		{
			int b = (Boolean) o ? 1 : 0;
			pstmt.setInt(idx++, b);
		}
		else if (o instanceof Date)
		{
			pstmt.setTimestamp(idx++, new Timestamp(((Date) o).getTime()));
		}
		else if (o == null)
		{
			pstmt.setNull(idx++, java.sql.Types.NULL);
		}
		else
		{
			return false;
		}

		return true;
	}


	public int applyParamsToInsertAndUpdateStatement(PreparedStatement pstmt, Record sde, Date transactionTime, Date createdTime)
	{

		int idx = applyParamsToInsertStatement(pstmt, sde, transactionTime, createdTime);

		try
		{
			currentRS.last();
		}
		catch (SQLException e)
		{
			throw new RuntimeException("An error occured during application of parameters to a prepared statement. "
					+ "The database contained no records for entity id [" + sde.getKey() + "]");
		}
		for (String notUpdateName : notUpdatedColumns)
		{
			try
			{
				Object o = currentRS.getObject(notUpdateName);
				if (!setObjectOnPreparedStatement(pstmt, idx++, o))
				{
					logger.warn("Column " + notUpdateName + " has unsupported returntype: " + o
							+ ". DB mapping unknown");
					throw new RuntimeException(
							"An error occured during application of parameters to a prepared statement. Entity type: ["
									+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
									+ "]. There was an error setting value for record: [" + notUpdateName
									+ "]. The type is not supported");
				}
			}
			catch (SQLException sqle)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type:["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not set field for record name: [" + notUpdateName + "].", sqle);
			}
		}

		return idx;
	}


	public int applyParamsToInsertStatement(PreparedStatement pstmt, Record sde, Date transactionTime, Date createdTime)
	{

		int idx = 1;

		try
		{
			pstmt.setTimestamp(idx++, new Timestamp(transactionTime.getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(createdTime.getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(sde.getValidFrom().getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(sde.getValidTo().getTime()));
		}
		catch (SQLException sqle)
		{
			throw new RuntimeException("An error occured during application of parameters to a prepared statement. ",
					sqle);
		}

		for (Method method : outputMethods)
		{
			Object o;
			try
			{
				o = method.invoke(sde);
			}
			catch (InvocationTargetException ite)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type: ["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not invoke target method: [" + method.getName() + "]", ite);
			}
			catch (IllegalAccessException iae)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type: ["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not access method: [" + method.getName() + "]", iae);
			}
			try
			{
				if (!setObjectOnPreparedStatement(pstmt, idx++, o))
				{
					logger.warn("method " + Dataset.getEntityTypeDisplayName(sde.getClass()) + "." + method.getName()
							+ " has unsupported returntype: " + o + ". DB mapping unknown");
					throw new RuntimeException(
							"An error occured during application of parameters to a prepared statement. Entity type: ["
									+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
									+ "]. There was an error setting value for method: [" + method.getName()
									+ "]. The type is not supported");
				}
			}
			catch (SQLException sqle)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type:["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not set field for method name: [" + method.getName() + "].", sqle);
			}
		}
		return idx;
	}


	public void applyParamsToUpdateStatement(PreparedStatement pstmt, Record sde, Date transactionTime, Date createdTime, Date existingValidFrom, Date existingValidTo)
	{

		int idx = 1;

		try
		{
			pstmt.setTimestamp(idx++, new Timestamp(transactionTime.getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(sde.getValidFrom().getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(sde.getValidTo().getTime()));
		}
		catch (SQLException sqle)
		{
			throw new RuntimeException("An error occured during application of parameters to a prepared statement. ",
					sqle);
		}

		for (Method method : outputMethods)
		{
			Object o;
			try
			{
				o = method.invoke(sde);
			}
			catch (InvocationTargetException ite)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type: ["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not invoke target method: [" + method.getName() + "]", ite);
			}
			catch (IllegalAccessException iae)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type: ["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not access method: [" + method.getName() + "]", iae);
			}
			try
			{
				if (!setObjectOnPreparedStatement(pstmt, idx++, o))
				{
					logger.warn("method " + Dataset.getEntityTypeDisplayName(sde.getClass()) + "." + method.getName()
							+ " has unsupported returntype: " + o + ". DB mapping unknown");
					throw new RuntimeException(
							"An error occured during application of parameters to a prepared statement. Entity type: ["
									+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
									+ "]. There was an error setting value for method: [" + method.getName()
									+ "]. The type is not supported");
				}
			}
			catch (SQLException sqle)
			{
				throw new RuntimeException(
						"An error occured during application of parameters to a prepared statement. Entity type:["
								+ sde.getClass() + "]. The entity id was: [" + sde.getKey()
								+ "]. Could not set field for method name: [" + method.getName() + "].", sqle);
			}
		}

		try
		{

			updateValidToStmt.setObject(3, currentRS.getObject(Dataset.getIdOutputName(type)));
			setObjectOnPreparedStatement(pstmt, idx++, sde.getKey());

			pstmt.setTimestamp(idx++, new Timestamp(existingValidFrom.getTime()));
			pstmt.setTimestamp(idx++, new Timestamp(existingValidTo.getTime()));
		}
		catch (SQLException sqle)
		{

			throw new RuntimeException("An error occured during application of parameters to a prepared statement. ",
					sqle);
		}

	}


	public void copyCurrentRowButWithChangedValidFrom(Date validFrom, Date transactionTime)
	{

		try
		{
			StringBuilder columns = new StringBuilder(
					"CreatedBy, ModifiedBy, ModifiedDate, CreatedDate, ValidFrom, ValidTo");
			StringBuilder values = new StringBuilder("?, ?, ?, ?, ?, ?");

			for (Method method : outputMethods)
			{
				columns.append(", ");
				columns.append(NamingConvention.getColumnName(method));
				values.append(", ?");
			}

			for (String notUpdateName : notUpdatedColumns)
			{
				columns.append(", ");
				columns.append(notUpdateName);
				values.append(", ?");
			}

			PreparedStatement stmt = connection.prepareStatement(String
					.format("INSERT INTO %s (%s) VALUES (%s)", tablename, columns, values));

			int i = 1;

			stmt.setString(i++, APP_ID);
			stmt.setString(i++, APP_ID);
			stmt.setTimestamp(i++, new Timestamp(transactionTime.getTime()));
			stmt.setTimestamp(i++, new Timestamp(transactionTime.getTime()));
			stmt.setTimestamp(i++, new Timestamp(validFrom.getTime()));
			stmt.setTimestamp(i++, currentRS.getTimestamp("ValidTo"));

			for (Method method : outputMethods)
			{
				Object value = currentRS.getObject(NamingConvention.getColumnName(method));
				stmt.setObject(i++, value);
			}

			for (String notUpdateName : notUpdatedColumns)
			{
				Object value = currentRS.getObject(notUpdateName);
				stmt.setObject(i++, value);
			}

			stmt.executeUpdate();

			insertedRecords++;
		}
		catch (SQLException e)
		{
			logger.error("copyCurrentRowButWithChangedValidFrom", e);
		}
	}


	public boolean dataInCurrentRowEquals(T sde)
	{

		for (Method method : outputMethods)
		{
			try
			{
				if (!fieldEqualsCurrentRow(method, sde)) return false;
			}
			catch (Exception e)
			{
				logger.error("dataInCurrentRowEquals failed on method " + method.getName(), e);
				return false;
			}
		}
		return true;

	}


	public void deleteCurrentRow()
	{

		try
		{
			deleteStmt.setObject(1, currentRS.getObject(Dataset.getIdOutputName(type)));
			deleteStmt.setObject(2, currentRS.getObject("ValidFrom"));

			int rowsAffected = deleteStmt.executeUpdate();
			if (rowsAffected != 1)
				logger.error("deleteCurrentRow - expected to delete 1 row. Deleted: " + rowsAffected);
			deletedRecords += rowsAffected;
		}
		catch (SQLException e)
		{
			logger.error("deleteCurrentRow", e);
		}

	}


	public void drop()
	{

		try
		{
			connection.createStatement().execute("drop table " + tablename + ";");
		}
		catch (Exception e)
		{
			logger.error("truncate");
		}
	}


	public boolean fetchEntityVersions(Date validFrom, Date validTo)
	{

		try
		{
			String sql = format("SELECT * FROM %s WHERE NOT (ValidTo < ? OR ValidFrom > ?)", tablename);

			PreparedStatement stm = connection.prepareStatement(sql);

			stm.setTimestamp(1, new Timestamp(validFrom.getTime()));
			stm.setTimestamp(2, new Timestamp(validTo.getTime()));

			currentRS = stm.executeQuery();

			return currentRS.next();
		}
		catch (SQLException e)
		{
			logger.error("fetchEntityVersions", e);
			return false;
		}

	}


	/**
	 * @param id
	 * @param validFrom
	 * @param validTo
	 * @return If at least one version of the entity was found with the
	 *         specified id in the specified validfrom-validto range
	 */
	public boolean fetchEntityVersions(Object id, Date validFrom, Date validTo)
	{

		try
		{
			this.selectByIdStmt.setObject(1, id);
			this.selectByIdStmt.setTimestamp(2, new Timestamp(validFrom.getTime()));
			this.selectByIdStmt.setTimestamp(3, new Timestamp(validTo.getTime()));
			currentRS = selectByIdStmt.executeQuery();
			return currentRS.next();
		}
		catch (SQLException e)
		{
			logger.error("", e);
		}
		return false;

	}


	public Date getCurrentRowValidFrom()
	{

		try
		{

			return new Date(currentRS.getTimestamp("ValidFrom").getTime());
		}
		catch (SQLException e)
		{

			logger.error("getCurrentRowValidFrom()", e);
		}

		return null;
	}


	public Date getCurrentRowValidTo()
	{

		try
		{
			return new Date(currentRS.getTimestamp("ValidTo").getTime());
		}
		catch (SQLException e)
		{
			logger.error("getCurrentRowValidTo()", e);
		}

		return null;
	}


	public int getDeletedRecords()
	{

		return deletedRecords;
	}


	public List<RecordVersion> getRecordVersions(Date validFrom, Date validTo)
	{

		List<RecordVersion> versionList;

		try
		{
			String idMethodName = NamingConvention.getColumnName(idMethod);

			String sql = format("SELECT %s, ValidFrom FROM %s WHERE NOT (ValidTo < ? OR ValidFrom > ?)", idMethodName, tablename);

			PreparedStatement stm = connection.prepareStatement(sql);

			stm.setTimestamp(1, new Timestamp(validFrom.getTime()));
			stm.setTimestamp(2, new Timestamp(validTo.getTime()));

			currentRS = stm.executeQuery();

			versionList = new ArrayList<RecordVersion>();

			while (currentRS.next())
			{

				RecordVersion version = new RecordVersion();
				version.id = currentRS.getObject(1);
				version.validFrom = currentRS.getTimestamp(2);
				versionList.add(version);
			}

			logger.debug(format("Returning %d entity versions", versionList.size()));
		}
		catch (SQLException e)
		{

			logger.error("fetchEntityVersions", e);
			versionList = null;
		}

		return versionList;
	}


	public boolean getIdOfCurrentRow(Record sde)
	{

		long t0 = System.nanoTime();

		try
		{
			return fieldEqualsCurrentRow(idMethod, sde);
		}
		catch (Exception e)
		{

			logger.error("IdOfCurrentRowEquals", e);
			return false;
		}
		finally
		{

			tIdOfCurrentRowEquals += System.nanoTime() - t0;
		}
	}


	public int getInsertedRows()
	{

		return insertedRecords;
	}


	public int getUpdatedRecords()
	{

		return updatedRecords;
	}


	public void insertAndUpdateRow(T sde, Date transactionTime)
	{

		applyParamsToInsertAndUpdateStatement(insertAndUpdateRecordStmt, sde, transactionTime, transactionTime);
		try
		{
			insertAndUpdateRecordStmt.execute();
			if (++insertedRecords % 1000 == 0)
			{
				logger.debug("Inserted " + tablename + ": " + insertedRecords);
			}
		}
		catch (SQLException sqle)
		{
			String message = "An error occured while inserting new entity of type: "
					+ Dataset.getEntityTypeDisplayName(type);
			try
			{
				message += "entityid: " + sde.getKey() + " SQLError: " + sqle.getMessage();
			}
			catch (Exception e)
			{
				message += " Error: " + e.getMessage();
			}
			throw new RuntimeException(message, sqle);
		}
	}


	public void insertRow(T sde, Date transactionTime)
	{

		applyParamsToInsertStatement(insertRecordStmt, sde, transactionTime, transactionTime);

		try
		{
			insertRecordStmt.execute();
			if (++insertedRecords % 1000 == 0)
			{
				logger.debug("Inserted " + tablename + ": " + insertedRecords);
			}
		}
		catch (SQLException sqle)
		{
			String message = "An error occured while inserting new entity of type: "
					+ Dataset.getEntityTypeDisplayName(type);
			try
			{
				message += "entityid: " + sde.getKey();
			}
			catch (Exception e)
			{
			}
			throw new RuntimeException(message, sqle);
		}
	}


	/*
	 * Get a list with all columns that will not be updated by the Entity
	 * Entities doesn't have to be complete. The can update only parts of a
	 * table and then the rest have to be copied as not changed.
	 */
	public List<String> locateNotUpdatedColumns()
	{

		ArrayList<String> res = new ArrayList<String>();
		Statement stm = null;

		try
		{
			stm = connection.createStatement();
			stm.execute("desc " + tablename);
			ResultSet rs = stm.getResultSet();

			while (rs.next())
			{
				String colName = rs.getString(1);

				// Ignore all system columns
				if (colName.toUpperCase().indexOf("PID") > 0) continue;
				if (colName.equalsIgnoreCase("CreatedBy")) continue;
				if (colName.equalsIgnoreCase("ModifiedBy")) continue;
				if (colName.equalsIgnoreCase("ModifiedDate")) continue;
				if (colName.equalsIgnoreCase("CreatedDate")) continue;
				if (colName.equalsIgnoreCase("ValidFrom")) continue;
				if (colName.equalsIgnoreCase("ValidTo")) continue;

				boolean found = false;
				for (Method method : outputMethods)
				{
					// Ignore the columns that are updated by the entity
					String name = NamingConvention.getColumnName(method);
					if (colName.equalsIgnoreCase(name))
					{
						found = true;
						continue;
					}
				}

				if (!found) res.add(colName);
			}
		}
		catch (SQLException e)
		{
			logger.error("Error locateNotUpdatedColumns. Error is: " + e.getMessage());
		}
		finally
		{
			try
			{
				if (stm != null) stm.close();
			}
			catch (SQLException e)
			{
				// TODO: Log
			}
		}

		return res;
	}


	public boolean hasMoreRows()
	{

		try
		{
			return currentRS.next();
		}
		catch (SQLException e)
		{
			logger.error("nextRow()", e);
		}

		return false;
	}


	public PreparedStatement prepareDeleteStatement()
	{

		try
		{
			String sql = "delete from " + tablename + " where " + Dataset.getIdOutputName(type) + " = "
					+ "? and ValidFrom = ?";
			// logger.debug("Preparing delete statemetn: " + sql);
			return connection.prepareStatement(sql);
		}
		catch (SQLException e)
		{
			logger.error("prepareDeleteStatement");
		}
		return null;
	}


	public void truncate()
	{

		try
		{
			connection.createStatement().execute("truncate " + tablename + ";");
		}
		catch (Exception e)
		{
			logger.error("truncate");
		}
	}


	public void updateRow(T sde, Date transactionTime, Date existingValidFrom, Date existingValidTo)
	{

		applyParamsToUpdateStatement(updateRecordStmt, sde, transactionTime, transactionTime, existingValidFrom, existingValidTo);
		try
		{
			updateRecordStmt.execute();

			if (++updatedRecords % 1000 == 0)
			{
				logger.debug("Updated " + tablename + ": " + updatedRecords);
			}
		}
		catch (SQLException e)
		{
			String message = "An error occured while inserting new entity of type: "
					+ Dataset.getEntityTypeDisplayName(type);
			try
			{
				message += "entityid: " + sde.getKey();
			}
			catch (Exception ex)
			{
				// TODO: Log?
			}

			throw new RuntimeException(message, e);
		}
	}


	public void updateValidFromOnCurrentRow(Date validFrom, Date transactionTime)
	{

		try
		{
			updateValidFromStmt.setTimestamp(1, new Timestamp(validFrom.getTime()));
			updateValidFromStmt.setTimestamp(2, new Timestamp(transactionTime.getTime()));
			updateValidFromStmt.setObject(3, currentRS.getObject(Dataset.getIdOutputName(type)));
			updateValidFromStmt.setTimestamp(4, currentRS.getTimestamp("ValidFrom"));

			int rowsAffected = updateValidFromStmt.executeUpdate();

			if (rowsAffected != 1)
				logger.error("updateValidFromStmt - expected to update 1 row. updated: " + rowsAffected);
			updatedRecords += rowsAffected;
		}
		catch (SQLException e)
		{
			logger.error("updateValidFromOnCurrentRow", e);
		}
	}


	public void updateValidToOnCurrentRow(Date validTo, Date transactionTime)
	{

		logger.debug("Updating validto on current record");
		try
		{
			updateValidToStmt.setTimestamp(1, new Timestamp(validTo.getTime()));
			updateValidToStmt.setTimestamp(2, new Timestamp(transactionTime.getTime()));
			updateValidToStmt.setObject(3, currentRS.getObject(Dataset.getIdOutputName(type)));
			updateValidToStmt.setTimestamp(4, currentRS.getTimestamp("ValidFrom"));
			int rowsAffected = updateValidToStmt.executeUpdate();
			if (rowsAffected != 1)
				logger.error("updateValidToStmt - expected to update 1 row. updated: " + rowsAffected);
			updatedRecords += rowsAffected;
		}
		catch (SQLException e)
		{
			logger.error("updateValidToOnCurrentRow", e);
		}

	}


	public void updateValidToOnEntityVersion(Date validTo, RecordVersion evs, Date transactionTime)
	{

		try
		{
			updateValidToStmt.setTimestamp(1, new Timestamp(validTo.getTime()));
			updateValidToStmt.setTimestamp(2, new Timestamp(transactionTime.getTime()));
			updateValidToStmt.setObject(3, evs.id);
			updateValidToStmt.setTimestamp(4, new Timestamp(evs.validFrom.getTime()));

			int rowsAffected = updateValidToStmt.executeUpdate();

			if (rowsAffected != 1)
			{
				logger.error("updateValidToStmt - expected to update 1 row. updated: " + rowsAffected);
			}

			updatedRecords += rowsAffected;
		}
		catch (SQLException e)
		{
			logger.error("updateValidToOnCurrentRow", e);
		}

	}

}
