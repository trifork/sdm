package com.trifork.stamdata.client;

/**
 * An exception caused by an unexpected error streaming.
 * 
 * @author Thomas Børlum (thb@trifork.com)
 */
public class RecordStreamException extends RuntimeException {

	private static final long serialVersionUID = 4814805381950142545L;

	public RecordStreamException(Exception e) {

		super(e);
	}
}
