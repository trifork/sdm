package com.trifork.stamdata;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.util.StringTokenizer;


/**
 * A helper class for VOCES subject serial numbers.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public class SubjectSerialNumber {

	/**
	 * A UID can at most be 47 char long, a cvr is always 8.
	 */
	private static final String CVR_RID = "CVR:[0-9]{8}-UID:[0-9]{1,47}";

	private final String subjectSerialNumber;
	private final String cvr;
	private final String uid;

	public SubjectSerialNumber(String subjectSerialNumber) throws IllegalArgumentException {

		checkNotNull(subjectSerialNumber);
		checkArgument(subjectSerialNumber.matches(CVR_RID));
		this.subjectSerialNumber = subjectSerialNumber;

		StringTokenizer tokenizer = new StringTokenizer(subjectSerialNumber, ":-");

		tokenizer.nextToken(); // CVR Label
		this.cvr = tokenizer.nextToken(); // CVR Number
		tokenizer.nextToken(); // UID Label
		this.uid = tokenizer.nextToken(); // UID Number
	}

	public String getUID() {

		return uid;
	}

	public String getCVR() {

		return cvr;
	}

	@Override
	public String toString() {

		return subjectSerialNumber;
	}
}
