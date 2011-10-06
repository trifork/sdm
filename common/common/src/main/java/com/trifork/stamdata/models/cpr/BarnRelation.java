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


package com.trifork.stamdata.models.cpr;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.models.BaseTemporalEntity;


@Entity
public class BarnRelation extends BaseTemporalEntity
{
	private String cpr;
	private String barnCpr;
	private String id;

	@Id
	@Column
	public String getId()
	{
		return id;
	}
	
	// TODO: The ID Column is constructed by the data manager's entity class.
	// Actual construction of the id should be moved to the CPR parser.
	public void setId(String id)
	{
		this.id = id;
	}

	@Column
	public String getCpr()
	{
		return cpr;
	}

	public void setCpr(String cpr)
	{
		this.cpr = cpr;
	}

	@Column
	public String getBarnCpr()
	{
		return barnCpr;
	}

	public void setBarnCpr(String barnCpr)
	{
		this.barnCpr = barnCpr;
	}
}
