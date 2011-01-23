package com.trifork.stamdata.registre.takst;

import java.text.NumberFormat;
import java.util.Locale;

import javax.persistence.Column;


public class Priser extends TakstRecord {

	private Long varenummer; // Ref. t. LMS02, felt 02
	private Long aIP; // Apotekets indkøbspris
	private Long registerpris; // Beregning i prisbekendtg., § 2, stk. 1, 9 og
								// 10
	private Long ekspeditionensSamlPrisESP; // Reg.pris + evt. receptur- og
											// færdigtilb.gebyr
	private Long tilskudsprisTSP; // Tilskudspris (human) eller 000000000
									// (veterinær)
	private Long leveranceprisTilHospitaler; // Beregning i
												// prisbekendtgørelsen, § 2,
												// stk. 3, 4, 5, 9 og 10
	private Long ikkeTilskudsberettigetDel; // Fx utensilie eller del af
											// kombinationspakn.

	private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("da", "DK"));


	public Long getVarenummer() {

		return this.varenummer;
	}


	public void setVarenummer(Long varenummer) {

		this.varenummer = varenummer;
	}


	@Column(name = "apoteketsIndkoebspris")
	public NumeriskMedEnhed getAIP() {

		return new NumeriskMedEnhed(takst, nf.format(this.aIP / 100.0), this.aIP / 100.0, "DKK");
	}


	public void setAIP(Long aIP) {

		this.aIP = aIP;
	}


	@Column
	public NumeriskMedEnhed getRegisterpris() {

		return new NumeriskMedEnhed(takst, nf.format(this.registerpris / 100.0), this.registerpris / 100.0,
				"DKK");
	}


	public void setRegisterpris(Long registerpris) {

		this.registerpris = registerpris;
	}


	@Column(name = "ekspeditionensSamledePris")
	public NumeriskMedEnhed getEkspeditionensSamlPrisESP() {

		return new NumeriskMedEnhed(takst, nf.format(this.ekspeditionensSamlPrisESP / 100.0),
				this.ekspeditionensSamlPrisESP / 100.0, "DKK");
	}


	public void setEkspeditionensSamlPrisESP(Long ekspeditionensSamlPrisESP) {

		this.ekspeditionensSamlPrisESP = ekspeditionensSamlPrisESP;
	}


	@Column(name = "tilskudspris")
	public NumeriskMedEnhed getTilskudsprisTSP() {

		return new NumeriskMedEnhed(takst, nf.format(this.tilskudsprisTSP / 100.0),
				this.tilskudsprisTSP / 100.0, "DKK");
	}


	public void setTilskudsprisTSP(Long tilskudsprisTSP) {

		this.tilskudsprisTSP = tilskudsprisTSP;
	}


	@Column
	public NumeriskMedEnhed getLeveranceprisTilHospitaler() {

		return new NumeriskMedEnhed(takst, nf.format(this.leveranceprisTilHospitaler / 100.0),
				this.leveranceprisTilHospitaler / 100.0, "DKK");
	}


	public void setLeveranceprisTilHospitaler(Long leveranceprisTilHospitaler) {

		this.leveranceprisTilHospitaler = leveranceprisTilHospitaler;
	}


	@Column
	public NumeriskMedEnhed getIkkeTilskudsberettigetDel() {

		return new NumeriskMedEnhed(takst, nf.format(this.ikkeTilskudsberettigetDel / 100.0),
				this.ikkeTilskudsberettigetDel / 100.0, "DKK");
	}


	public void setIkkeTilskudsberettigetDel(Long ikkeTilskudsberettigetDel) {

		this.ikkeTilskudsberettigetDel = ikkeTilskudsberettigetDel;
	}


	@Override
	public Long getKey() {

		return this.varenummer;
	}


	public Takst getTakst() {

		return takst;
	}


	public void setTakst(Takst takst) {

		this.takst = takst;
	}

}