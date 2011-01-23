package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "IndikationATCRef")
public class Indikationskode extends TakstRecord {

	private String aTC; // Ref. t. LMS01
	private Long indikationskode; // Ref. t. LMS26
	private Long drugID; // Ref. t. LMS01, felt 01


	@Id
	@Column
	public String getCID() {

		// TODO: Get rid of this ugly calculated ID. Should be handled by the
		// DAO
		// A calculated ID. Necessary because the DAO implementation needs a
		// single key
		return aTC + "-" + indikationskode;
	}


	@Column
	public String getATC() {

		return this.aTC;
	}


	public void setATC(String aTC) {

		this.aTC = aTC;
	}


	@Column(name = "IndikationKode")
	public Long getIndikationskode() {

		return this.indikationskode;
	}


	public void setIndikationskode(Long indikationskode) {

		this.indikationskode = indikationskode;
	}


	public Long getDrugID() {

		return this.drugID;
	}


	public void setDrugID(Long drugID) {

		this.drugID = drugID;
	}

}