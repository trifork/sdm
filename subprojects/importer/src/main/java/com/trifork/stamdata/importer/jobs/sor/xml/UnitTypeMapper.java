package com.trifork.stamdata.importer.jobs.sor.xml;


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
