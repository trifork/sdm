package com.trifork.stamdata.lookup.validation;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import oio.sagdok.person._1_0.PersonType;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class PersonValidatorTest {

	@Test
	@Ignore
	public void performsValidation() throws SAXException, JAXBException, IOException, Exception {
		new PersonValidator().validate(new oio.sagdok.person._1_0.ObjectFactory().createPerson(new PersonType()), new ErrorHandler() {

			@Override
			public void warning(SAXParseException exception) throws SAXException {
				throw exception;

			}

			@Override
			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;

			}

			@Override
			public void error(SAXParseException exception) throws SAXException {
				throw exception;

			}
		});
	}
}
