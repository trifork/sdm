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

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.Documented;
import com.trifork.stamdata.replication.replication.annotations.ViewPath;
import com.trifork.stamdata.replication.replication.views.View;


@Entity
@XmlRootElement
@ViewPath("doseringsforslag/dosageunit/v1")
@Documented("Indeholder anvendte doseringsenheder.\n" + "Doseringsenhederne stammer dels fra Lægemiddelstyrelsens takst (her er code <= 1000),\n" + "dels er der tale om nye data (code > 1000).")
public class DosageUnit extends View {

	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "DosageUnitPID")
	protected BigInteger recordID;

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	protected long releaseNumber;

	// Unik kode for doseringsenheden. Obligatorisk. Heltal, 4 cifre.
	@Column(length = 4)
	protected int code;

	// Doseringenhedens tekst i ental. Obligatorisk. Streng, 100 tegn.
	@Column(length = 100)
	protected String textSingular;

	// Doseringsenhedens tekst i flertal. Obligatorisk. Streng, 100 tegn.
	@Column(length = 100)
	protected String textPlural;

	@Column(name="ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name="ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlTransient
	protected Date modifiedDate;

	@Override
	public BigInteger getRecordID() {

		return recordID;
	}

	@Override
	public Date getUpdated() {

		return modifiedDate;
	}

	@Override
	public String getId() {

		return Integer.toString(code);
	}
}
