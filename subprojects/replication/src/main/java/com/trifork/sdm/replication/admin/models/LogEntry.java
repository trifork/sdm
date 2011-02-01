package com.trifork.sdm.replication.admin.models;


import java.util.Date;


public class LogEntry
{
	private final String message;
	private final Date createdAt;
	private final String id;


	public LogEntry(String id, String message, Date createdAt)
	{
		this.id = id;
		this.message = message;
		this.createdAt = createdAt;
	}


	public String getId()
	{
		return id;
	}


	public String getMessage()
	{
		return message;
	}


	public Date getCreatedAt()
	{
		return createdAt;
	}
}
