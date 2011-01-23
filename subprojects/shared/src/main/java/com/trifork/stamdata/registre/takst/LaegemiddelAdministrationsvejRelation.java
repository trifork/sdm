package com.trifork.stamdata.registre.takst;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class LaegemiddelAdministrationsvejRelation extends TakstRecord {
	private long drugId;
	private String AdministrationsvejKode;


	public LaegemiddelAdministrationsvejRelation(Laegemiddel lm, Administrationsvej av) {

		this.drugId = lm.getDrugid();
		this.AdministrationsvejKode = av.getKode();
	}


	@Id
	@Column
	public String getCID() {

		return drugId + "-" + AdministrationsvejKode;
	}


	@Column
	public long getDrugId() {

		return drugId;
	}


	public void setDrugId(long drugId) {

		this.drugId = drugId;
	}


	@Column
	public String getAdministrationsvejKode() {

		return AdministrationsvejKode;
	}


	public void setAdministrationsvejKode(String administrationsvejKode) {

		AdministrationsvejKode = administrationsvejKode;
	}

}

/*
 * 
 * DrugID BIGINT(12) NOT NULL, AdministrationsvejKode CHAR(2) NOT NULL,
 * ModifiedBy VARCHAR(200) NOT NULL, ModifiedDate DATETIME NOT NULL, ValidFrom
 * DATETIME, ValidTo DATETIME, CreatedBy VARCHAR(200) NOT NULL, CreatedDate
 * DATETIME NOT NULL, INDEX (ValidFrom, ValidTo, DrugID, DoseringKode) )
 * ENGINE=InnoDB
 */