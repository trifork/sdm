package com.trifork.sdm.importer.performance;

import javax.management.Notification;
import javax.management.NotificationListener;


public class ClientListener implements NotificationListener {

	private long start;
	private long stop;
	private String error;


	public void handleNotification(Notification notification, Object handback) {

		if (notification.getMessage() != null) {
			if (notification.getMessage().equals("Starting Takst import")) {
				start = System.currentTimeMillis();
			}
			else if (notification.getMessage().equals("Takst import ok")) {
				stop = System.currentTimeMillis();
			}
			else if (notification.getMessage().startsWith("Takst import failed: ")) {
				error = notification.getMessage();
			}
		}
	}


	public boolean hasError() {

		return error != null && !error.equals("");
	}


	public String getError() {

		return error;
	}


	public long getDuration() {

		return stop - start;
	}
}
