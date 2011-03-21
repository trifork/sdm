package dk.trifork.sdm;

import dk.trifork.sdm.config.Configuration;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProjectInfo {
	private String deployType = "Unknown";
	private String buildInfo = "Unknown";
	private String vendor = "Unknown";
	private String title = "Unknown";
	private String version = "Unknown";
	
	public ProjectInfo(ServletContext sctx) {
		try {
			deployType = "WAR";
			InputStream is = sctx.getResourceAsStream("META-INF/MANIFEST.MF");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = reader.readLine();
			while (line != null){
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
		} catch (Exception e) {
			// No Manifest. Properly a inplace deployment in development environment
			deployType = "INPLACE";
			buildInfo =  (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")).format(new Date());
			vendor = "Trifork";
			title = "sdm";
			version = Configuration.getString("version could not be extracted from MANIFEST.MF");
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
