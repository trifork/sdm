package com.trifork.stamdata.importer.cpr.model;

import java.util.Calendar;

import com.trifork.stamdata.model.Id;
import com.trifork.stamdata.model.Output;

@Output
public class Beskyttelse extends CPREntity {

	Calendar startDato;
	Calendar sletteDato;
	String beskyttelsestype;

	@Id
	@Output
	public String getId() {
		return getCpr() + "-" + beskyttelsestype;
	}

	@Output
	@Override
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getBeskyttelsestype() {
		return beskyttelsestype;
	}

	public void setBeskyttelsestype(String beskyttelsestype) {
		this.beskyttelsestype = beskyttelsestype;
	}

	@Override
	public Calendar getValidTo() {
		return sletteDato;
	}

	@Override
	public Calendar getValidFrom() {
		return startDato;
	}
	
	public void setValidFrom(Calendar validFrom) {
		this.startDato = validFrom;
	}
	public void setValidTo(Calendar validTo) {
		this.sletteDato = validTo;
	}
}
