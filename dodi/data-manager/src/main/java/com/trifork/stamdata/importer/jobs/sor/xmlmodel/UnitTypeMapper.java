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


package com.trifork.stamdata.importer.jobs.sor.xmlmodel;

import java.util.HashMap;
import java.util.Map;


public class UnitTypeMapper
{
	static Map<Long, String> unitTypeMap;

	private static void init()
	{
		if (unitTypeMap == null)
		{
			unitTypeMap = new HashMap<Long, String>();
			unitTypeMap.put(550811000005108L, "administrativ enhed");
			unitTypeMap.put(550871000005101L, "akut modtage enhed");
			unitTypeMap.put(309964003L, "billeddiagnostisk enhed");
			unitTypeMap.put(2104671000005110L, "ergoterapiklinik");
			unitTypeMap.put(550841000005107L, "forskningsenhed");
			unitTypeMap.put(550861000005106L, "fysioterapi- og ergoterapiklinik");
			unitTypeMap.put(547011000005103L, "fysioterapiklinik");
			unitTypeMap.put(309904001L, "intensiv enhed");
			unitTypeMap.put(550631000005103L, "jordemoderklinik");
			unitTypeMap.put(550851000005109L, "klinisk enhed");
			unitTypeMap.put(726L, "KONVAFD");
			unitTypeMap.put(551611000005102L, "operationsgang");
			unitTypeMap.put(550821000005102L, "service enhed");
			unitTypeMap.put(225728007L, "skadestue");
			unitTypeMap.put(554071000005100L, "sygehusapotek");
			unitTypeMap.put(550831000005104L, "uddannelsesenhed");
		}
	}

	public static Map<Long, String> getMap()
	{
		init();
		return unitTypeMap;
	}

	public static String kodeToString(Long code)
	{
		init();
		return unitTypeMap.get(code);
	}

}
