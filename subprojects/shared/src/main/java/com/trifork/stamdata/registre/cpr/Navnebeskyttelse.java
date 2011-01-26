package com.trifork.stamdata.registre.cpr;

import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


@Entity
@Table(name = "Person")
public class Navnebeskyttelse extends CPRRecord {

	Date navneBeskyttelseStartDato;
	Date navneBeskyttelseSletteDato;


	@Override
	@Id
	@Column
	@XmlOrder(1)
	public String getCpr() {

		return super.getCpr();
	}


	@Column
	@XmlOrder(2)
	@XmlName("startdato")
	public Date getNavneBeskyttelseStartDato() {

		return navneBeskyttelseStartDato;
	}


	public void setNavneBeskyttelseStartDato(Date startDato) {

		this.navneBeskyttelseStartDato = startDato;
	}


	@Column
	@XmlOrder(3)
	@XmlName("slutdato")
	public Date getNavneBeskyttelseSletteDato() {

		return navneBeskyttelseSletteDato;
	}


	public void setNavneBeskyttelseSletteDato(Date sletteDato) {

		this.navneBeskyttelseSletteDato = sletteDato;
	}
}
