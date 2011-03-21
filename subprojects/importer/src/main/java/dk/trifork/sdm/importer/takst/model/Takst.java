package dk.trifork.sdm.importer.takst.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.Dataset;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;


/**
 * A version of the Takst
 * 
 * @author Rune
 */
@Output(name = "TakstVersion")
public class Takst extends TakstEntity {

	private final List<CompleteDataset<? extends StamdataEntity>> datasets = new ArrayList<CompleteDataset<? extends StamdataEntity>>();

	// The week-number for which LMS guarantees some sort of stability/validity
	// for a subset of this takst. (The stable subset excludes pricing and
	// substitions and possibly more)
	private int validityYear, validityWeekNumber;

	private Calendar validFrom, validTo;

	public Takst(Calendar validFrom, Calendar validTo) {

		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	/**
	 * @param clazz the Class that the returned entities should have
	 * @return All entities of the given type in this takst.
	 */
	public <A extends TakstEntity> TakstDataset<A> getDatasetOfType(Class<A> clazz) {

		for (Dataset<? extends StamdataEntity> dataset : datasets) {
			if (clazz.equals(dataset.getType())) return (TakstDataset<A>) dataset;
		}
		return null;
	}

	public List<CompleteDataset<? extends StamdataEntity>> getDatasets() {

		return datasets;
	}

	public List<StamdataEntity> getEntities() {

		List<StamdataEntity> result = new ArrayList<StamdataEntity>();
		for (Dataset dataset : datasets) {
			result.addAll(dataset.getEntities());
		}
		return result;
	}

	public void addDataset(TakstDataset dataset) {

		datasets.add(dataset);
	}

	/**
	 * @param type, the type of the requested entity
	 * @param entityId the id of the requested entity
	 * @return the requested entity
	 */
	public <T extends TakstEntity> T getEntity(Class<T> type, Object entityId) {

		if (entityId == null) return null;
		Dataset avds = getDatasetOfType(type);
		if (avds == null) return null;
		return (T) avds.getEntityById(entityId);
	}

	@Output(name = "TakstUge")
	public String getStableWeek() {

		return "" + validityYear + validityWeekNumber;
	}

	public void setValidityYear(int validityYear) {

		this.validityYear = validityYear;
	}

	public void setValidityWeekNumber(int validityWeekNumber) {

		this.validityWeekNumber = validityWeekNumber;
	}

	@Id
	public Calendar getValidFrom() {

		return validFrom;
	}

	public Calendar getValidTo() {

		return validTo;
	}
}
