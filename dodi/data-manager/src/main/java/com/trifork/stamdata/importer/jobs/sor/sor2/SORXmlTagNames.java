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

package com.trifork.stamdata.importer.jobs.sor.sor2;

public class SORXmlTagNames {
	
	public static final String INSTITUTION_OWNER_ENTITY = "InstitutionOwnerEntity";
	public static final String INSTITUTION_OWNER = "InstitutionOwner";
    // 
	public static final String SOR_IDENTIFIER = "SorIdentifier";
	public static final String ENTITY_NAME = "EntityName";
	public static final String OWNER_TYPE = "OwnerType";
	public static final String SOR_STATUS = "SorStatus";
	public static final String EAN_LOCATION_CODE_ENTITY = "EanLocationCodeEntity";
	public static final String POSTAL_ADDRESS_INFO = "PostalAddressInformation";
	public static final String VIRTUAL_ADDRESS_INFO = "VirtualAddressInformation";
    
    // SorStatusType element children
	public static final String FROM_DATE = "FromDate";
	public static final String TO_DATE = "ToDate";
	public static final String UPDATED_AT_DATE = "UpdatedAt";
	public static final String FIRST_FROM_DATE = "FirstFromDate";
    
    // VirtualAddressInformation children
	public static final String EMAIL_ADDRESS_IDENTIFIER = "EmailAddressIdentifier";
	public static final String WEBSITE = "Website";
	public static final String TELEPHONE_NUMBER_IDENTIFIER = "TelephoneNumberIdentifier";
	public static final String FAX_NUMBER_IDENTIFIER = "FaxNumberIdentifier";

    // EanLocationCodeEntity children
	public static final String EAN_LOCATION_CODE = "EanLocationCode";
	public static final String ONLY_INTERNAL_INDICATOR = "OnlyInternalIndicator";
	public static final String NON_ACTIVITY_INDICATOR = "NonActiveIndicator";
	public static final String SYSTEM_SUPPLIER = "SystemSupplier";
	public static final String SYSTEM_TYPE = "SystemType";
    public static final String COMMUNICATION_SUPPLIER = "CommunicationSupplier";
    public static final String REGION_CODE = "RegionCode";
    public static final String EDI_ADMINISTRATOR = "EdiAdministrator";
    public static final String SOR_NOTE = "SorNote";
    
    // PostalAddressInformation children
    public static final String STAIRWAY = "Stairway";
    public static final String MAIL_DELIVERY_SUBLOC_IDENT = "dkcc:MailDeliverySublocationIdentifier";
    public static final String STREET_NAME = "dkcc2005:StreetName";
    public static final String STREET_NAME_FORADDRESSING = "cpr:StreetNameForAddressingName";
    public static final String STREET_BUILDING_IDENTIFIER = "dkcc:StreetBuildingIdentifier";
    public static final String FLOOR_IDENTIFIER = "dkcc:FloorIdentifier";
    public static final String SUITE_IDENTIFIER = "dkcc:SuiteIdentifier";
    public static final String DISTRICT_SUBDIVISION_IDENT = "dkcc2005:DistrictSubdivisionIdentifier";
    public static final String POSTBOX_IDENTIFIER = "dkcc2005-2:PostOfficeBoxIdentifier";
    public static final String POSTCODE_IDENTIFIER = "dkcc2005:PostCodeIdentifier";
    public static final String DISTRICT_NAME = "dkcc2005:DistrictName";
    public static final String COUNTRY_IDENT_CODE = "dkcc:CountryIdentificationCode";
    
    //////////////////////////////////////////////////////////////////
    
    
}
