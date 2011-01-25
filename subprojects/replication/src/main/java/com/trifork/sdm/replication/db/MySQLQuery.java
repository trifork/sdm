package com.trifork.sdm.replication.db;


import java.sql.*;
import java.util.*;
import java.util.Date;

import javax.inject.Inject;

import com.google.inject.assistedinject.Assisted;
import com.trifork.sdm.replication.db.JdbcConnectionFactory.DB;
import com.trifork.sdm.replication.replication.RecordExtractor;
import com.trifork.stamdata.NamingConvention;
import com.trifork.stamdata.Record;


public class MySQLQuery implements Query
{
	private final long pid;
	private final Date date;
	private final Class<? extends Record> entity;
	private final JdbcConnectionFactory connectionFactory;
	private final int pageSize;


	@Inject
	MySQLQuery(@Assisted Class<? extends Record> entity, @Assisted long pid, @Assisted Date since, @Assisted int pageSize, JdbcConnectionFactory connectionFactory)
	{
		this.pid = pid;
		this.date = since;
		this.entity = entity;
		this.pageSize = pageSize;
		this.connectionFactory = connectionFactory;
	}


	public long getPID()
	{
		return pid;
	}


	public Date getDate()
	{

		return date;
	}


	@Override
	public Iterator<Map<String, Object>> iterator()
	{
		Connection connection = connectionFactory.create(DB.SdmDB);

		PreparedStatement stm;
		final RecordExtractor extractor = new RecordExtractor(entity);

		String tableName = NamingConvention.getTableName(entity);

		try
		{
			// TODO: Clean this up.

			StringBuilder sql = new StringBuilder();
			sql.append(String.format("SELECT * FROM %s ", tableName));
			sql.append(String.format("WHERE (%sPID > ? AND ModifiedDate = ?) OR (ModifiedDate > ?) ", tableName));
			sql.append(String.format("ORDER BY %sPID, ModifiedDate, CreatedDate ", tableName));
			sql.append(String.format("LIMIT %d", pageSize));

			stm = connection.prepareStatement(sql.toString());

			stm.setLong(1, pid);
			stm.setTimestamp(2, new Timestamp(date.getTime()));
			stm.setTimestamp(3, new Timestamp(date.getTime()));

			System.out.println(stm.toString());

			final ResultSet resultSet = stm.executeQuery();

			return new Iterator<Map<String, Object>>()
			{
				private final ResultSet results = resultSet;


				@Override
				public boolean hasNext()
				{
					try
					{
						return results.next();
					}
					catch (SQLException e)
					{
						throw new RuntimeException(e);
					}
				}


				@Override
				public Map<String, Object> next()
				{
					try
					{
						return extractor.extract(results);
					}
					catch (Exception e)
					{
						throw new RuntimeException(e);
					}
				}


				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
}
