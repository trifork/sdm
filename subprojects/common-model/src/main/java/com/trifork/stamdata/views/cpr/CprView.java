package com.trifork.stamdata.views.cpr;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.util.DateUtils;
import com.trifork.stamdata.views.View;

@MappedSuperclass
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class CprView extends View {

	@Id
	@GeneratedValue
	@XmlElement(required = true)
	private BigInteger recordID;

	@XmlElement(required = true)
	protected String cpr;

	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	protected Date createdDate;

	@Temporal(TIMESTAMP)
	@XmlElement(required = true)
	protected Date modifiedDate;

	@Temporal(TIMESTAMP)
	@XmlTransient // uses property-based access type
	protected Date validFrom;

	@Temporal(TIMESTAMP)
	@XmlTransient // uses property-based access type
	protected Date validTo;

	@XmlTransient // not of interest to clients
	public String createdBy;
	@XmlTransient // not of interest to clients
	public String modifiedBy;

	public String getCpr() {
		return cpr;
	}

	public void setCpr(String cpr) {
		this.cpr = cpr;
	}

	@Override
	public BigInteger getRecordID() {
		return recordID;
	}

	@Override
	public Date getUpdated() {
		return modifiedDate;
	}

	@XmlElement(required = false)
	public Date getValidFrom() {
		return DateUtils.nullIfPast(validFrom);
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	@XmlElement(required = false)
	public Date getValidTo() {
		return DateUtils.nullIfFuture(validTo);
	}

	public void setValidTo(Date validTo) {
		this.validTo = validTo;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
}