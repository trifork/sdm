package dk.nsi.stamdata.cpr.pvit.proxy;

import dk.nsi.stamdata.cpr.ws.DGWSFault;

/**
 * Signals an exception while calling the BRS CPR Subscription service
 */
public class CprAbbsException extends Exception {
	public CprAbbsException(DGWSFault cause) {
		super(cause);
	}
}
