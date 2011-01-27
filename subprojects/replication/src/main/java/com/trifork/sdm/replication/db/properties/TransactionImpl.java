package com.trifork.sdm.replication.db.properties;

import java.lang.annotation.Annotation;

class TransactionImpl implements Transaction
{
	private final Database database;


	public TransactionImpl(Database database)
	{
		this.database = database;
	}


	@Override
	public Class<? extends Annotation> annotationType()
	{
		return Transaction.class;
	}


	@Override
	public Database value()
	{
		return database;
	}


	@Override
	public int hashCode()
	{
		// This is specified in java.lang.Annotation.
		return (127 * "value".hashCode()) ^ database.hashCode();
	}


	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Transaction))
		{
			return false;
		}

		Transaction other = (Transaction) o;
		return database.equals(other.value());
	}


	@Override
	public String toString()
	{
		return "@" + Transaction.class.getName() + "(database=" + database + ")";
	}
	

	private static final long serialVersionUID = 0;
}
