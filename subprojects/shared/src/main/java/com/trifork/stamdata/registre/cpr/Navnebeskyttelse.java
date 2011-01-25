package com.trifork.stamdata.registre.cpr;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.trifork.stamdata.XmlName;


@Entity
@Table(name = "Person")
public class Navnebeskyttelse extends CPRRecord {

	Date navneBeskyttelseStartDato;
	Date navneBeskyttelseSletteDato;


	@Override
	@Id
	@Column
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	@XmlName("startdato")
	public Date getNavneBeskyttelseStartDato() {

		return navneBeskyttelseStartDato;
	}


	public void setNavneBeskyttelseStartDato(Date startDato) {

		this.navneBeskyttelseStartDato = startDato;
	}


	@Column
	@XmlName("slutdato")
	public Date getNavneBeskyttelseSletteDato() {

		return navneBeskyttelseSletteDato;
	}


	public void setNavneBeskyttelseSletteDato(Date sletteDato) {

		this.navneBeskyttelseSletteDato = sletteDato;
	}
}
