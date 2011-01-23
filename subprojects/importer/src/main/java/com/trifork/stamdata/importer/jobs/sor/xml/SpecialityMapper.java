package com.trifork.stamdata.importer.jobs.sor.xml;


import java.util.HashMap;
import java.util.Map;


public class SpecialityMapper
{
	static Map<Long, String> specialityMap;


	private static void init()
	{
		if (specialityMap == null)
		{
			specialityMap = new HashMap<Long, String>();
			specialityMap.put(408443003L, "almen medicin");
			specialityMap.put(394577000L, "anæstesiologi");
			specialityMap.put(394821009L, "arbejdsmedicin");
			specialityMap.put(394588006L, "børne- og ungdomspsykiatri");
			specialityMap.put(394582007L, "dermato-venerologi");
			specialityMap.put(394914008L, "diagnostisk radiologi");
			specialityMap.put(394583002L, "endokrinologi");
			specialityMap.put(394811001L, "geriatri");
			specialityMap.put(394585009L, "gynækologi og obstetrik");
			specialityMap.put(408472002L, "hepatologi");
			specialityMap.put(394803006L, "hæmatologi");
			specialityMap.put(394807007L, "infektionsmedicin");
			specialityMap.put(419192003L, "intern medicin");
			specialityMap.put(394579002L, "kardiologi");
			specialityMap.put(408463005L, "karkirurgi");
			specialityMap.put(394609007L, "kirurgi");
			specialityMap.put(551411000005104L, "kirurgisk gastroenterologi");
			specialityMap.put(394596001L, "klinisk biokemi");
			specialityMap.put(394600006L, "klinisk farmakologi");
			specialityMap.put(394601005L, "klinisk fysiologi og nuklearmedicin");
			specialityMap.put(394580004L, "klinisk genetik");
			specialityMap.put(421661004L, "klinisk immunologi");
			specialityMap.put(408454008L, "klinisk mikrobiologi");
			specialityMap.put(394809005L, "klinisk neurofysiologi");
			specialityMap.put(394592004L, "klinisk onkologi");
			specialityMap.put(418112009L, "lungesygdomme");
			specialityMap.put(394805004L, "medicinsk allergologi");
			specialityMap.put(394584008L, "medicinsk gastroenterologi");
			specialityMap.put(394589003L, "nefrologi");
			specialityMap.put(394610002L, "neurokirurgi");
			specialityMap.put(394591006L, "neurologi");
			specialityMap.put(394812008L, "odontologi");
			specialityMap.put(394594003L, "oftalmologi");
			specialityMap.put(394801008L, "ortopædisk kirurgi");
			specialityMap.put(394604002L, "oto-rhino-laryngologi");
			specialityMap.put(394915009L, "patologisk anatomi og cytologi");
			specialityMap.put(394611003L, "plastik kirurgi");
			specialityMap.put(394587001L, "psykiatri");
			specialityMap.put(394537008L, "pædiatri");
			specialityMap.put(394810000L, "reumatologi");
			specialityMap.put(554011000005107L, "Retsmedicin");
			specialityMap.put(394581000L, "samfundsmedicin");
			specialityMap.put(394603008L, "thoraxkirurgi");
			specialityMap.put(408448007L, "tropemedicin");
			specialityMap.put(394612005L, "urologi");
		}
	}


	public static Map<Long, String> getMap()
	{
		init();
		return specialityMap;
	}


	public static String kodeToString(Long code)
	{
		init();
		return specialityMap.get(code);
	}

}
