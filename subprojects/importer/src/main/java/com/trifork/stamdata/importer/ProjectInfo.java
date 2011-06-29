// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.slf4j.Logger;

import com.google.inject.Inject;


public class ProjectInfo {

	private static final Logger logger = getLogger(ProjectInfo.class);

	private String deployType;
	private String buildInfo;
	private String vendor;
	private String title;
	private String version;

	@Inject
	ProjectInfo(ServletContext context) {

		InputStream inputStream = context.getResourceAsStream("/META-INF/MANIFEST.MF");

		if (inputStream != null) {
			try {

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				String line = reader.readLine();
				while (line != null) {
					if (line.startsWith("Built-Date:")) {
						buildInfo = line.substring("Built-Date:".length() + 1);
					}
					if (line.startsWith("Implementation-Vendor:")) {
						vendor = line.substring("Implementation-Vendor:".length() + 1);
					}
					if (line.startsWith("Implementation-Title:")) {
						title = line.substring("Implementation-Title:".length() + 1);
					}
					if (line.startsWith("Built-Version:")) {
						version = line.substring("Built-Version:".length() + 1);
					}
					line = reader.readLine();
				}

				reader.close();

				deployType = "WAR";
			}
			catch (Exception e) {

				logger.error("Could not load file: 'META-INF/MANIFEST.MF'.", e);
			}
		}
		else {
			deployType = "Unknown";
			buildInfo = "Unknown";
			vendor = "Unknown";
			title = "Unknown";
			version = "Unknown";
		}
	}

	public String getDeployType() {

		return deployType;
	}

	public String getBuildInfo() {

		return buildInfo;
	}

	public String getVendor() {

		return vendor;
	}

	public String getTitle() {

		return title;
	}

	public String getVersion() {

		return version;
	}
}
