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

package com.trifork.stamdata.replication.replication.views.autorisationsregisteret;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@ViewPath("autorisationsregisteret/autorisation/v1")
public class Autorisation extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "AutorisationPID")
	private BigInteger recordID;

	@Column(name = "Autorisationsnummer")
	protected String id;

	protected String cpr;

	@Column(name = "Fornavn")
	protected String fornavn;

	@Column(name = "Efternavn")
	protected String efternavn;

	@Column(name = "UddannelsesKode")
	protected String uddannelsesKode;

	@Column(name = "ValidFrom")
	protected Date validFrom;

	@Column(name = "ValidTo")
	protected Date validTo;

	@XmlTransient
	@Temporal(TIMESTAMP)
	@Column(name = "ModifiedDate")
	private Date modifiedDate;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}
}
