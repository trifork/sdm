package dk.trifork.sdm.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;


public class DateUtilsTest {

	@Test
	public void testFormatting() throws Exception {

		assertEquals("1976-11-10", DateUtils.toISO8601date(19761110l));
	}

	@Test
	public void testNull() throws Exception {

		assertEquals(null, DateUtils.toISO8601date(0l));
	}

	@Test
	public void testError() throws Exception {

		assertEquals("1", DateUtils.toISO8601date(1l));
	}

	@Test
	public void testFormattingToFileNameDateformat() throws Exception {

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		assertEquals("2009-08-21T21-45-40", DateUtils.toFilenameDatetime(cal));
	}

	@Test
	public void testToMysqlFormat() throws Exception {

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		assertEquals("2009-08-21 21:45:40", DateUtils.toMySQLdate(cal));
	}

	@Test
	public void testGetCalendarFromMysqlDate() throws Exception {

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());
		assertEquals(cal.getTime().getTime(), DateUtils.toCalendar(date).getTime().getTime());
	}

}
