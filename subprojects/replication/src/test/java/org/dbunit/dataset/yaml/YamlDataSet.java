package org.dbunit.dataset.yaml;

import java.io.*;
import java.util.*;

import org.dbunit.dataset.*;
import org.dbunit.dataset.datatype.DataType;
import org.yaml.snakeyaml.Yaml;


public class YamlDataSet implements IDataSet
{
	private final Map<String, MyTable> tables = new HashMap<String, MyTable>();


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public YamlDataSet(InputStream inputStream) throws FileNotFoundException
	{
		Map<String, List<Map>> data = (Map<String, List<Map>>) new Yaml().load(inputStream);
		for (Map.Entry<String, List<Map>> ent : data.entrySet())
		{
			String tableName = ent.getKey();
			List<Map> rows = ent.getValue();
			createTable(tableName, rows);
		}
	}

	@SuppressWarnings("rawtypes")
	public class MyTable implements ITable
	{
		String name;

		List<Map> data;
		ITableMetaData meta;


		MyTable(String name, List<String> columnNames)
		{
			this.name = name;
			this.data = new ArrayList<Map>();
			meta = createMeta(name, columnNames);
		}


		ITableMetaData createMeta(String name, List<String> columnNames)
		{
			Column[] columns = null;
			if (columnNames != null)
			{
				columns = new Column[columnNames.size()];
				for (int i = 0; i < columnNames.size(); i++)
				{
					columns[i] = new Column(columnNames.get(i), DataType.UNKNOWN);
				}
			}
			return new DefaultTableMetaData(name, columns);
		}


		@Override
		public int getRowCount()
		{
			return data.size();
		}


		@Override
		public ITableMetaData getTableMetaData()
		{
			return meta;
		}


		@Override
		public Object getValue(int row, String column) throws DataSetException
		{
			if (data.size() <= row)
			{
				throw new RowOutOfBoundsException("" + row);
			}
			return data.get(row).get(column.toUpperCase());
		}


		@SuppressWarnings("unchecked")
		void addRow(Map values)
		{
			data.add(convertMap(values));
		}


		@SuppressWarnings("unchecked")
		Map convertMap(Map<String, Object> values)
		{
			Map ret = new HashMap();
			for (Map.Entry<String, Object> ent : values.entrySet())
			{
				ret.put(ent.getKey().toUpperCase(), ent.getValue());
			}
			return ret;
		}

	}


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MyTable createTable(String name, List<Map> rows)
	{
		MyTable table = new MyTable(name, rows.size() > 0 ? new ArrayList(rows.get(0).keySet()) : null);
		for (Map values : rows)
		{
			table.addRow(values);
		}
		tables.put(name, table);
		return table;
	}


	@Override
	public ITable getTable(String tableName) throws DataSetException
	{
		return tables.get(tableName);
	}


	@Override
	public ITableMetaData getTableMetaData(String tableName) throws DataSetException
	{
		return tables.get(tableName).getTableMetaData();
	}


	@Override
	public String[] getTableNames() throws DataSetException
	{
		return tables.keySet().toArray(new String[tables.size()]);
	}


	@Override
	public ITable[] getTables() throws DataSetException
	{
		return tables.values().toArray(new ITable[tables.size()]);
	}


	@Override
	public ITableIterator iterator() throws DataSetException
	{
		return new DefaultTableIterator(getTables());
	}


	@Override
	public ITableIterator reverseIterator() throws DataSetException
	{
		return new DefaultTableIterator(getTables(), true);
	}


	@Override
	public boolean isCaseSensitiveTableNames()
	{
		return false;
	}
}
