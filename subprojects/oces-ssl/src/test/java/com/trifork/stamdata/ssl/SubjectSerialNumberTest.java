package com.trifork.stamdata.ssl;

import static org.junit.Assert.*;

import org.junit.Test;

import com.trifork.stamdata.ssl.SubjectSerialNumber.Kind;

public class SubjectSerialNumberTest {
	@Test
	public void canParseMocesSubjectSerialNumber() {
		SubjectSerialNumber ssn = new SubjectSerialNumber("CVR:12345678-RID:1203980293");
		assertEquals(Kind.MOCES, ssn.getKind());
		assertEquals("12345678", ssn.getCvrNumber());
		assertEquals("1203980293", ssn.getSubjectId());
	}

	@Test
	public void canParseVocesSubjectSerialNumber() {
		SubjectSerialNumber ssn = new SubjectSerialNumber("CVR:12345678-UID:1203980293");
		assertEquals(Kind.VOCES, ssn.getKind());
		assertEquals("12345678", ssn.getCvrNumber());
		assertEquals("1203980293", ssn.getSubjectId());
	}

	@Test
	public void canParseFocesSubjectSerialNumber() {
		SubjectSerialNumber ssn = new SubjectSerialNumber("CVR:12345678-FID:1203980293");
		assertEquals(Kind.FOCES, ssn.getKind());
		assertEquals("12345678", ssn.getCvrNumber());
		assertEquals("1203980293", ssn.getSubjectId());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void rejectsIllegalSubjectSerialNumber() {
		new SubjectSerialNumber("Hej med dig");
	}
	
	@Test
	public void givesOriginalSubjectSerialNumberAsStringRepresentation() {
		SubjectSerialNumber ssn = new SubjectSerialNumber(Kind.MOCES, "23456789", "My rid");
		assertEquals("CVR:23456789-RID:My rid", ssn.toString());
	}
}
