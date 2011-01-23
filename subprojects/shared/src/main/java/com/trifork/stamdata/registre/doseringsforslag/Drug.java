package com.trifork.stamdata.registre.doseringsforslag;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.util.AbstractRecord;
import com.trifork.stamdata.util.Documented;


@Entity
@Documented("Indeholder information om lægemidlers drug-id og doseringsenhed.")
public class Drug extends AbstractRecord {

	private Date validFrom;
	private int releaseNumber;
	private long drugId;
	private int dosageUnitCode;


	// Don't output this.
	// @Column(length=15)
	public int getReleaseNumber() {

		return releaseNumber;
	}


	public void setReleaseNumber(int releaseNumber) {

		this.releaseNumber = releaseNumber;
	}


	@Id
	@Column(length = 11)
	public long getDrugId() {

		return drugId;
	}


	public void setDrugId(long drugId) {

		this.drugId = drugId;
	}


	@Column(length = 4)
	public int getDosageUnitCode() {

		return dosageUnitCode;
	}


	public void setDosageUnitCode(int dosageUnitCode) {

		this.dosageUnitCode = dosageUnitCode;
	}


	@Override
	public void setValidFrom(Date validfrom) {

		this.validFrom = validfrom;
	}


	@Override
	public Date getValidFrom() {

		return validFrom;
	}
}
