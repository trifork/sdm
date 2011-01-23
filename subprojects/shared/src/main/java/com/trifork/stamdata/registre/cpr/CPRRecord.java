package com.trifork.stamdata.registre.cpr;

import com.trifork.stamdata.util.AbstractRecord;


public abstract class CPRRecord extends AbstractRecord {

	private String cpr;


	public String getCpr() {

		return cpr;
	}


	public void setCpr(String cpr) {

		this.cpr = cpr;
	}
}
