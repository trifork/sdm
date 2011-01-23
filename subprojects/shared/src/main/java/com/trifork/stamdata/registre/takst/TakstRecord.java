package com.trifork.stamdata.registre.takst;

import java.util.Date;

import com.trifork.stamdata.util.AbstractRecord;


public abstract class TakstRecord extends AbstractRecord {

	protected Takst takst;


	@Override
	public Date getValidFrom() {

		return takst.getValidFrom();
	}
}
