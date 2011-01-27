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
}
