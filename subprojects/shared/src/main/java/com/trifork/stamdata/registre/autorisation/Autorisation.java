package com.trifork.stamdata.registre.autorisation;

import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
public class Autorisation extends AbstractRecord {

	private String nummer;
	private String cpr;

	private String fornavn;
	private String efternavn;

	private String uddKode;

	private Date validFrom;


	@Id
	@Column
	@XmlName("nummer")
	@XmlOrder(1)
	public String getAutorisationsnummer() {

		return nummer;
	}


	@Column
	@XmlName("cpr")
	@XmlOrder(2)
	public String getCpr() {

		return cpr;
	}


	@Column
	@XmlOrder(3)
	public String getEfternavn() {

		return efternavn;
	}


	@Column
	@XmlOrder(4)
	public String getFornavn() {

		return fornavn;
	}


	@Column
	@XmlOrder(5)
	public String getUddannelsesKode() {

		return uddKode;
	}


	@Override
	public Date getValidFrom() {

		return validFrom;
	}


	@Override
	public Date getValidTo() {

		return DateUtils.FOREVER;
	}


	public void setNummer(String nummer) {

		this.nummer = nummer;
	}


	public void setCpr(String cpr) {

		this.cpr = cpr;
	}


	public void setFornavn(String fornavn) {

		this.fornavn = fornavn;
	}


	public void setEfternavn(String efternavn) {

		this.efternavn = efternavn;
	}


	public void setUddKode(String uddKode) {

		this.uddKode = uddKode;
	}


	@Override
	public void setValidFrom(Date validFrom) {

		this.validFrom = validFrom;
	}
}
