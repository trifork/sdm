package dk.trifork.sdm.importer.dosagesuggestions.models;

import java.util.Calendar;

import dk.trifork.sdm.model.AbstractStamdataEntity;


public abstract class DosageRecord extends AbstractStamdataEntity {

	private Calendar validFrom;

	public void setVersion(Calendar validFrom) {

		this.validFrom = validFrom;
	}

	@Override
	public Calendar getValidFrom() {

		return validFrom;
	}
}
