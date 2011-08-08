// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.trifork.stamdata.Preconditions;


public class DateUtils
{
	public static final Date FUTURE = toDate(2999, 12, 31);
	public static final Date PAST = toDate(999, 12, 31);

	public static final DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
	public static final DateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * @param long1 representing a date sing the format: yyyyMMdd.
	 * @return a String representing the ISO 8601 date without time zone.
	 * @throws ParseException 
	 */
	public static Date toISO8601date(String dateString) throws ParseException
	{
		SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
		return informat.parse(dateString);
	}

	public static String toFilenameDatetime(Date date)
	{
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		return outformat.format(date);
	}

	/**
	 * @param year
	 * @param month (1-12)
	 * @param date (1-31)
	 */
	public static Date toDate(int year, int month, int date)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, date);
		return cal.getTime();
	}

	/**
	 * @param year
	 * @param month (1-12)
	 * @param date (1-31)
	 */
	public static Date toDate(int year, int month, int date, int hours, int minutes, int secs)
	{
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month - 1, date, hours, minutes, secs);
		return cal.getTime();
	}

	public static Date toCalendar(java.sql.Date date)
	{
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal.getTime();
	}

	public static String toMySQLdate(Date date)
	{
		Preconditions.checkNotNull(date);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
