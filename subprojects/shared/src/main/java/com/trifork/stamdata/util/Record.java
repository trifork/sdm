package com.trifork.stamdata.util;

import java.util.Date;


public interface Record {

	void setPID(long pid);


	long getPID();


	Object getKey();


	Date getValidFrom();


	Date getValidTo();


	void setValidFrom(Date validfrom);


	void setValidTo(Date validTo);


	void setModifiedDate(Date modifiedDate);


	Date getModifiedDate();
}
