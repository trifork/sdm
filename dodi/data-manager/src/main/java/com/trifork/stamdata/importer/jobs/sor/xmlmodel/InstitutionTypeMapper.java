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


public class InstitutionTypeMapper
{
	static Map<Long, String> institutionTypeMap;

	private static void init()
	{
		if (institutionTypeMap == null)
		{
			institutionTypeMap = new HashMap<Long, String>();
			institutionTypeMap.put(550411000005105L, "andet sundhedsvæsen");
			institutionTypeMap.put(264372000L, "apotek");
			institutionTypeMap.put(20078004L, "behandlingscenter for stofmisbrugere");
			institutionTypeMap.put(546821000005103L, "ergoterapiklinik");
			institutionTypeMap.put(547011000005103L, "fysioterapiklinik");
			institutionTypeMap.put(546811000005109L, "genoptræningscenter");
			institutionTypeMap.put(550621000005101L, "hjemmesygepleje");
			institutionTypeMap.put(22232009L, "hospital");
			institutionTypeMap.put(550631000005103L, "jordemoderklinik");
			institutionTypeMap.put(550641000005106L, "kiropraktor klinik");
			institutionTypeMap.put(550651000005108L, "lægelaboratorium");
			institutionTypeMap.put(394761003L, "lægepraksis");
			institutionTypeMap.put(550661000005105L, "lægevagt");
			institutionTypeMap.put(42665001L, "plejehjem");
			institutionTypeMap.put(550711000005101L, "psykologisk rådgivningsklinik");
			institutionTypeMap.put(550671000005100L, "speciallægepraksis");
			institutionTypeMap.put(264361005L, "sundhedscenter");
			institutionTypeMap.put(554061000005105L, "Statsautoriseret fodterapeut");
			institutionTypeMap.put(550681000005102L, "tandlægepraksis");
			institutionTypeMap.put(550691000005104L, "tandpleje klinik");
			institutionTypeMap.put(550701000005104L, "tandteknisk klinik");
		}
	}

	public static Map<Long, String> getMap()
	{
		init();
		return institutionTypeMap;
	}

	public static String kodeToString(Long code)
	{
		init();
		return institutionTypeMap.get(code);
	}

}
