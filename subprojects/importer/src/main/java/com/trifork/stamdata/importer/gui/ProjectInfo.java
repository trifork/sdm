package com.trifork.stamdata.importer.gui;


import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ProjectInfo
{
	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

	public String buildDate = "Unknown";
	public String vendor = "Unknown";
	public String title = "Unknown";
	public String version = "Unknown";


	public ProjectInfo(final ServletContext context)
	{
		try
		{
			InputStream fileStream = context.getResourceAsStream(MANIFEST_PATH);
			Manifest manifest = new Manifest(fileStream);
			Attributes attributes = (Attributes) manifest.getMainAttributes();

			version = attributes.getValue("Built-Version");
			buildDate = attributes.getValue("Built-Date");
			vendor = attributes.getValue("Implementation-Vendor");
			title = attributes.getValue("Implementation-Title");

			fileStream.close();
		}
		catch (IOException e)
		{
			Logger logger = LoggerFactory.getLogger(getClass());
			logger.error("Could not load meta-data from the manifest file.", e);
		}
	}
}
