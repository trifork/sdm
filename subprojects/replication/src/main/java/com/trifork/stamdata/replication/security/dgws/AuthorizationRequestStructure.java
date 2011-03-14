package com.trifork.stamdata.replication.security.dgws;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AuthorizationRequestStructure {

	private String viewURI;

	protected AuthorizationRequestStructure() {

	}

	public AuthorizationRequestStructure(String viewURI) {

		this.viewURI = checkNotNull(viewURI);
	}

	public String getViewURI() {

		return viewURI;
	}
}
