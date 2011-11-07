/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.persistence;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;

import com.trifork.stamdata.models.TemporalEntity;


/**
 * @author Rune Skou Larsen <rsj@trifork.com>
 */
public interface Persister
{
	/**
	 * This method is for persisting the complete dataset so it is represented
	 * in the Datasets validity interval (dataset.ValidTo-dataset.ValidFrom).
	 * Conflicts with existing data in this range are resolved by deleting or
	 * modifying the existing. In other words, þe supplied dataset is treated as
	 * representing the whole truth within the datasets validity range.
	 * 
	 * If an existing entityVersion has validity in this datasets range, and is
	 * not part of this dataset, it will have this validity limited, by setting
	 * ValidTo to this datasets ValidFrom.
	 * 
	 * This dataset cannot overwrite existing records outside the datasets
	 * validity range. The Contained StamdateEntities are however persisted with
	 * a validTo exceeding the datasets, when no existing records exist in this
	 * range. For instance TakstEntities are valid for all eternity, or until
	 * another version exist.
	 * 
	 */
	public void persistCompleteDataset(CompleteDataset<? extends TemporalEntity>... dataset) throws Exception;

	/**
	 * Persist the records in the dataset.
	 */
	public <T extends TemporalEntity> void persistDeltaDataset(Dataset<T> dataset) throws Exception;

	/**
	 * HACK: This method returns the connection used by the persister.
	 * 
	 * DO NOT use this. It is implemented to allow the CPR parser to access its
	 * version table in the database. In the future the persister should allow
	 * the parsers access to a key/value store.
	 */
	public Connection getConnection();
	
	
	void persist(Object entity) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
}
