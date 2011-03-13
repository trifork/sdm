package com.trifork.sdm.replication.util;


public class Authorization {

	private static final String SEPARATOR = ":";

	private final String cvrNumber;
	private final long expirationDate;
	private final String signature;


	public Authorization(String cvrNumber, long expirationDate, String signature) {

		this.cvrNumber = cvrNumber;
		this.expirationDate = expirationDate;
		this.signature = signature;
	}


	public static Authorization parse(String headerString) {

		if (headerString == null || !headerString.startsWith("STAMDATA ")) {
			return null;
		}

		try {
			String[] parts = headerString.split(SEPARATOR);

			String cvrNumber = parts[0];
			long expirationDate = Long.parseLong(parts[1]) * 1000;
			String signature = parts[2];

			return new Authorization(cvrNumber, expirationDate, signature);
		}
		catch (Exception e) {

			return null;
		}
	}


	public String getCvrNumber() {
		return cvrNumber;
	}


	public long getExpirationDate() {
		return expirationDate;
	}


	public String getSignature() {
		return signature;
	}
}
