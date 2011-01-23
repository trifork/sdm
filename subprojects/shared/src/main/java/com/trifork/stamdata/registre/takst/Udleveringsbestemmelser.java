package com.trifork.stamdata.registre.takst;

public class Udleveringsbestemmelser extends TakstRecord {

	private String kode; // Ref. t. LMS02, felt 10
	private String udleveringsgruppe; // Recept: A eller B, håndkøb: H
	private String kortTekst;
	private String tekst;


	public String getKode() {

		return this.kode;
	}


	public void setKode(String kode) {

		this.kode = kode;
	}


	public String getUdleveringsgruppe() {

		return this.udleveringsgruppe;
	}


	public void setUdleveringsgruppe(String udleveringsgruppe) {

		this.udleveringsgruppe = udleveringsgruppe;
	}


	public String getKortTekst() {

		return this.kortTekst;
	}


	public void setKortTekst(String kortTekst) {

		this.kortTekst = kortTekst;
	}


	public String getTekst() {

		return this.tekst;
	}


	public void setTekst(String tekst) {

		this.tekst = tekst;
	}


	@Override
	public String getKey() {

		return "" + this.kode;
	}
}