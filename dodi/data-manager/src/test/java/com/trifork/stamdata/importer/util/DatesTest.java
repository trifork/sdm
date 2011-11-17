/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.util;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.trifork.stamdata.importer.util.Dates;

public class DatesTest
{
	@Test
	public void testFormattingToFileNameDateformat() throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		assertEquals("2009-08-21T21-45-40", Dates.toFilenameDatetime(cal.getTime()));
	}

	@Test
	public void testToMysqlFormat() throws Exception
	{
		Calendar cal = Calendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		assertEquals("2009-08-21 21:45:40", Dates.toSqlDate(cal.getTime()));
	}

	@Test
	public void testGetCalendarFromMysqlDate() throws Exception
	{
		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2009, Calendar.AUGUST, 21, 21, 45, 40);
		java.sql.Date date = new java.sql.Date(cal.getTimeInMillis());
		assertEquals(cal.getTime().getTime(), Dates.toCalendar(date).getTime());
	}
}
