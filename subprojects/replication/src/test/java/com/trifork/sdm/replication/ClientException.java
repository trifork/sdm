package com.trifork.sdm.replication;

public class ClientException extends Exception {
	
	private static final long serialVersionUID = 8199686556697009371L;
	
	private final String faultCode;
	private final String faultString;


	public ClientException(String message, String faultCode, String faultString) {

		super(message);
		this.faultCode = faultCode;
		this.faultString = faultString;
	}


	public String getFaultCode() {

		return faultCode;
	}


	public String getFaultString() {

		return faultString;
	}
}
