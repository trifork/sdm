package dk.trifork.sdm;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.slf4j.Logger;


public class ProjectInfo {

	private static final Logger logger = getLogger(ProjectInfo.class);

	private String deployType;
	private String buildInfo;
	private String vendor;
	private String title;
	private String version;

	public ProjectInfo(ServletContext context) {

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
