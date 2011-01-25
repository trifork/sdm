package com.trifork.sdm.importer.util;


import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.trifork.stamdata.DateUtils;


public class DateUtilsTest
{
	@Test
	public void testFormatting() throws Exception
	{
		assertEquals("1976-11-10", DateUtils.toISO8601date(19761110l));
	}


	@Test
	public void testNull() throws Exception
	{
		assertEquals(null, DateUtils.toISO8601date(0l));
	}


	@Test
	public void testError() throws Exception
	{
		assertEquals("1", DateUtils.toISO8601date(1l));
	}


	@Test
	public void testFormattingToFileNameDateformat() throws Exception
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		assertEquals("2009-08-21T21-45-40", DateUtils.toFilenameDatetime(cal));
	}
}
