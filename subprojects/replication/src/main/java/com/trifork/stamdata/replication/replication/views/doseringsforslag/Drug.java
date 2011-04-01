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

package com.trifork.stamdata.replication.replication.views.doseringsforslag;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.Documented;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity(name="DosageDrug")
@XmlRootElement
@ViewPath("doseringsforslag/drug/v1")
@Documented("Indeholder information om lægemidlers drug-id og doseringsenhed.")
public class Drug extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DosageDrugPID")
	protected BigInteger recordID;

	protected int releaseNumber;

	@Column(length = 11)
	protected long drugId;

	@Column(length = 200)
	protected String drugName;

	@Column(length = 4)
	protected int dosageUnitCode;

	@Column(name = "ModifiedDate")
	@XmlTransient
	@Temporal(TIMESTAMP)
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TIMESTAMP)
	protected Date validFrom;

	@Override
	public String getId() {

		return Long.toString(drugId);
	}

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
