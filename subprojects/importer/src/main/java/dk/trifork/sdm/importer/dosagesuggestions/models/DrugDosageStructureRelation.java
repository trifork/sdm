package dk.trifork.sdm.importer.dosagesuggestions.models;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;


@Output
public class DrugDosageStructureRelation extends DosageRecord {

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	private long releaseNumber;

	// Lægemidlets drug id. Reference til drugId i drugs. Obligatorisk. Heltal,
	// 11 cifre.
	private long drugId;

	// Reference til code i dosageStructure. Obligatorisk. Heltal, 11 cifre.
	private long dosageStructureCode;

	public void setReleaseNumber(long releaseNumber) {

		this.releaseNumber = releaseNumber;
	}

	@Id
	@Output
	public String getId() {

		return Long.toString(drugId) + Long.toString(dosageStructureCode);
	}

	@Output
	public long getReleaseNumber() {

		return releaseNumber;
	}

	public void setDrugId(long drugId) {

		this.drugId = drugId;
	}

	@Output
	public long getDrugId() {

		return drugId;
	}

	public void setDosageStructureCode(long dosageStructureCode) {

		this.dosageStructureCode = dosageStructureCode;
	}

	@Output
	public long getDosageStructureCode() {

		return dosageStructureCode;
	}
}
