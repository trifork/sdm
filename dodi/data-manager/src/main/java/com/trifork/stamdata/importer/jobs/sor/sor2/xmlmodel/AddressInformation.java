/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel;

public class AddressInformation {
    public enum CountryIdentificationScheme {
        iso3166_alpha2, iso3166_alpha3, un_numeric3, imk
    }

    private String stairway; //can be null, up to 40 chars

    private String mailDeliverySublocationIdentifier;
    private String streetName; //required
    private String streetNameForAddressingName;
    private String streetBuildingIdentifier; //required
    private String floorIdentifier;
    private String suiteIdentifier;
    private String districtSubdivisionIdentifier;
    private Integer postOfficeBoxIdentifier;
    private int postCodeIdentifier;//required
    private String districtName;//required

    private String countryIdentificationCode; //optional, can contain either string or int content depending on which CountryIdentificationScheme is being used - possible values are: "iso3166-alpha2" => String with length 2, "iso3166-alpha3" => String with length 3, "un-numeric3" => int of length 3, "imk" => int of length 4
    private CountryIdentificationScheme countryIdentificationScheme;


    public String getCountryIdentificationCode() {
        return countryIdentificationCode;
    }

    public void setCountryIdentificationCode(String countryIdentificationCode) {
        this.countryIdentificationCode = countryIdentificationCode;
    }

    public CountryIdentificationScheme getCountryIdentificationScheme() {
        return countryIdentificationScheme;
    }

    public void setCountryIdentificationScheme(CountryIdentificationScheme countryIdentificationScheme) {
        this.countryIdentificationScheme = countryIdentificationScheme;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getDistrictSubdivisionIdentifier() {
        return districtSubdivisionIdentifier;
    }

    public void setDistrictSubdivisionIdentifier(String districtSubdivisionIdentifier) {
        this.districtSubdivisionIdentifier = districtSubdivisionIdentifier;
    }

    public String getFloorIdentifier() {
        return floorIdentifier;
    }

    public void setFloorIdentifier(String floorIdentifier) {
        this.floorIdentifier = floorIdentifier;
    }

    public String getMailDeliverySublocationIdentifier() {
        return mailDeliverySublocationIdentifier;
    }

    public void setMailDeliverySublocationIdentifier(String mailDeliverySublocationIdentifier) {
        this.mailDeliverySublocationIdentifier = mailDeliverySublocationIdentifier;
    }

    public int getPostCodeIdentifier() {
        return postCodeIdentifier;
    }

    public void setPostCodeIdentifier(int postCodeIdentifier) {
        this.postCodeIdentifier = postCodeIdentifier;
    }

    public Integer getPostOfficeBoxIdentifier() {
        return postOfficeBoxIdentifier;
    }

    public void setPostOfficeBoxIdentifier(Integer postOfficeBoxIdentifier) {
        this.postOfficeBoxIdentifier = postOfficeBoxIdentifier;
    }

    public String getStairway() {
        return stairway;
    }

    public void setStairway(String stairway) {
        this.stairway = stairway;
    }

    public String getStreetBuildingIdentifier() {
        return streetBuildingIdentifier;
    }

    public void setStreetBuildingIdentifier(String streetBuildingIdentifier) {
        this.streetBuildingIdentifier = streetBuildingIdentifier;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNameForAddressingName() {
        return streetNameForAddressingName;
    }

    public void setStreetNameForAddressingName(String streetNameForAddressingName) {
        this.streetNameForAddressingName = streetNameForAddressingName;
    }

    public String getSuiteIdentifier() {
        return suiteIdentifier;
    }

    public void setSuiteIdentifier(String suiteIdentifier) {
        this.suiteIdentifier = suiteIdentifier;
    }


}
