package dk.trifork.sdm.importer.cpr.model;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Statsborgerskab extends CPREntity {
	private String landekode;
	private Calendar validFrom;
	private String statsborgerskabstartdatousikkerhedsmarkering;
	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getLandekode() {
		return landekode;
	}
	public void setLandekode(String landekode) {
		this.landekode = landekode;
	}

	@Output
	public String getStatsborgerskabstartdatousikkerhedsmarkering() {
		return statsborgerskabstartdatousikkerhedsmarkering;
	}
	public void setStatsborgerskabstartdatousikkerhedsmarkering(
			String statsborgerskabstartdatousikkerhedsmarkering) {
		this.statsborgerskabstartdatousikkerhedsmarkering = statsborgerskabstartdatousikkerhedsmarkering;
	}

	@Override
	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

}
