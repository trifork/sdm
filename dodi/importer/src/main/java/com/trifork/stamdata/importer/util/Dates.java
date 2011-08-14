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

import java.sql.Timestamp;
import java.text.*;
import java.util.*;

import org.joda.time.*;
import org.joda.time.format.*;

import com.trifork.stamdata.Preconditions;


/* Helpers for handling dates and time.
 * 
 * These date formatters and converters should be used when managing data
 * in the registries.
 * 
 * Is is not safe to assume that all registries will use Danish time (CET),
 * though this is mostly the case.
 * 
 * WARNING! DO NOT SET THE DEFAULT TIME ZONE. It is not always clear what the
 * default time zone is set to and setting the TimeZone.setDefault() could
 * change the default for the entire JVM! (depending on JVM version)
 * Other applications might do the same, so always specify your timezone or
 * use these helpers.
 */
public class Dates
{
	public static final DateTimeZone DK_TIMEZONE = DateTimeZone.forID("Europe/Copenhagen");
	public static final Calendar DK_CALENDAR = Calendar.getInstance(DK_TIMEZONE.toTimeZone());

	public static final Date THE_END_OF_TIME = new DateTime(2999, 12, 31, 0, 0, 0, 0, DK_TIMEZONE).toDate();

	public static final DateTimeFormatter DK_yyyyMMdd = ISODateTimeFormat.basicDate().withZone(DK_TIMEZONE);
	public static final DateTimeFormatter DK_yyyyMMddHHmm = DateTimeFormat.forPattern("yyyyMMddHHmm").withZone(DK_TIMEZONE);
	public static final DateTimeFormatter DK_yyyy_MM_dd = ISODateTimeFormat.date().withZone(DK_TIMEZONE);

	public static Date newDateDK(int year, int month, int date)
	{
		return newDateDK(year, month, date, 0, 0, 0);
	}

	public static Timestamp newTimestampDK(int year, int month, int date)
	{
		return new Timestamp(newDateDK(year, month, date).getTime());
	}

	public static Date newDateDK(int year, int month, int date, int hours, int minutes, int secs)
	{
		return new DateTime(year, month, date, hours, minutes, secs, 0, DK_TIMEZONE).toDate();
	}

	public static Timestamp newTimestampDK(int year, int month, int date, int hours, int minutes, int secs)
	{
		return new Timestamp(newDateDK(year, month, date, hours, minutes, secs).getTime());
	}

	@Deprecated
	public static String toMySQLDateDK(Date date)
	{
		Preconditions.checkNotNull(date);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(DK_TIMEZONE.toTimeZone());
		return sdf.format(date);
	}
}
