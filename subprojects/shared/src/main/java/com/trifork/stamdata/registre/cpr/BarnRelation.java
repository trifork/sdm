package com.trifork.stamdata.registre.cpr;

import javax.persistence.*;

import com.trifork.stamdata.XmlOrder;


@Entity
public class BarnRelation extends CPRRecord {
	String barnCpr;


	@Id
	@Column
	@XmlOrder(1)
	public String getId() {

		return getCpr() + "-" + barnCpr;
	}


	@Column
	@Override
	@XmlOrder(2)
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	@XmlOrder(3)
	public String getBarnCpr() {

		return barnCpr;
	}


	public void setBarnCpr(String barnCpr) {

		this.barnCpr = barnCpr;
	}
}
