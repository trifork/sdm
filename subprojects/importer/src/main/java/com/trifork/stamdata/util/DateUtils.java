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

package com.trifork.stamdata.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
	public static final Calendar FUTURE = toCalendar(2999,12,31);
	public static final Calendar PAST = toCalendar(999, 12, 31);
	private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

	public static final DateFormat yyyyMMddHHmm = new SimpleDateFormat("yyyyMMddHHmm");
	public static final DateFormat yyyy_MM_dd = new SimpleDateFormat("yyyy-MM-dd");


	/**
	 * @param long1 representing a date sing the format: yyyyMMdd.
	 * @return a String representing the ISO 8601 date without time zone.
	 */
	public static String toISO8601date(Long long1) {
		if (long1 == null || long1 == 0)
			return null;
		SimpleDateFormat informat = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			return "" + outformat.format(informat.parse("" + long1));
		} catch (ParseException e) {
			logger.error("Error converting date to iso 8601 date format. Returning unformated string: '" + long1 + "'");
			return "" + long1;
		}

	}

	public static String toFilenameDatetime(Calendar cal) {
		SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd'T'HH-mm-ss");
		return outformat.format(cal.getTime());
	}

	/**
	 * @param year
	 * @param month (1-12)
	 * @param date (1-31)
	 */
	public static Calendar toCalendar(int year, int month, int date) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, date);
		return cal;
	}
	/**
	 * @param year
	 * @param month (1-12)
	 * @param date (1-31)
	 */

	public static Calendar toCalendar(int year, int month, int date, int hours, int minutes, int secs) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(year, month-1, date, hours, minutes, secs);
		return cal;
	}

	public static Calendar toCalendar(java.sql.Date date) {

		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal;
	}

	public static Calendar toCalendar(java.util.Date date) {
		if (date == null) return null;
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.setTimeInMillis(date.getTime());
		return cal;
	}

	public static String toMySQLdate(Calendar date) {
		if (date == null){
			logger.warn("Cannot convert null to mysqldate");
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date.getTime());
	}

}
