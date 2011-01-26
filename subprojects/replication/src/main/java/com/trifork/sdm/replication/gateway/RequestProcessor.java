package com.trifork.sdm.replication.gateway;


public interface RequestProcessor
{
	void process(String request, String clientCVR, String httpMethod);


	String getResponse();


	int getResponseCode();


	String getContentType();
}
