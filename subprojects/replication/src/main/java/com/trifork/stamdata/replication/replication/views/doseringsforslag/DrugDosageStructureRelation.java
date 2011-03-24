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
@ViewPath("doseringsforslag/drugdosagestructurerelation/v1")
@Documented("Referencetabel der knytter doseringsstrukturer i dosageStructures til lægemidler.")
public class DrugDosageStructureRelation extends View {

	@Id
	@Column(name = "DrugDosageStructureRelationPID")
	@XmlTransient
	@GeneratedValue
	protected BigInteger recordID;

	@Column(length = 22)
	protected String id;

	// Reference til releaseNumber i Version. Obligatorisk. Heltal, 15 cifre.
	protected long releaseNumber;

	// Lægemidlets drug id. Reference til drugId i drugs. Obligatorisk. Heltal,
	// 11 cifre.
	protected long drugId;

	// Reference til code i dosageStructure. Obligatorisk. Heltal, 11 cifre.
	@Column(length = 11)
	protected long dosageStructureCode;

	@Column(name="ValidFrom")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date validFrom;

	@Column(name="ModifiedDate")
	@XmlTransient
	@Temporal(TemporalType.TIMESTAMP)
	protected Date modifiedDate;

	@Override
	public String getId() {

		// TODO (thb): Are these id elements even needed?

		return id;
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
