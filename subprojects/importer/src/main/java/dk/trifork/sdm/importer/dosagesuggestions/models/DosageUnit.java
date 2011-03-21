package dk.trifork.sdm.importer.dosagesuggestions.models;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output
public class DosageUnit extends AbstractStamdataEntity {

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

	@Id
	@Output
	public int getCode() {

		return code;
	}

	@Output
	public long getReleaseNumber() {

		return releaseNumber;
	}

	public void setCode(int code) {

		this.code = code;
	}

	public void setTextSingular(String textSingular) {

		this.textSingular = textSingular;
	}

	@Output
	public String getTextSingular() {

		return textSingular;
	}

	public void setTextPlural(String textPlural) {

		this.textPlural = textPlural;
	}

	@Output
	public String getTextPlural() {

		return textPlural;
	}

	@Override
	public Calendar getValidFrom() {

		Calendar c = Calendar.getInstance();
		c.setTime(validFrom);
		return c;
	}
}
