package dk.trifork.sdm.importer.dosagesuggestions.models;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output
public class DosageVersion extends AbstractStamdataEntity {

	// daDate: Dato for Apotekerforeningens mærkevaretakst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date daDate;

	// lmsDate: Dato for Lægemiddelstyrelsens takst, som datasættet er
	// udarbejdet på baggrund af. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date lmsDate;

	// Dato filen er released. Obligatorisk. Dato, yyyy-MM-dd.
	protected Date releaseDate;

	// Unikt release nummer. Obligatorisk. Heltal, 15 cifre.
	protected long releaseNumber;

	protected DosageVersion() {

	}

	@Output
	public Date getDaDate() {

		return daDate;
	}

	@Output
	public Date getLmsDate() {

		return lmsDate;
	}

	@Id
	@Output
	public Date getReleaseDate() {

		return releaseDate;
	}

	@Output
	public long getReleaseNumber() {

		return releaseNumber;
	}

	public void setDaDate(Date daDate) {

		this.daDate = daDate;
	}

	public void setLmsDate(Date lmsDate) {

		this.lmsDate = lmsDate;
	}

	public void setReleaseDate(Date releaseDate) {

		this.releaseDate = releaseDate;
	}

	public void setReleaseNumber(long releaseNumber) {

		this.releaseNumber = releaseNumber;
	}

	@Override
	public Calendar getValidFrom() {

		Calendar c = Calendar.getInstance();
		c.setTime(releaseDate);
		return c;
	}
}
