package com.trifork.sdm.replication.admin.views;

import java.io.File;
import java.net.URL;

import com.google.inject.AbstractModule;

import freemarker.template.*;


public class TemplateModule extends AbstractModule
{

	@Override
	protected void configure()
	{

		// We use freemaker to template HTML.
		// The template files can be found in the webapp-dir
		// and all have the extension .ftl.

		Configuration config = new Configuration();

		String TEMPLATE_DIR = "views";
		URL TEMPLATE_DIR_URL = getClass().getClassLoader().getResource(TEMPLATE_DIR);

		try
		{
			File templateDir = new File(TEMPLATE_DIR_URL.toURI());

			// Specify the data source where the template files come from.
			// Here I set a file directory for it:

			config.setDirectoryForTemplateLoading(templateDir);

			// Specify how templates will see the data-model.
			// We just use the default:

			config.setObjectWrapper(new DefaultObjectWrapper());

			bind(Configuration.class).toInstance(config);
		}
		catch (Throwable t)
		{

			addError("Invalid template directory.", t);
		}
	}
}
