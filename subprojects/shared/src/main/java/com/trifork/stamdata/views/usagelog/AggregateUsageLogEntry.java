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

package com.trifork.stamdata.views.usagelog;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.UsageLogged;
import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@Table(name="usagelogentry")
@UsageLogged(false)
@XmlRootElement
@ViewPath("usage/aggregate/v1")
@XmlAccessorType(XmlAccessType.FIELD)
public class AggregateUsageLogEntry extends View {
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "UsageLogEntryPID")
	public BigInteger recordID;

	@XmlElement(required = true)
	public String clientId;

	@XmlElement(required = true, name = "date")
	@Column(name="date")
	public Date modifiedDate;

	@XmlElement(required = true)
	public String type;

	@XmlElement(required = true)
	public int amount;

	public AggregateUsageLogEntry() {
		// JPA
	}

	public AggregateUsageLogEntry(String clientId, Date date, String type, int amount) {
		this.clientId = clientId;
		this.modifiedDate = date;
		this.type = type;
		this.amount = amount;
	}

	@Override
	public String getId() {
		return recordID.toString();
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@Override
	public String toString() {
		return "UsageLogEntry [clientId=" + clientId + ", modifiedDate="
				+ modifiedDate + ", type=" + type + ", amount=" + amount + "]";
	}
}
