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
    
    // SorStatusType element children
	public static final String FROM_DATE = "FromDate";
	public static final String TO_DATE = "ToDate";
	public static final String UPDATED_AT_DATE = "UpdatedAt";
	public static final String FIRST_FROM_DATE = "FirstFromDate";
    
	public static final String VIRTUAL_ADDRESS_INFO = "VirtualAddressInformation";
	public class VirtualAddressInformation {
		public static final String EMAIL_ADDRESS_IDENTIFIER = "xkom:EmailAddressIdentifier";
		public static final String WEBSITE = "Website";
		public static final String TELEPHONE_NUMBER_IDENTIFIER = "TelephoneNumberIdentifier";
		public static final String FAX_NUMBER_IDENTIFIER = "FaxNumberIdentifier";
	};

	public static final String EAN_LOCATION_CODE_ENTITY = "EanLocationCodeEntity";
	public class EanLocationCode {
		public static final String EAN_LOCATION_CODE = "EanLocationCode";
		public static final String ONLY_INTERNAL_INDICATOR = "OnlyInternalIndicator";
		public static final String NON_ACTIVITY_INDICATOR = "NonActiveIndicator";
		public static final String SYSTEM_SUPPLIER = "SystemSupplier";
		public static final String SYSTEM_TYPE = "SystemType";
	    public static final String COMMUNICATION_SUPPLIER = "CommunicationSupplier";
	    public static final String REGION_CODE = "RegionCode";
	    public static final String EDI_ADMINISTRATOR = "EdiAdministrator";
	    public static final String SOR_NOTE = "SorNote";
	    public static final String SOR_STATUS = "SorStatus";
	};

    public static final String POSTAL_ADDRESS_INFO = "PostalAddressInformation";
    public static final String ADDRESS_POSTAL = "xkom-1:AddressPostal";
    public class PostalAddressInformation {
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
    };
    
    public static final String ORGANIZATIONAL_UNIT_ENTITY = "OrganizationalUnitEntity";
    public class OrganizationalUnit {
    	public static final String SOR_IDENTIFIER = "SorIdentifier";
    	public static final String ENTITY_NAME = "EntityName";
    	public static final String UNIT_TYPE = "UnitType";
    	public static final String LOCAL_CODE = "LocalCode";
    	public static final String PHARMACY_IDENTIFIER = "PharmacyIdentifier";
    	public static final String SHAK_IDENTIFIER = "ShakIdentifier";
    	public static final String PROVIDER_IDENTIFIER = "ProviderIdentifier";
    	
    	public static final String OPTIONAL_EAN_LOCATION_CODE = "OptionalEanLocationCode";
    	public static final String EAN_ENTITY_INHERITED_INDICATOR = "EntityInheritedIndicator";
    	public static final String EAN_LOCATION_CODE = "EanLocationCodeEntity";
    	
    	public static final String GEOGRAPHICAL_PARENT = "GeographicalParent";
    	// SUB NODE
    	public static final String GEOGRAPHICAL_PARENT_RELATION = "GeographicalParentRelation";
    	public static final String GEOGRAPHICAL_PARENT_SOR_IDENTIFIER = "GeographicalParentSorIdentifier";
    	
    	public static final String POSTAL_ADDRESS_INFO = "PostalAddressInformation";
    	public static final String VISITING_ADDRESS_INFO = "VisitingAddressInformation";
    	public static final String ACTIVITY_ADDRESS_INFO = "ActivityAddressInformation";
    	public static final String VIRTUAL_ADDRESS_INFO = "VirtualAddressInformation";
    	public static final String CLINICAL_SPECIALITY_COLLECTION = "ClinicalSpecialityCollection"; // TODO
    	
    	public static final String SOR_STATUS = "SorStatus";
    	public static final String REPLACES_ENTITY_COLLECTION = "ReplacesEntityCollection"; // TODO
    	public static final String REPLACED_BY_ENTITY_COLLECTION = "ReplacedByEntityCollection"; // TODO
    	
    	public static final String ORGANIZATIONAL_UNIT_INFO = "OrganizationalUnitInformation";
    	// SUB NODES
  		public static final String AMBULANT_ACTIVITY_INDICATOR = "AmbulantActivityIndicator";
  		public static final String PATIENTS_ADMITTED_INDICATOR = "PatientsAdmittedIndicator";
   		public static final String REPORTING_LEVEL_INDICATOR = "ReportingLevelIndicator";
   		
   		public static final String LOCAL_ATTRBIBUTES_COLLECTION = "LocalAttributeCollection";
   		// SUB NODES
   		public static final String LOCAL_ATTRBIBUTE1 = "LocalAttribute1";
   		public static final String LOCAL_ATTRBIBUTE2 = "LocalAttribute1";
   		public static final String LOCAL_ATTRBIBUTE3 = "LocalAttribute1";
   		public static final String LOCAL_ATTRBIBUTE4 = "LocalAttribute1";
   		public static final String LOCAL_ATTRBIBUTE5 = "LocalAttribute1";
    };
    
    // HEALTH_INSTITUTION
    public static final String HEALTH_INSTITUTION = "HealthInstitution";
}
