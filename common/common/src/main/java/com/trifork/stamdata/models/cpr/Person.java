package com.trifork.stamdata.models.cpr;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;

import com.trifork.stamdata.models.BaseTemporalEntity;

// Use the split of different parts that the person table is made up of instead.
// The person table is unstable and its behavior is not well-defined.
@Entity
public class Person extends BaseTemporalEntity
{
	protected Person()
	{
	}

	private String cpr;
	
	@Id
	@Column
	public String getCPR()
	{
		return cpr;
	}
	
	public void setCPR(String cpr)
	{
		this.cpr = cpr;
	}

	public String koen;

	public String fornavn;

	public String mellemnavn;

	public String efternavn;

	public String coNavn;

	public String lokalitet;

	public String vejnavn;

	public String bygningsnummer;

	public String husnummer;

	public String etage;

	public String sideDoerNummer;

	public String bynavn;

	public String postnummer;

	public String postdistrikt;

	public String status;

	public String gaeldendeCPR;

	@Temporal(DATE)
	public Date foedselsdato;

	public String stilling;

	public String vejKode;

	public String kommuneKode;

	@Temporal(TIMESTAMP)
	public Date navnebeskyttelseslettedato;

	@Temporal(TIMESTAMP)
	public Date navnebeskyttelsestartdato;
}
