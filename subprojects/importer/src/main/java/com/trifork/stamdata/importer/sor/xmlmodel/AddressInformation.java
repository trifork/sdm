// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.importer.sor.xmlmodel;

public class AddressInformation {
	private String streetName;
	private String streetBuildingIdentifier;
	private String postCodeIdentifier;
	private String districtName;
	private String countryIdentificationCode;

	private String emailAddressIdentifier;
	private String website;
	private String telephoneNumberIdentifier;
	private String faxNumberIdentifier;

	private Long eanLocationCode;
	private Boolean entityInheritedIndicator;

	public String getStreetName() {
		return streetName;
	}
	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}
	public String getStreetBuildingIdentifier() {
		return streetBuildingIdentifier;
	}
	public void setStreetBuildingIdentifier(String streetBuildingIdentifier) {
		this.streetBuildingIdentifier = streetBuildingIdentifier;
	}
	public String getPostCodeIdentifier() {
		return postCodeIdentifier;
	}
	public void setPostCodeIdentifier(String postCodeIdentifier) {
		this.postCodeIdentifier = postCodeIdentifier;
	}
	public String getDistrictName() {
		return districtName;
	}
	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}
	public String getCountryIdentificationCode() {
		return countryIdentificationCode;
	}
	public void setCountryIdentificationCode(String countryIdentificationCode) {
		this.countryIdentificationCode = countryIdentificationCode;
	}

	public String getEmailAddressIdentifier() {
		return emailAddressIdentifier;
	}
	public void setEmailAddressIdentifier(String emailAddressIdentifier) {
		this.emailAddressIdentifier = emailAddressIdentifier;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public String getTelephoneNumberIdentifier() {
		return telephoneNumberIdentifier;
	}
	public void setTelephoneNumberIdentifier(String telephoneNumberIdentifier) {
		this.telephoneNumberIdentifier = telephoneNumberIdentifier;
	}
	public String getFaxNumberIdentifier() {
		return faxNumberIdentifier;
	}
	public void setFaxNumberIdentifier(String faxNumberIdentifier) {
		this.faxNumberIdentifier = faxNumberIdentifier;
	}
	public Long getEanLocationCode() {
		return eanLocationCode;
	}
	public void setEanLocationCode(Long eanLocationCode) {
		this.eanLocationCode = eanLocationCode;
	}
	public Boolean isEntityInheritedIndicator() {
		return entityInheritedIndicator;
	}
	public void setEntityInheritedIndicator(Boolean entityInheritedIndicator) {
		this.entityInheritedIndicator = entityInheritedIndicator;
	}

}
