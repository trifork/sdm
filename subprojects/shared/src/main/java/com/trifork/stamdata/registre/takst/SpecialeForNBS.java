package com.trifork.stamdata.registre.takst;

public class SpecialeForNBS extends TakstRecord {

	private String kode; // Ref. t. LMS02, felt 11
	private String kortTekst; // Speciale, forkortet
	private String tekst; // Tekst for speciale


	public String getKode() {

		return this.kode;
	}


	public void setKode(String kode) {

		this.kode = kode;
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