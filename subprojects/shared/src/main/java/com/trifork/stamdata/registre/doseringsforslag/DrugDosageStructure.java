package com.trifork.stamdata.registre.doseringsforslag;

import java.util.Date;

import javax.persistence.*;

import com.trifork.stamdata.*;


@Entity
@Documented("Referencetabel der knytter doseringsstrukturer i dosageStructures til lægemidler.")
public class DrugDosageStructure extends AbstractRecord {

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Lægemidlets drug id. Reference til drugId i drugs. Obligatorisk. Heltal,
	// 11 cifre.
	private long drugId;

	// Reference til code i dosageStructure. Obligatorisk. Heltal, 11 cifre.
	private long dosageStructureCode;

	private Date validFrom;


	public void setReleaseNumber(long releaseNumber) {

		this.releaseNumber = releaseNumber;
	}


	@Id
	@Column(length = 22)
	@XmlOrder(1)
	public String getId() {

		return Long.toString(drugId) + Long.toString(dosageStructureCode);
	}


	// Don't output this.
	// @Column(length=15)
	public long getReleaseNumber() {

		return releaseNumber;
	}


	public void setDrugId(long drugId) {

		this.drugId = drugId;
	}


	@Column(length = 11)
	@XmlOrder(2)
	public long getDrugId() {

		return drugId;
	}


	public void setDosageStructureCode(long dosageStructureCode) {

		this.dosageStructureCode = dosageStructureCode;
	}


	@Column(length = 11)
	@XmlOrder(3)
	public long getDosageStructureCode() {

		return dosageStructureCode;
	}


	@Override
	public Date getValidFrom() {

		return validFrom;
	}
}
