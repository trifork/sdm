package dk.trifork.sdm.spooler;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.trifork.sdm.jobspooler.JobExecutor;
import dk.trifork.sdm.util.DateUtils;

public class JobSpoolerImpl extends AbstractSpoolerImpl {
    private static Logger logger = LoggerFactory.getLogger(FileSpoolerImpl.class);
    
    final private JobSpoolerSetup setup;
	private JobExecutor executor;
	
    Calendar lastRun = null;


	public JobSpoolerImpl(JobSpoolerSetup setup) {
		super();
		this.setup = setup;
		try {
			executor = setup.getJobExecutorClass().newInstance();
		} catch (Exception e) {
            logger.error("Could not instantiate importer of class", e);
            setMessage("Spooler cannot get an instance if importer class. Please change the setup");
            setStatus(Status.ERROR);
            return;
        }
		setStatus(Status.RUNNING);
		setActivity(Activity.AWAITING);
	}

	public JobSpoolerSetup getSetup() {
		return setup;
	}
	
    public String getLastRunFormatted() {
		if (lastRun  == null)
            return "Never";
        else
            return DateUtils.toMySQLdate(lastRun);
    }

    public Calendar getLastRun() {
        return lastRun;
    }


	@Override
	public String getName() {
		return setup.getName();
	}

	@Override
	public void execute() {
		if (getStatus() == Status.RUNNING) {
			try {
				setActivity(Activity.EXECUTING);
				executor.run();
				Calendar runTime = Calendar.getInstance();
				runTime.setTime(new Date());
				lastRun = runTime;
				setActivity(Activity.AWAITING);
			} catch (Exception e) {
	            logger.error("Job "+ getName() + " faild during executing the job.", e);
	            setMessage("Job "+ getName() + " faild during executing the job.");
	            setStatus(Status.ERROR);
	            return;
			}
		}
	}
}
