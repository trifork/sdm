package dk.nsi.stamdata.cpr.mapping;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;


public class MunicipalityMapper
{
	public String toCountyCode(String municipalityCode)
	{
		String countyCode = municipalityToCounty.get(municipalityCode);
		Preconditions.checkState(countyCode != null, "The municipality code '%s' is not associated with any county. There is an error in the registry or CPR-service.", municipalityCode);
		return countyCode;
	}


	private static final Map<String, String> municipalityToCounty;
	static
	{
		// Ordering the entries in a tree by name should be faster than
		// a hash map.

		municipalityToCounty = Maps.newTreeMap(Ordering.usingToString());

		// Region Hovedstaden

		municipalityToCounty.put("101", "1084");
		municipalityToCounty.put("147", "1084");
		municipalityToCounty.put("151", "1084");
		municipalityToCounty.put("153", "1084");
		municipalityToCounty.put("155", "1084");
		municipalityToCounty.put("157", "1084");
		municipalityToCounty.put("159", "1084");
		municipalityToCounty.put("161", "1084");
		municipalityToCounty.put("163", "1084");
		municipalityToCounty.put("165", "1084");
		municipalityToCounty.put("167", "1084");
		municipalityToCounty.put("169", "1084");
		municipalityToCounty.put("173", "1084");
		municipalityToCounty.put("175", "1084");
		municipalityToCounty.put("230", "1084");
		municipalityToCounty.put("183", "1084");
		municipalityToCounty.put("185", "1084");
		municipalityToCounty.put("187", "1084");
		municipalityToCounty.put("201", "1084");
		municipalityToCounty.put("190", "1084");
		municipalityToCounty.put("210", "1084");
		municipalityToCounty.put("250", "1084");
		municipalityToCounty.put("260", "1084");
		municipalityToCounty.put("270", "1084");
		municipalityToCounty.put("217", "1084");
		municipalityToCounty.put("219", "1084");
		municipalityToCounty.put("223", "1084");
		municipalityToCounty.put("240", "1084");
		municipalityToCounty.put("400", "1084");
		municipalityToCounty.put("411", "1084");

		// Region Sjælland

		municipalityToCounty.put("350", "1085");
		municipalityToCounty.put("253", "1085");
		municipalityToCounty.put("259", "1085");
		municipalityToCounty.put("265", "1085");
		municipalityToCounty.put("269", "1085");
		municipalityToCounty.put("306", "1085");
		municipalityToCounty.put("320", "1085");
		municipalityToCounty.put("316", "1085");
		municipalityToCounty.put("326", "1085");
		municipalityToCounty.put("329", "1085");
		municipalityToCounty.put("330", "1085");
		municipalityToCounty.put("340", "1085");
		municipalityToCounty.put("360", "1085");
		municipalityToCounty.put("376", "1085");
		municipalityToCounty.put("370", "1085");
		municipalityToCounty.put("336", "1085");
		municipalityToCounty.put("390", "1085");

		// Region Syddanmark (1083)

		municipalityToCounty.put("420", "1083");
		municipalityToCounty.put("430", "1083");
		municipalityToCounty.put("440", "1083");
		municipalityToCounty.put("410", "1083");
		municipalityToCounty.put("450", "1083");
		municipalityToCounty.put("461", "1083");
		municipalityToCounty.put("479", "1083");
		municipalityToCounty.put("482", "1083");
		municipalityToCounty.put("480", "1083");
		municipalityToCounty.put("492", "1083");
		municipalityToCounty.put("510", "1083");
		municipalityToCounty.put("580", "1083");
		municipalityToCounty.put("561", "1083");
		municipalityToCounty.put("563", "1083");
		municipalityToCounty.put("530", "1083");
		municipalityToCounty.put("573", "1083");
		municipalityToCounty.put("575", "1083");
		municipalityToCounty.put("607", "1083");
		municipalityToCounty.put("621", "1083");
		municipalityToCounty.put("630", "1083");
		municipalityToCounty.put("766", "1083");
		municipalityToCounty.put("615", "1083");
		municipalityToCounty.put("657", "1083");
		municipalityToCounty.put("661", "1083");
		municipalityToCounty.put("756", "1083");

		// Region Midtjylland (1082)

		municipalityToCounty.put("665", "1082");
		municipalityToCounty.put("760", "1082");
		municipalityToCounty.put("671", "1082");
		municipalityToCounty.put("706", "1082");
		municipalityToCounty.put("707", "1082");
		municipalityToCounty.put("710", "1082");
		municipalityToCounty.put("727", "1082");
		municipalityToCounty.put("730", "1082");
		municipalityToCounty.put("741", "1082");
		municipalityToCounty.put("740", "1082");
		municipalityToCounty.put("746", "1082");
		municipalityToCounty.put("751", "1082");
		municipalityToCounty.put("779", "1082");
		municipalityToCounty.put("791", "1082");

		// Region Nordjylland (1081)

		municipalityToCounty.put("773", "1081");
		municipalityToCounty.put("787", "1081");
		municipalityToCounty.put("810", "1081");
		municipalityToCounty.put("813", "1081");
		municipalityToCounty.put("860", "1081");
		municipalityToCounty.put("846", "1081");
		municipalityToCounty.put("825", "1081");
		municipalityToCounty.put("840", "1081");
		municipalityToCounty.put("849", "1081");
		municipalityToCounty.put("851", "1081");
		municipalityToCounty.put("820", "1081");
	}
}
