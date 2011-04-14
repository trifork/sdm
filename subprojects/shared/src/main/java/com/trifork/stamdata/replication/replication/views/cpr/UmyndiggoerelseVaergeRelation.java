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

package com.trifork.stamdata.replication.replication.views.cpr;

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

import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.replication.views.ViewPath;


@Entity
@XmlRootElement
@ViewPath("cpr/umyndigoerelsevaergerelation/v1")
public class UmyndiggoerelseVaergeRelation extends View {

	@Id
	@GeneratedValue
	@Column(name = "UmyndiggoerelseVaergeRelationPID")
	private BigInteger recordID;

	@Column(name = "Id")
	protected String id;

	@Column(name = "CPR")
	protected String cpr;

	@Column(name = "TypeKode")
	protected String typeKode;

	@Column(name = "TypeTekst")
	protected String typeTekst;

	@Column(name = "RelationCpr")
	protected String relationCpr;

	@Column(name = "RelationCprStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date relationCprStartDato;

	@Column(name = "VaergesNavn")
	protected String vaergesNavn;

	@Column(name = "VaergesNavnStartDato")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date vaergesNavnStartDato;

	@Column(name = "relationsTekst1")
	protected String RelationsTekst1;

	@Column(name = "relationsTekst2")
	protected String RelationsTekst2;

	@Column(name = "relationsTekst3")
	protected String RelationsTekst3;

	@Column(name = "relationsTekst4")
	protected String RelationsTekst4;

	@Column(name = "relationsTekst5")
	protected String RelationsTekst5;

	@XmlTransient
	@Column(name = "ModifiedDate")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@Column(name = "ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name = "ValidTo")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validTo;

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
	
	@Override
	public String toString() {
		return "Umynddiggørelse-værge-relation[cpr=" + cpr + ", type=" + typeKode + ", tekst=" + typeTekst + ", relations-cpr=" + relationCpr + "]";
	}
}
