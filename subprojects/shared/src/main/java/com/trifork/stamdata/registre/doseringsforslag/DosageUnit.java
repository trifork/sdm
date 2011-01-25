package com.trifork.stamdata.registre.doseringsforslag;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.AbstractRecord;
import com.trifork.stamdata.Documented;


@Entity
@Documented("Indeholder anvendte doseringsenheder.\n"
		+ "Doseringsenhederne stammer dels fra Lægemiddelstyrelsens takst (her er code <= 1000),\n"
		+ "dels er der tale om nye data (code > 1000).")
public class DosageUnit extends AbstractRecord {

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Unik kode for doseringsenheden. Obligatorisk. Heltal, 4 cifre.
	private int code;

	// Doseringenhedens tekst i ental. Obligatorisk. Streng, 100 tegn.
	private String textSingular;

	// Doseringsenhedens tekst i flertal. Obligatorisk. Streng, 100 tegn.
	private String textPlural;

	private Date validFrom;


	public void setReleaseNumber(long releaseNumber) {

		this.releaseNumber = releaseNumber;
	}


	// Don't output this.
	// @Column(length = 15)
	public long getReleaseNumber() {

		return releaseNumber;
	}


	public void setCode(int code) {

		this.code = code;
	}


	@Id
	@Column(length = 4)
	public int getCode() {

		return code;
	}


	public void setTextSingular(String textSingular) {

		this.textSingular = textSingular;
	}


	@Column(length = 100)
	public String getTextSingular() {

		return textSingular;
	}


	public void setTextPlural(String textPlural) {

		this.textPlural = textPlural;
	}


	@Column(length = 100)
	public String getTextPlural() {

		return textPlural;
	}


	@Override
	public Date getValidFrom() {

		return validFrom;
	}
}
