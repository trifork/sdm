package dk.trifork.sdm.spooler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.trifork.sdm.config.Configuration;
import dk.trifork.sdm.jobspooler.JobExecutor;

public class JobSpoolerSetup {
    private static Logger logger = LoggerFactory.getLogger(FileSpoolerSetup.class);
	private final String jobName;
	
    private Class<? extends JobExecutor> executerClass;


	public JobSpoolerSetup(String jobName) {
		super();
		this.jobName = jobName;
	}
	
    public Class<? extends JobExecutor> getJobExecutorClass() {
        if (executerClass == null) {
            resolveImporterClass(getConfig("jobExecutorClass", null));
        }
        return executerClass;
    }
    
    public String getSchedule() {
    	return getConfig("schedule", null);
    }


    @SuppressWarnings("unchecked")
    private void resolveImporterClass(String executorName) {
        if (executorName == null) {
            logger.error("Configuration error. You need to configure a executer class to the job " + getName() +
                    ". Set property " + getConfigEntry("jobExecutorClass") + " to the class path of the job executor");
        }
        try {
            Class<?> executor = Class.forName(executorName);
            executerClass = (Class<? extends JobExecutor>) executor;
        } catch (ClassNotFoundException e) {
            logger.error("Configuration error. The configured job executor class (" + executorName + " could not be found. " +
            		     "Set property " + getConfigEntry("jobExecutorClass") + " to a valid job executor");
        } catch (ClassCastException e) {
            logger.error("Configuration error. The configured job executor class (" + executorName + 
            		" didn't implement interface " + JobExecutor.class.getName() + ". Set property " + getConfigEntry("jobExecutorClass") + " to a valid job executor");
        }
    }

    public String getName() {
		return jobName;
	}

	String getConfigEntry(String key) {
        return "jobspooler." + getName() + "." + key;
    }

    String getConfig(String key, String Default) {
        String value = Configuration.getString(getConfigEntry(key));
        if (value != null)
            return value;
        return Default;
    }


}
