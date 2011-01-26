package com.trifork.stamdata.registre.takst;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.trifork.stamdata.XmlName;
import com.trifork.stamdata.XmlOrder;


@Entity
@Table(name = "LaegemiddelDoseringRef")
public class Doseringskode extends TakstRecord
{
	private Long drugid; // Ref. t. LMS01
	private Long doseringskode; // Ref. t. LMS28


	@Id
	@Column
	@XmlName("id")
	@XmlOrder(1)
	public String getCID()
	{
		return drugid + "-" + doseringskode;
	}


	@Column(name = "DrugID")
	@XmlOrder(2)
	public long getDrugid()
	{
		return this.drugid;
	}


	public void setDrugid(Long drugid)
	{
		this.drugid = drugid;
	}


	@Column(name = "DoseringKode")
	@XmlName("doseringskode")
	@XmlOrder(3)
	public long getDoseringskode()
	{
		return this.doseringskode;
	}


	public void setDoseringskode(Long doseringskode)
	{
		this.doseringskode = doseringskode;
	}
}
