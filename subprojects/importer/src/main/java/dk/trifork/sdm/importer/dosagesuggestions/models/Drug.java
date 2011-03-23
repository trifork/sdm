package dk.trifork.sdm.importer.dosagesuggestions.models;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output(name = "DosageDrug")
public class Drug extends DosageRecord {

	private long drugId;
	private int releaseNumber;
	private String drugName;
	private int dosageUnitCode;

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
}
