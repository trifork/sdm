package com.trifork.sdm.replication.client;


import static org.slf4j.LoggerFactory.*;

import org.slf4j.Logger;

import com.trifork.sdm.replication.db.TransactionManager;
import com.trifork.sdm.replication.db.TransactionManager.Transactional;


public class ComparisonRunner
{
	private static Logger LOG = getLogger(ComparisonRunner.class);

	private static final long ONE_HOUR = 1000 * 60 * 60;


	public static void main(String[] args) throws Exception
	{
		LOG.info("Comparison Program Started.");

		while (true)
		{
			try
			{
				doComparison();
			}
			catch (Throwable t)
			{
				LOG.debug("Unexpected exception durring comparison.", t);
			}

			Thread.sleep(ONE_HOUR);
		}
	}


	TransactionManager manager = new TransactionManager();


	@Transactional
	private static void doComparison() throws Exception
	{

	}
}
