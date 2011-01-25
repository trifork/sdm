package com.trifork.stamdata.persistence;

import java.util.Date;
import java.util.List;

import com.trifork.stamdata.Record;


/**
 * A Dataset that is the complete truth within the given validfrom-validto
 * interval. That is, no other records are allowed other than the ones in this
 * dataset.
 */
public class CompleteDataset<T extends Record> extends Dataset<T> {

	private final Date validFrom;
	private final Date validTo;


	protected CompleteDataset(Class<T> type, List<T> entities, Date validFrom, Date ValidTo) {

		super(entities, type);

		this.validFrom = validFrom;
		this.validTo = ValidTo;
	}


	public CompleteDataset(Class<? extends T> type, Date validFrom, Date ValidTo) {

		super(type);

		this.validFrom = validFrom;
		this.validTo = ValidTo;
	}


	public Date getValidFrom() {

		return validFrom;
	}


	public Date getValidTo() {

		return validTo;
	}

}
