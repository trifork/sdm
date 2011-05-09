package com.trifork.stamdata;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SubjectSerialNumberTest {

	@Test(expected = NullPointerException.class)
	public void should_throw_exception_on_null_argument() {

		new SubjectSerialNumber(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_exception_if_uid_is_malformed() {

		new SubjectSerialNumber("CVR:12345678-UID:AAA123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_throw_exception_if_cvr_is_malformed() {

		new SubjectSerialNumber("CVR:1234567-UID:1234");
	}

	@Test
	public void should_parse_uid_correctly() {

		SubjectSerialNumber ssn = new SubjectSerialNumber("CVR:12345678-UID:1234");

		assertThat(ssn.getUID(), equalTo("1234"));
	}

	@Test
	public void should_parse_cvr_correctly() {

		SubjectSerialNumber ssn = new SubjectSerialNumber("CVR:12345678-UID:1234");

		assertThat(ssn.getCVR(), equalTo("12345678"));
	}
}
