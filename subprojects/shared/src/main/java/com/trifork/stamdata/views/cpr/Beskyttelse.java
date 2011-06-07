package com.trifork.stamdata.views.cpr;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.views.ViewPath;

@Entity
@Table(name = "Beskyttelse")
@XmlRootElement
@ViewPath("cpr/beskyttelse/v1")
@XmlAccessorType(XmlAccessType.FIELD)
@AttributeOverride(name = "recordID",column = @Column(name = "BeskyttelsePID"))
public class Beskyttelse extends CprView {

	@XmlTransient
	public String id;
	
	
	@XmlElement(required = true)
	public String beskyttelsestype;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "Beskyttelse[cpr=" + cpr + ", beskyttelsestype=" + beskyttelsestype + "]";
	}
}
