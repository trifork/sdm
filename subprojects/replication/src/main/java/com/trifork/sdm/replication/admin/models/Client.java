package com.trifork.sdm.replication.admin.models;

public class Client
{
	private final String id;
	private final String name;
	private final String certificateId;


	public Client(String id, String name, String certificateId)
	{
		this.id = id;
		this.name = name;
		this.certificateId = certificateId;
	}


	public String getName()
	{
		return name;
	}


	public String getId()
	{
		return id;
	}


	public String getCertificateId()
	{
		return certificateId;
	}
}
