package com.trifork.stamdata.views.sikrede;

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

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("sikrede/patientdoctorrelation/v1")
public class PatientDoctorRelation extends View
{
	@Id
	@GeneratedValue
	@XmlTransient
	@Column(name = "PatientDoctorRelationPID")
	protected BigInteger recordID;
	
	@Temporal(TIMESTAMP)
	@Column(name = "ValidFrom")
	protected Date validFrom;

	@XmlTransient
	@Temporal(TIMESTAMP)
	@Column(name = "ModifiedDate")
	protected Date modifiedDate;

	@Override
	public String getId()
	{
		return "TODO: The unique key for this view. Such as CPR for a person.";
	}

	@Override
	public BigInteger getRecordID()
	{
		return recordID;
	}

	@Override
	public Date getUpdated()
	{
		return modifiedDate;
	}
}
