package com.trifork.stamdata.authorization;

import static com.trifork.stamdata.authorization.Preconditions.checkNotNull;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "cpr", "firstName", "lastName", "authorizations" })
public class AuthorizationResponseStructure {

	protected AuthorizationResponseStructure() {
		
	}
	
	@Id
	@GeneratedValue
	@XmlTransient
	protected BigInteger AutorisationPID;

	@XmlElement(required = true)
	protected String cpr;
	
	@XmlElement(required = false)
	protected String firstName;

	@XmlElement(required = false)
	protected String lastName;

	@XmlElement(name = "authorization", required = false)
	protected List<Authorization> authorizations;

	protected AuthorizationResponseStructure(String cpr, List<Authorization> authorizations) {

		this.cpr = checkNotNull(cpr);
		this.authorizations = checkNotNull(authorizations);
		
		if (!authorizations.isEmpty()) {
			
			firstName = checkNotNull(authorizations.get(0).firstName);
			lastName = checkNotNull(authorizations.get(0).lastName);
		}
	}
}
