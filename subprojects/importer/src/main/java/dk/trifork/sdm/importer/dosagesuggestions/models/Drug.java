package dk.trifork.sdm.importer.dosagesuggestions.models;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output(name = "DosageDrug")
public class Drug extends AbstractStamdataEntity {

	private long drugId;
	private int releaseNumber;
	private String drugName;
	private int dosageUnitCode;

	private Date validFrom;

	protected Drug() {

	}

	@Id
	@Output
	public long getDrugId() {

		return drugId;
	}

	@Output
	public int getReleaseNumber() {

		return releaseNumber;
	}

	@Output
	public String getDrugName() {

		return drugName;
	}

	@Output
	public int getDosageUnitCode() {

		return dosageUnitCode;
	}

	@Override
	public Calendar getValidFrom() {

		Calendar c = Calendar.getInstance();
		c.setTime(validFrom);
		return c;
	}
}
