package com.trifork.stamdata.importer.persistence;


import java.util.Date;

import com.trifork.stamdata.Record;


public interface RecordStorage<T extends Record>
{

	boolean fetchEntityVersions(Object entityId, Date validFrom, Date validTo);


	void insertRow(T entity, Date transactionTime);


	void insertAndUpdateRow(T entity, Date transactionTime);


	void updateRow(T sde, Date transactionTime, Date existingValidFrom,
			Date existingValidTo);


	Date getCurrentRowValidFrom();


	Date getCurrentRowValidTo();


	boolean dataInCurrentRowEquals(T entity);


	void copyCurrentRowButWithChangedValidFrom(Date validTo, Date now);


	void updateValidToOnCurrentRow(Date validFrom, Date now);


	void updateValidFromOnCurrentRow(Date validFrom, Date now);


	void deleteCurrentRow();


	boolean hasMoreRows();


	int getUpdatedRecords();


	int getInsertedRows();


	int getDeletedRecords();


	void truncate();


	void drop();
}
