package com.trifork.stamdata.registre.takst;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.trifork.stamdata.persistence.CompleteDataset;


public class TakstDataset<T extends TakstRecord> extends CompleteDataset<T> {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private Takst takst;

	public TakstDataset(Takst takst, List<T> entities, Class<T> type) {

		super(type, entities, takst.getValidFrom(), takst.getValidTo());

		for (TakstRecord entity : entities) {
			entity.takst = takst;
		}

		this.takst = takst;
	}


	@Override
	public Date getValidFrom() {

		return takst.getValidFrom();
	}


	@Override
	public Date getValidTo() {

		return takst.getValidTo();
	}


	@Override
	public void addRecord(T entity) {

		super.addRecord(entity);
		entity.takst = takst;

	}
}
