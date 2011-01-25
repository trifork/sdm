package com.trifork.stamdata.registre.cpr;


import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.XmlName;


@Entity
@Table(name = "Person")
public class Navneoplysninger extends CPRRecord
{
	String fornavn;
	String fornavnMarkering;
	String mellemnavn;
	String mellemnavnMarkering;
	String efternavn;
	String efternavnMarkering;
	Date startDato;
	String startDatoMarkering;
	String adresseringsNavn;


	@Override
	@Id
	@Column
	public String getCpr()
	{
		return super.getCpr();
	}


	@Column
	public String getFornavn()
	{
		return fornavn;
	}


	public void setFornavn(String fornavn)
	{
		this.fornavn = fornavn;
	}


	public String getFornavnMarkering()
	{
		return fornavnMarkering;
	}


	public void setFornavnMarkering(String fornavnMarkering)
	{
		this.fornavnMarkering = fornavnMarkering;
	}


	@Column
	public String getMellemnavn()
	{
		return mellemnavn;
	}


	public void setMellemnavn(String mellemnavn)
	{
		this.mellemnavn = mellemnavn;
	}


	public String getMellemnavnMarkering()
	{
		return mellemnavnMarkering;
	}


	public void setMellemnavnMarkering(String mellemnavnMarkering)
	{
		this.mellemnavnMarkering = mellemnavnMarkering;
	}


	@Column
	public String getEfternavn()
	{
		return efternavn;
	}


	public void setEfternavn(String efternavn)
	{
		this.efternavn = efternavn;
	}


	public String getEfternavnMarkering()
	{
		return efternavnMarkering;
	}


	public void setEfternavnMarkering(String efternavnMarkering)
	{
		this.efternavnMarkering = efternavnMarkering;
	}


	public Date getStartDato()
	{
		return startDato;
	}


	public void setStartDato(Date startDato)
	{
		this.startDato = startDato;
	}


	public String getStartDatoMarkering()
	{
		return startDatoMarkering;
	}


	public void setStartDatoMarkering(String startDatoMarkering)
	{
		this.startDatoMarkering = startDatoMarkering;
	}


	public String getAdresseringsNavn()
	{
		return adresseringsNavn;
	}


	public void setAdresseringsNavn(String adresseringsNavn)
	{
		this.adresseringsNavn = adresseringsNavn;
	}
}
