package dk.trifork.sdm.importer.cpr.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.util.DateUtils;

import java.util.Calendar;


public abstract class CPREntity extends AbstractStamdataEntity {

	CPRDataset dataset;
	String cpr;

	public String getCpr() {

		return cpr;
	}

	public void setCpr(String cpr) {

		this.cpr = cpr;
	}

	@Override
	public Calendar getValidTo() {

		return DateUtils.FUTURE;
	}

	public CPRDataset getDataset() {

		return dataset;
	}

	public void setDataset(CPRDataset dataset) {

		this.dataset = dataset;
	}

	@Override
	public Calendar getValidFrom() {

		return dataset.getValidFrom();
	}
}
