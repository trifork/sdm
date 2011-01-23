package com.trifork.stamdata.importer.jobs.sor.xml;


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
