package com.trifork.stamdata.ssl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubjectSerialNumber {
	public enum Kind {
		MOCES("RID"), VOCES("UID"), FOCES("FID"), POCES("PID");
		
		private final String representation;

		private Kind(String representation) {
			this.representation = representation;
		}
		
		public String getRepresentation() {
			return representation;
		}
		
		static Kind from(String s) {
			for (Kind k : Kind.values()) {
				if (k.representation.equals(s)) {
					return k;
				}
			}
			throw new IllegalArgumentException("Unknown kind: " + s);
		}
	}
	
	private static Pattern ssnPattern = Pattern.compile("CVR:([\\d]{8})-([RFU]ID):(.+)");
	private final Kind kind;
	private final String cvrNumber;
	private final String subjectId;
	
	public SubjectSerialNumber(Kind kind, String cvrNumber, String subjectId) {
		this.kind = kind;
		this.cvrNumber = cvrNumber;
		this.subjectId = subjectId;
	}

	public SubjectSerialNumber(String subjectSerialNumber) {
		Matcher matcher = ssnPattern.matcher(subjectSerialNumber);
		if(!matcher.matches()) {
			throw new IllegalArgumentException("Invalid format for SubjectSerialNumber: " + subjectSerialNumber);
		}
		kind = Kind.from(matcher.group(2));
		cvrNumber = matcher.group(1);
		subjectId = matcher.group(3);
	}

	public Kind getKind() {
		return kind;
	}

	public String getCvrNumber() {
		return cvrNumber;
	}

	public String getSubjectId() {
		return subjectId;
	}
	
	@Override
	public String toString() {
		return "CVR:" + cvrNumber + "-" + kind.getRepresentation() + ":" + subjectId;
	}
}
