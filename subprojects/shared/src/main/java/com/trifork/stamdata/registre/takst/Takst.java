package com.trifork.stamdata.registre.takst;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.trifork.stamdata.persistence.CompleteDataset;
import com.trifork.stamdata.persistence.Dataset;
import com.trifork.stamdata.util.Record;


@Entity
@Table(name = "TakstVersion")
public class Takst extends TakstRecord {

	private final List<CompleteDataset<? extends Record>> datasets = new ArrayList<CompleteDataset<? extends Record>>();

	// The week-number for which LMS guarantees some sort of stability/validity
	// for a subset of this rate. (The stable subset excludes pricing and
	// substitutions and possibly more)
	private int validityYear, validityWeekNumber;

	private final Date validFrom;
	private final Date validTo;


	public Takst(Date validFrom, Date validTo) {

		this.validFrom = validFrom;
		this.validTo = validTo;
	}


	/**
	 * @param type
	 *            the Type that the returned entities should have.
	 * 
	 * @return All entities of the given type in this takst.
	 */
	@SuppressWarnings("unchecked")
	public <T extends TakstRecord> TakstDataset<T> getDatasetOfType(Class<T> type) {

		for (CompleteDataset<? extends Record> dataset : datasets) {
			if (type.equals(dataset.getType())) {
				return (TakstDataset<T>) dataset;
			}
		}

		return null;
	}


	public List<CompleteDataset<? extends Record>> getDatasets() {

		return datasets;
	}


	public List<Record> getEntities() {

		List<Record> result = new ArrayList<Record>();

		for (CompleteDataset<? extends Record> dataset : datasets) {
			result.addAll(dataset.getEntities());
		}

		return result;
	}


	// TODO: What should the type argument be here?
	public void addDataset(TakstDataset<?> dataset) {

		datasets.add(dataset);
	}


	/**
	 * @param type
	 *            the type of the requested entity
	 * @param entityId
	 *            the id of the requested entity
	 * @return the requested entity
	 */
	public <T extends TakstRecord> T getEntity(Class<T> type, Object entityId) {

		if (entityId == null) return null;

		Dataset<T> avds = getDatasetOfType(type);

		if (avds == null) return null;

		return avds.getRecordById(entityId);
	}


	@Column(name = "TakstUge")
	public String getStableWeek() {

		return "" + validityYear + validityWeekNumber;
	}


	public void setValidityYear(int validityYear) {

		this.validityYear = validityYear;
	}


	public void setValidityWeekNumber(int validityWeekNumber) {

		this.validityWeekNumber = validityWeekNumber;
	}


	@Override
	@Id
	public Date getValidFrom() {

		return validFrom;
	}


	@Override
	public Date getValidTo() {

		return validTo;
	}
}
