package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.trifork.stamdata.XmlName;


@Entity
public class LaegemiddelAdministrationsvejRelation extends TakstRecord
{
	private long drugId;
	private String administrationsvejKode;


	public LaegemiddelAdministrationsvejRelation(Laegemiddel drug, Administrationsvej av)
	{
		this.drugId = drug.getDrugid();
		this.administrationsvejKode = av.getKey();
	}


	@Id
	@Column
	@XmlName("cid")
	public String getCID()
	{
		return drugId + "-" + administrationsvejKode;
	}


	@Column
	public long getDrugId()
	{
		return drugId;
	}


	public void setDrugId(long drugId)
	{
		this.drugId = drugId;
	}


	@Column
	@XmlName("administrationsvejkode")
	public String getAdministrationsvejKode()
	{
		return administrationsvejKode;
	}


	public void setAdministrationsvejKode(String administrationsvejKode)
	{
		this.administrationsvejKode = administrationsvejKode;
	}
}
