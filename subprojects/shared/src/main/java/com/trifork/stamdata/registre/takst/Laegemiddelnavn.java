package com.trifork.stamdata.registre.takst;

public class Laegemiddelnavn extends TakstRecord {

	private Long drugid; // Ref. t. LMS01, felt 01
	private String laegemidletsUforkortedeNavn;


	public Long getDrugid() {

		return this.drugid;
	}


	public void setDrugid(Long drugid) {

		this.drugid = drugid;
	}


	public String getLaegemidletsUforkortedeNavn() {

		return this.laegemidletsUforkortedeNavn;
	}


	public void setLaegemidletsUforkortedeNavn(String laegemidletsUforkortedeNavn) {

		this.laegemidletsUforkortedeNavn = laegemidletsUforkortedeNavn;
	}


	@Override
	public String getKey() {

		return "" + this.drugid;
	}

}