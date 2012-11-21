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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

public class MunicipalityMapper {
	private static final Logger logger = Logger.getLogger(MunicipalityMapper.class);

	public String toCountyCode(String municipalityCode) {
		String countyCode = municipalityToCounty.getProperty(municipalityCode);
		if (countyCode == null) {
			logger.error("Unknown municipality: " + municipalityCode);
			countyCode = "99";
		}
		
		return countyCode;
	}

	private static Properties municipalityToCounty = new Properties();
	static {
		ClassLoader classLoader = MunicipalityMapper.class.getClassLoader();
		InputStream isInternal = classLoader.getResourceAsStream("municipalities-internal.properties");
		InputStream isExternal = classLoader.getResourceAsStream("municipalities-external.properties");

		try {
			municipalityToCounty.load(isInternal);

			// external file is optional, but will overwrite settings in the internal file
			if (isExternal != null) {
				municipalityToCounty.load(isExternal);
			}
		}
		catch (IOException e) {
			throw new RuntimeException("Problem loading one of the municipalities.properties file", e);
		}
		finally {
			IOUtils.closeQuietly(isExternal);
			IOUtils.closeQuietly(isInternal);
		}
	}
}
