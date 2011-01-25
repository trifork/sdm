package com.trifork.sdm.importer.persistence.mysql;


import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;

import org.junit.Before;
import org.junit.BeforeClass;

import com.trifork.stamdata.DateUtils;


public abstract class AbstractMySQLIntegationTest
{

	protected static Date t0 = new Date();
	protected static Date t1 = new Date();
	protected static Date t2 = new Date();
	protected static Date t3 = new Date();
	protected static Date t4 = new Date();
	protected static Date t1000 = new Date();


	@Before
	public void setup() throws Exception
	{
		/*
		Connection con = MySQLConnectionManager.getAutoCommitConnection();
		Statement stmt = con.createStatement();
		stmt.executeUpdate("truncate table TakstVersion");
		stmt.close();
		con.close();
		*/
	}


	@BeforeClass
	public static void initDates()
	{
		/*
		t0 = DateUtils.toDate(2000, 0, 1, 1, 2, 3);
		t1 = DateUtils.toDate(2001, 0, 1, 1, 2, 3);
		t2 = DateUtils.toDate(2002, 0, 1, 1, 2, 3);
		t3 = DateUtils.toDate(2003, 0, 1, 1, 2, 3);
		t4 = DateUtils.toDate(2003, 0, 1, 1, 2, 3);
		t1000 = DateUtils.toDate(3003, 0, 1, 1, 2, 3);
		*/
	}
}
