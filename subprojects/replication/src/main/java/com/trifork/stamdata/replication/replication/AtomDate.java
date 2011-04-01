// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication;

import java.util.*;


public class AtomDate {

	/**
	 * Create the serialized string form from a java.util.Date
	 * 
	 * @param date
	 *            A java.util.Date
	 * @return The serialized string form of the date
	 */
	public static String format(Date date) {

		StringBuilder sb = new StringBuilder();
		Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		c.setTime(date);
		sb.append(c.get(Calendar.YEAR));
		sb.append('-');
		int f = c.get(Calendar.MONTH);
		if (f < 9) sb.append('0');
		sb.append(f + 1);
		sb.append('-');
		f = c.get(Calendar.DATE);
		if (f < 10) sb.append('0');
		sb.append(f);
		sb.append('T');
		f = c.get(Calendar.HOUR_OF_DAY);
		if (f < 10) sb.append('0');
		sb.append(f);
		sb.append(':');
		f = c.get(Calendar.MINUTE);
		if (f < 10) sb.append('0');
		sb.append(f);
		sb.append(':');
		f = c.get(Calendar.SECOND);
		if (f < 10) sb.append('0');
		sb.append(f);
		sb.append('.');
		f = c.get(Calendar.MILLISECOND);
		if (f < 100) sb.append('0');
		if (f < 10) sb.append('0');
		sb.append(f);
		sb.append('Z');
		return sb.toString();
	}
}
