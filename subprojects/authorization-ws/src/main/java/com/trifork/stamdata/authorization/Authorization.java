package com.trifork.stamdata.authorization;

import static com.trifork.stamdata.authorization.Preconditions.checkNotNull;
import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


@Entity
@Table(name = "autreg")
@XmlType(propOrder = { "authorizationCode", "educationCode", "educationName" })
@XmlAccessorType(FIELD)
public class Authorization {

	protected Authorization() {
		
	}
	
	@XmlTransient
	private static Map<String,String> educations = new HashMap<String, String>(); {

		educations.put("4498", "Optiker");
		educations.put("5151", "Fysioterapeut");
		educations.put("5153", "Ergoterapeut");
		educations.put("5155", "Fodterapeut");
		educations.put("5158", "Radiograf");
		educations.put("5159", "Bioanalytiker");
		educations.put("5166", "Sygeplejerske");
		educations.put("5175", "Jordemoder");
		educations.put("5265", "Kiropraktor");
		educations.put("5431", "Tandplejer");
		educations.put("5432", "Klinisk Tandtekniker");
		educations.put("5433", "Tandlæge");
		educations.put("5451", "Klinisk diætist");
		educations.put("7170", "Læge");
		educations.put("9495", "Bandagist");
	}

	public Authorization(String cpr, String givenName, String surname, String authorizationCode, String educationCode) {

		this.cpr = checkNotNull(cpr);
		this.firstName = checkNotNull(givenName);
		this.lastName = checkNotNull(surname);
		this.authorizationCode = checkNotNull(authorizationCode);
		this.educationCode = checkNotNull(educationCode);
		this.educationName = educations.get(educationCode);
	}

	@Id
	@XmlTransient
	@GeneratedValue
	protected BigInteger id;
	
	@XmlTransient
	@Column(nullable = false, length = 10)
	protected String cpr;

	@XmlTransient
	@Column(name = "given_name", nullable = false, length = 50)
	protected String firstName;

	@XmlTransient
	@Column(name = "surname", nullable = false, length = 100)
	protected String lastName;

	@Column(name = "aut_id", nullable = false, length = 5)
	@XmlElement(required=true)
	protected String authorizationCode;

	@Column(name = "edu_id", nullable = false, length = 4)
	@XmlElement(required=true)
	protected String educationCode;

	@Transient
	@XmlElement(name = "educationName", required=false)
	protected String educationName;
}
