package com.trifork.sdm.replication.db.properties;

import java.lang.annotation.Annotation;


@SuppressWarnings("all")
class TransactionImpl implements Transactional
{
	private final Database database;


	public TransactionImpl(Database database)
	{
		this.database = database;
	}


	@Override
	public Class<? extends Annotation> annotationType()
	{
		return Transactional.class;
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
		return 127 * "value".hashCode() ^ database.hashCode();
	}


	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof Transactional))
		{
			return false;
		}

		Transactional other = (Transactional) o;
		return database.equals(other.value());
	}


	@Override
	public String toString()
	{
		return "@" + Transactional.class.getName() + "(database=" + database + ")";
	}

	private static final long serialVersionUID = 0;
}
