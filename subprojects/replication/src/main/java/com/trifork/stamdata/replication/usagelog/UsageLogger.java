package com.trifork.stamdata.replication.usagelog;

import java.util.Date;

import javax.inject.Inject;

import org.hibernate.Session;

import com.trifork.stamdata.replication.replication.views.usagelog.UsageLogEntry;

public class UsageLogger {

	private final Session session;

	@Inject
	public UsageLogger(Session session) {
		this.session = session;
	}

	public void log(String clientId, String type, int amount) {
		session.save(new UsageLogEntry(clientId, new Date(), type, amount));
	}

}
