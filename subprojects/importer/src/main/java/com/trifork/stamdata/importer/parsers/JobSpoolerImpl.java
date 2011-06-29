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

package com.trifork.stamdata.importer.parsers;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.importer.util.DateUtils;


public class JobSpoolerImpl extends AbstractSpoolerImpl {
    private static Logger logger = LoggerFactory.getLogger(FileSpoolerImpl.class);

    final private JobSpoolerSetup setup;
	private Job executor;

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
