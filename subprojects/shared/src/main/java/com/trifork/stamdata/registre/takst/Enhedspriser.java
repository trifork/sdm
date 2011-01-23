package com.trifork.stamdata.registre.takst;

public class Enhedspriser extends TakstRecord {

	private Long drugID; // Ref. t. LMS01
	private Long varenummer; // Ref. t. LMS02
	private Long prisPrEnhed; // Pris = Ekspeditionens samlede pris (ESP)
	private Long prisPrDDD; // Pris = ESP
	private String billigstePakning; // Markering af billigste pakning pr. enhed
										// for DrugID


	public Long getDrugID() {

		return this.drugID;
	}


	public void setDrugID(Long drugID) {

		this.drugID = drugID;
	}


	public Long getVarenummer() {

		return this.varenummer;
	}


	public void setVarenummer(Long varenummer) {

		this.varenummer = varenummer;
	}


	public Long getPrisPrEnhed() {

		return this.prisPrEnhed;
	}


	public void setPrisPrEnhed(Long prisPrEnhed) {

		this.prisPrEnhed = prisPrEnhed;
	}


	public Long getPrisPrDDD() {

		return this.prisPrDDD;
	}


	public void setPrisPrDDD(Long prisPrDDD) {

		this.prisPrDDD = prisPrDDD;
	}


	public String getBilligstePakning() {

		return this.billigstePakning;
	}


	public void setBilligstePakning(String billigstePakning) {

		this.billigstePakning = billigstePakning;
	}


	@Override
	public String getKey() {

		return "" + varenummer;
	}

}