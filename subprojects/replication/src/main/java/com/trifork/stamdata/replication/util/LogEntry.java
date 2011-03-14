package com.trifork.stamdata.replication.util;

import static javax.persistence.TemporalType.TIMESTAMP;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;


@Entity
public class LogEntry {

	@Id
	@GeneratedValue
	private String id;

	private String message;

	@Temporal(TIMESTAMP)
	private Date createdAt;
	
	protected LogEntry() {
		
	}

	public LogEntry(String message) {

		this.message = message;
	}

	public String getId() {

		return id;
	}

	public String getMessage() {

		return message;
	}

	public Date getCreatedAt() {

		return createdAt;
	}
}
