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

import java.text.*;
import java.util.*;

import org.joda.time.*;
import org.joda.time.format.*;

import com.trifork.stamdata.Preconditions;


public class Dates
{
	// Is is safe to assume that most registries will note time in CET as it is
	// the Danish time zone.
	// You do have to be careful through. It is not always clear what the
	// default time zone it set to and setting the TimeZone.setDefault() could
	// change the default for the entire JVM! (depending on JVM version)

	public static final DateTimeZone CET_TIMEZONE = DateTimeZone.forID("Europe/Copenhagen");

	public static final DateTimeFormatter CET_yyyyMMdd = ISODateTimeFormat.basicDate().withZone(CET_TIMEZONE);
	public static final DateTimeFormatter CET_yyyyMMddHHmm = DateTimeFormat.forPattern("yyyyMMddHHmm").withZone(CET_TIMEZONE);
	public static final DateTimeFormatter CET_yyyy_MM_dd = ISODateTimeFormat.date().withZone(CET_TIMEZONE);

	public static final Date THE_END_OF_TIME = new DateTime(2999, 12, 31, 0, 0, 0, 0, DateTimeZone.UTC).toDate();

	@Deprecated
	public static Date toCETDate(int year, int month, int date)
	{
		return new DateTime().withDate(year, month, date).withZone(CET_TIMEZONE).toDate();
	}

	@Deprecated
	public static Date toCETDate(int year, int month, int date, int hours, int minutes, int secs)
	{
		return new DateTime(year, month, date, hours, minutes, secs, 0, CET_TIMEZONE).toDate();
	}

	@Deprecated
	public static String toMySQLdate(Date date)
	{
		Preconditions.checkNotNull(date);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}
}
