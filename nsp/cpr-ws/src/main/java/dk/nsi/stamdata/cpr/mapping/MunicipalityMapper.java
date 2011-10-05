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

		municipalityToCounty.put("0101", "1084");
		municipalityToCounty.put("0147", "1084");
		municipalityToCounty.put("0151", "1084");
		municipalityToCounty.put("0153", "1084");
		municipalityToCounty.put("0155", "1084");
		municipalityToCounty.put("0157", "1084");
		municipalityToCounty.put("0159", "1084");
		municipalityToCounty.put("0161", "1084");
		municipalityToCounty.put("0163", "1084");
		municipalityToCounty.put("0165", "1084");
		municipalityToCounty.put("0167", "1084");
		municipalityToCounty.put("0169", "1084");
		municipalityToCounty.put("0173", "1084");
		municipalityToCounty.put("0175", "1084");
		municipalityToCounty.put("0230", "1084");
		municipalityToCounty.put("0183", "1084");
		municipalityToCounty.put("0185", "1084");
		municipalityToCounty.put("0187", "1084");
		municipalityToCounty.put("0201", "1084");
		municipalityToCounty.put("0190", "1084");
		municipalityToCounty.put("0210", "1084");
		municipalityToCounty.put("0250", "1084");
		municipalityToCounty.put("0260", "1084");
		municipalityToCounty.put("0270", "1084");
		municipalityToCounty.put("0217", "1084");
		municipalityToCounty.put("0219", "1084");
		municipalityToCounty.put("0223", "1084");
		municipalityToCounty.put("0240", "1084");
		municipalityToCounty.put("0400", "1084");
		municipalityToCounty.put("0411", "1084");

		// Region Sjælland

		municipalityToCounty.put("0350", "1085");
		municipalityToCounty.put("0253", "1085");
		municipalityToCounty.put("0259", "1085");
		municipalityToCounty.put("0265", "1085");
		municipalityToCounty.put("0269", "1085");
		municipalityToCounty.put("0306", "1085");
		municipalityToCounty.put("0320", "1085");
		municipalityToCounty.put("0316", "1085");
		municipalityToCounty.put("0326", "1085");
		municipalityToCounty.put("0329", "1085");
		municipalityToCounty.put("0330", "1085");
		municipalityToCounty.put("0340", "1085");
		municipalityToCounty.put("0360", "1085");
		municipalityToCounty.put("0376", "1085");
		municipalityToCounty.put("0370", "1085");
		municipalityToCounty.put("0336", "1085");
		municipalityToCounty.put("0390", "1085");

		// Region Syddanmark (1083)

		municipalityToCounty.put("0420", "1083");
		municipalityToCounty.put("0430", "1083");
		municipalityToCounty.put("0440", "1083");
		municipalityToCounty.put("0410", "1083");
		municipalityToCounty.put("0450", "1083");
		municipalityToCounty.put("0461", "1083");
		municipalityToCounty.put("0479", "1083");
		municipalityToCounty.put("0482", "1083");
		municipalityToCounty.put("0480", "1083");
		municipalityToCounty.put("0492", "1083");
		municipalityToCounty.put("0510", "1083");
		municipalityToCounty.put("0580", "1083");
		municipalityToCounty.put("0561", "1083");
		municipalityToCounty.put("0563", "1083");
		municipalityToCounty.put("0530", "1083");
		municipalityToCounty.put("0573", "1083");
		municipalityToCounty.put("0575", "1083");
		municipalityToCounty.put("0607", "1083");
		municipalityToCounty.put("0621", "1083");
		municipalityToCounty.put("0630", "1083");
		municipalityToCounty.put("0766", "1083");
		municipalityToCounty.put("0615", "1083");
		municipalityToCounty.put("0657", "1083");
		municipalityToCounty.put("0661", "1083");
		municipalityToCounty.put("0756", "1083");

		// Region Midtjylland (1082)

		municipalityToCounty.put("0665", "1082");
		municipalityToCounty.put("0760", "1082");
		municipalityToCounty.put("0671", "1082");
		municipalityToCounty.put("0706", "1082");
		municipalityToCounty.put("0707", "1082");
		municipalityToCounty.put("0710", "1082");
		municipalityToCounty.put("0727", "1082");
		municipalityToCounty.put("0730", "1082");
		municipalityToCounty.put("0741", "1082");
		municipalityToCounty.put("0740", "1082");
		municipalityToCounty.put("0746", "1082");
		municipalityToCounty.put("0751", "1082");
		municipalityToCounty.put("0779", "1082");
		municipalityToCounty.put("0791", "1082");

		// Region Nordjylland (1081)

		municipalityToCounty.put("0773", "1081");
		municipalityToCounty.put("0787", "1081");
		municipalityToCounty.put("0810", "1081");
		municipalityToCounty.put("0813", "1081");
		municipalityToCounty.put("0860", "1081");
		municipalityToCounty.put("0846", "1081");
		municipalityToCounty.put("0825", "1081");
		municipalityToCounty.put("0840", "1081");
		municipalityToCounty.put("0849", "1081");
		municipalityToCounty.put("0851", "1081");
		municipalityToCounty.put("0820", "1081");
	}
}
