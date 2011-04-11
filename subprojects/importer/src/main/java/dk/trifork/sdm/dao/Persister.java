// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package dk.trifork.sdm.dao;

import dk.trifork.sdm.importer.exceptions.FilePersistException;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.model.Dataset;
import dk.trifork.sdm.model.StamdataEntity;


public interface Persister {

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
	public void persistCompleteDataset(CompleteDataset<? extends StamdataEntity>... dataset) throws FilePersistException;

	/**
	 * Persist the records in the dataset.
	 */
	public <T extends StamdataEntity> void persistDeltaDataset(Dataset<T> dataset) throws FilePersistException;
}
