package com.trifork.stamdata.registre.cpr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class BarnRelation extends CPRRecord {
	String barnCpr;


	@Id
	@Column
	public String getId() {

		return getCpr() + "-" + barnCpr;
	}


	@Column
	@Override
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	public String getBarnCpr() {

		return barnCpr;
	}


	public void setBarnCpr(String barnCpr) {

		this.barnCpr = barnCpr;
	}
}
