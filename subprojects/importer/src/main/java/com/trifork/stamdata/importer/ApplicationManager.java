package com.trifork.stamdata.importer;


import static com.trifork.stamdata.importer.AppAttributes.CONFIGURATION;
import static com.trifork.stamdata.importer.AppAttributes.CONNECTION_FACTORY;
import static com.trifork.stamdata.importer.AppAttributes.ERROR;
import static com.trifork.stamdata.importer.AppAttributes.JOB_MANAGER;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;

import com.trifork.stamdata.importer.jobs.JobManager;
import com.trifork.stamdata.importer.jobs.NavnebeskyttelseRestrukt;
import com.trifork.stamdata.importer.jobs.autorisationsregisteret.AutorisationImporter;
import com.trifork.stamdata.importer.jobs.cpr.CprImporter;
import com.trifork.stamdata.importer.jobs.sks.SksImporter;
import com.trifork.stamdata.importer.jobs.sor.SorImporter;
import com.trifork.stamdata.importer.jobs.takst.TakstImporter;
import com.trifork.stamdata.importer.jobs.yderregisteret.YderImporter;
import com.trifork.stamdata.importer.persistence.ConnectionFactory;


public class ApplicationManager implements ServletContextListener
{
	private static final Logger LOG = getLogger(ApplicationManager.class);

	private Configuration config;
	private ConnectionFactory connectionFactory;
	private String defaultSchedule;
	private File rootDir;


	@Override
	public void contextInitialized(ServletContextEvent event)
	{
		LOG.info("Starting application.");

		ServletContext context = event.getServletContext();
		context.setAttribute(ERROR, true);

		try
		{
			LOG.info("Loading configuration.");

			config = new Configuration("/config.properties");
			context.setAttribute(CONFIGURATION, config);

			LOG.info("Setting up database connection.");

			configureDatabaseConnection(context, config);

			LOG.info("Initializing worker threads.");

			prepareJobs(context);

			LOG.info("Application initialized.");

			context.setAttribute(ERROR, false);
		}
		catch (ConfigurationException e)
		{
			LOG.error("Application failed durring initialization.", e);
		}
		catch (Throwable t)
		{
			LOG.error("Unknown expection was thrown during initialization.", t);
		}
	}


	@Override
	public void contextDestroyed(ServletContextEvent event)
	{
		LOG.info("Shutting down.");
	}


	private void configureDatabaseConnection(ServletContext context, Configuration config) throws ConfigurationException
	{
		String dbURI = config.getStringProperty("db.uri");
		String dbUsername = config.getStringProperty("db.username");
		String dbPassword = config.getStringProperty("db.password", "");
		String dbMainSchema = config.getStringProperty("db.schema.main");
		String dbHousekeepingSchema = config.getStringProperty("db.schema.housekeeping");

		connectionFactory = new ConnectionFactory(dbURI, dbMainSchema, dbHousekeepingSchema, dbUsername, dbPassword);
		context.setAttribute(CONNECTION_FACTORY, connectionFactory);
	}


	private void prepareJobs(ServletContext context) throws ConfigurationException
	{
		rootDir = new File(config.getStringProperty("rootDir"));
		defaultSchedule = config.getStringProperty("defaultSchedule");

		JobManager manager = new JobManager();

		scheduleJob(manager, "navnebeskyttelse", new NavnebeskyttelseRestrukt(connectionFactory));
		scheduleJob(manager, "cprImporter", new CprImporter(rootDir, connectionFactory));
		scheduleJob(manager, "takstImporter", new TakstImporter(rootDir, connectionFactory));
		scheduleJob(manager, "autorisationImporter", new AutorisationImporter(rootDir, connectionFactory));
		scheduleJob(manager, "sksImporter", new SksImporter(rootDir, connectionFactory));
		scheduleJob(manager, "sorImporter", new SorImporter(rootDir, connectionFactory));
		scheduleJob(manager, "yderImporter", new YderImporter(rootDir, connectionFactory));
		
		manager.run();
		
		context.setAttribute(JOB_MANAGER, manager);
	}


	private void scheduleJob(JobManager manager, String property, Runnable job) throws ConfigurationException
	{
		if (config.getBooleanProperty(property + ".enabled"))
		{
			String schedule = config.getStringProperty(property + ".schedule", defaultSchedule);
			manager.add(job, schedule);
		}
	}
}
