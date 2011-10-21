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

package dk.nsi.stamdata.replication.webservice;

import java.util.*;


/**
 * A helper class that formats dates for the Atom feed specification.
 * 
 * Atom feeds require a specific date format.
 * 
 * @author Thomas Børlum <thb@trifork.com>
 */
public class AtomDate
{
	/**
	 * Create the serialized string form from a java.util.Date
	 * 
	 * @param date A java.util.Date
	 * @return The serialized string form of the date
	 */
	public static String toString(Date date)
	{
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
