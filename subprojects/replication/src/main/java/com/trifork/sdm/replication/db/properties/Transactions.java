package com.trifork.sdm.replication.db.properties;


public class Transactions
{
	public static Transactional transaction(Database database)
	{
		return new TransactionImpl(database);
	}
}
