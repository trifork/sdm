// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.model;

import java.util.Date;
import java.util.List;


/**
 * A Dataset that is the complete truth within the given validfrom-validto
 * interval. That is, no other records are allowed other than the ones in this
 * dataset.
 *
 * @author rsl
 */
public class CompleteDataset<T extends StamdataEntity> extends Dataset<T>
{
	private final Date ValidFrom;
	private final Date ValidTo;

	protected CompleteDataset(Class<T> type, List<T> entities, Date validFrom, Date ValidTo)
	{
		super(entities, type);

		this.ValidFrom = validFrom;
		this.ValidTo = ValidTo;
	}

	public CompleteDataset(Class<T> type, Date validFrom, Date ValidTo)
	{
		super(type);

		this.ValidFrom = validFrom;
		this.ValidTo = ValidTo;
	}

	public Date getValidFrom()
	{
		return ValidFrom;
	}

	public Date getValidTo()
	{
		return ValidTo;
	}
}
