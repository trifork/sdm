package dk.trifork.sdm.spooler;

import dk.trifork.sdm.config.Configuration;

import it.sauronsoftware.cron4j.Scheduler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FileSpooler. Initiates and monitor file spoolers.
 *
 * @author Jan Buchholdt
 * 
 */
public class SpoolerManager {
    Map<String, FileSpoolerImpl> spoolers = new HashMap<String, FileSpoolerImpl>();
    Map<String, JobSpoolerImpl> jobSpoolers = new HashMap<String, JobSpoolerImpl>();

    private static final Logger logger = LoggerFactory.getLogger(SpoolerManager.class);
    private static final int POLLING_INTERVAL = Configuration.getInt("inputfile.polling.interval");
    private Timer timer = new Timer(true);
    private List<JobSpoolerImpl> jobQueue = Collections.synchronizedList(new LinkedList<JobSpoolerImpl>());
    private Scheduler jobScheduler = new Scheduler();


    public SpoolerManager(String rootDir) {
        String spoolerSetup = Configuration.getString("spooler");
        logger.debug("The following spoolers are configured: " + spoolerSetup);
        logger.debug("The global root dir is set to: " + rootDir);

        if (spoolerSetup.length() == 0) {
            logger.error("Manager created but no spooler configured. Please configure a spooler");
            return;
        }

        for (String spoolerName : spoolerSetup.split(",")) {
            spoolers.put(spoolerName, new FileSpoolerImpl(new FileSpoolerSetup(spoolerName, rootDir)));
        }
        
        String jobSpoolerSetup = Configuration.getString("jobspooler");
        logger.debug("The following job spoolers are configured: " + jobSpoolerSetup);

        for (String jobSpoolerName : jobSpoolerSetup.split(",")) {
        	JobSpoolerImpl jobSpooler = new JobSpoolerImpl(new JobSpoolerSetup(jobSpoolerName));
            jobSpoolers.put(jobSpoolerName, jobSpooler);
            jobScheduler.schedule(jobSpooler.getSetup().getSchedule(), new GernericJobSpoolerExecutor(jobSpooler));
        }

        jobScheduler.start();
        TimerTask pollTask = new PollingTask();
        timer.schedule(pollTask, 10 * 1000, POLLING_INTERVAL * 1000);
        
    }

    public void destroy() {
        timer.cancel();
        jobScheduler.stop();
    }

    /**
     * Checks that all configured spoolers exist and are running
     */
    public boolean isAllSpoolersRunning() {
        for (FileSpoolerImpl spooler : spoolers.values()) {
            if (!spooler.getStatus().equals(FileSpoolerImpl.Status.RUNNING))
                return false;
        }
        for (JobSpoolerImpl spooler : jobSpoolers.values()) {
            if (!spooler.getStatus().equals(JobSpoolerImpl.Status.RUNNING))
                return false;
        }
        return true;
    }

    /**
     * @param uriString
     * @return the uri converted to a file path. null if the uri was not a file uri
     */
    public static String uri2filepath(String uriString) {
        URI uri;
        try {
            uri = new URI(uriString);
            if (!"file".equals(uri.getScheme())) {
                String errorMessage = "uri2filepath(" + uriString + ") can only convert uri with scheme: 'file'!";
                logger.error(errorMessage);
                return null;
            }
            return uri.getPath();
        } catch (URISyntaxException e) {
            String errorMessage = "uri2filepath must be called with a uri";
            logger.error(errorMessage);
            return null;
        }
    }

    public boolean isAllRejectedDirsEmpty() {
        for (FileSpoolerImpl spooler : spoolers.values()) {
            if (!spooler.isRejectedDirEmpty()) {
                return false;
            }
        }
        return true;
    }


    public boolean isRejectDirEmpty(String type) {
        FileSpoolerImpl spooler = spoolers.get(type);
        if (spooler == null)
            return false;
        return spooler.isRejectedDirEmpty();
    }


    public boolean isNoOverdueImports() {
        for (FileSpoolerImpl spooler : spoolers.values()) {
            if (spooler.isOverdue()) {
                return false;
            }
        }
        return true;
    }

    public Map<String, FileSpoolerImpl> getSpoolers() {
        return spoolers;
    }

    public FileSpoolerImpl getSpooler(String type) {
        return spoolers.get(type);
    }

    public Map<String, JobSpoolerImpl> getJobSpoolers() {
        return jobSpoolers;
    }

    public JobSpoolerImpl getJobSpooler(String type) {
        return jobSpoolers.get(type);
    }

    public class PollingTask extends TimerTask {
        Throwable t;

        public void run() {
        	ExecutePendingJobs(); 
            for (FileSpoolerImpl spooler : spoolers.values()) {
                try {
                    spooler.execute();
                } catch (Throwable t) {
                    if (this.t == null || !t.getMessage().equals(this.t.getMessage())) {
                        logger.debug("Caught throwable while polling. Only logging once to avoid log file spamming", t);
                        this.t = t;
                    }
                }
                ExecutePendingJobs();
            }
        }

    }
    
    private void ExecutePendingJobs() {
        Throwable lastt = null;

        while (!jobQueue.isEmpty()) {
    		JobSpoolerImpl next = jobQueue.get(0);
            try {
        		next.execute();
        		jobQueue.remove(0);
            } catch (Throwable t) {
                if (lastt == null || !t.getMessage().equals(lastt.getMessage())) {
                    logger.debug("Caught throwable while running job " + next.getName() + ". Only logging once to avoid log file spamming", t);
                    lastt = t;
                }
            	
            }
    	}
    }
    
    public class GernericJobSpoolerExecutor implements Runnable  {
    	final private JobSpoolerImpl jobImpl;
    	
		public GernericJobSpoolerExecutor(JobSpoolerImpl jobImpl) {
			super();
			this.jobImpl = jobImpl;
		}

		@Override
		public void run() {
			// The job is activated. Add it to the jobQueue. 
			// The JobQueue is emptied in the polling task to avoid two jobs running simultaneously  
			jobQueue.add(jobImpl);
		}
    	
    }

}
