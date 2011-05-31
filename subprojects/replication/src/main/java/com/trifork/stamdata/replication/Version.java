package com.trifork.stamdata.replication;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * Main class that prints out the distribution verison.
 * 
 * Facility for the maintainer:
 * 
 * java -jar <war-file>
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class Version {
	
	public static void main(String[] args) throws IOException {
		
		InputStream inputStream = Version.class.getClassLoader().getResourceAsStream("/META-INF/MANIFEST.MF");
		Manifest manifest = new Manifest(inputStream);
		
		String versionParamName = java.util.jar.Attributes.Name.IMPLEMENTATION_VERSION.toString();
		
		Attributes attributes = manifest.getAttributes(versionParamName);
		String version = attributes.getValue(versionParamName);
		
		System.out.print("version: " + version);
	}
}
