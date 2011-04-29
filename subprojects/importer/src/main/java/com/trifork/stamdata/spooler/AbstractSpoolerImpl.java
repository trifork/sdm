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

package com.trifork.stamdata.spooler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSpoolerImpl {
    private static Logger logger = LoggerFactory.getLogger(AbstractSpoolerImpl.class);

    private Status status = Status.INITIATING;
    private Activity activity = Activity.AWAITING;
    private String message;

    public enum Activity {
        AWAITING, STABILIZING, IMPORTING, EXECUTING
    }

    public enum Status {
        INITIATING, RUNNING, ERROR
    }

    public Activity getActivity() {
        return activity;
    }

    void setActivity(Activity activity) {
        if (this.activity != activity) {
            this.activity = activity;
            logger.debug("Changed activity to: " + this.activity);
        }
    }

    void setStatus(Status status) {
        if (this.status == status)
            return;
        logger.debug("Status for spooler " + getName() + " changed from " + this.status + " to " + status);
        this.status = status;
        if (getStatus().equals(Status.ERROR)) {
            logger.error("Spooler " + getName() + " error message: " + getMessage());
        }
    }

    void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public Status getStatus() {
        return status;
    }

    abstract public void execute();

    abstract public String getName();

}
