package com.trifork.stamdata.authorization;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationRequestStructure {

	protected String cpr;

	protected AuthorizationRequestStructure() {

	}
	
	public String getCpr() {
		return cpr;
	}
}
