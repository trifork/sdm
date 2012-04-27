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

import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.inject.Inject;

public class SORFullEventHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(SORFullEventHandler.class);
	private final RecordPersister persister;
	
    private String characterContent;

    private final String INSTITUTION_OWNER_ENTITY = "InstitutionOwnerEntity";
    private final String INSTITUTION_OWNER = "InstitutionOwner";

    // 
    private final String SOR_IDENTIFIER = "SorIdentifier";
    private final String ENTITY_NAME = "EntityName";
    private final String OWNER_TYPE = "OwnerType";
    private final String SOR_STATUS = "SorStatus";
    private final String EAN_LOCATION_CODE_ENTITY = "EanLocationCodeEntity";
    private final String POSTAL_ADDRESS_INFO = "PostalAddressInformation";
    private final String VIRTUAL_ADDRESS_INFO = "VirtualAddressInformation";
    
    // SorStatusType element children
    private final String FROM_DATE = "FromDate";
    private final String TO_DATE = "ToDate";
    private final String UPDATED_AT_DATE = "UpdatedAt";
    private final String FIRST_FROM_DATE = "FirstFromDate";
    
    // VirtualAddressInformation children
    private final String EMAIL_ADDRESS_IDENTIFIER = "EmailAddressIdentifier";
    private final String WEBSITE = "Website";
    private final String TELEPHONE_NUMBER_IDENTIFIER = "TelephoneNumberIdentifier";
    private final String FAX_NUMBER_IDENTIFIER = "FaxNumberIdentifier";

    // EanLocationCodeEntity children
    private final String EAN_LOCATION_CODE = "EanLocationCode";
    private final String ONLY_INTERNAL_INDICATOR = "OnlyInternalIndicator";
    private final String NON_ACTIVITY_INDICATOR = "NonActiveIndicator";
    private final String SYSTEM_SUPPLIER = "SystemSupplier";
    private final String SYSTEM_TYPE = "SystemType";
    private final String COMMUNICATION_SUPPLIER = "CommunicationSupplier";
    private final String REGION_CODE = "RegionCode";
    private final String EDI_ADMINISTRATOR = "EdiAdministrator";
    private final String SOR_NOTE = "SorNote";
    
    // PostalAddressInformation children
    private final String STAIRWAY = "Stairway";
    private final String MAIL_DELIVERY_SUBLOC_IDENT = "dkcc:MailDeliverySublocationIdentifier";
    private final String STREET_NAME = "dkcc2005:StreetName";
    private final String STREET_NAME_FORADDRESSING = "cpr:StreetNameForAddressingName";
    private final String STREET_BUILDING_IDENTIFIER = "dkcc:StreetBuildingIdentifier";
    private final String FLOOR_IDENTIFIER = "dkcc:FloorIdentifier";
    private final String SUITE_IDENTIFIER = "dkcc:SuiteIdentifier";
    private final String DISTRICT_SUBDIVISION_IDENT = "dkcc2005:DistrictSubdivisionIdentifier";
    private final String POSTBOX_IDENTIFIER = "dkcc2005-2:PostOfficeBoxIdentifier";
    private final String POSTCODE_IDENTIFIER = "dkcc2005:PostCodeIdentifier";
    private final String DISTRICT_NAME = "dkcc2005:DistrictName";
    private final String COUNTRY_IDENT_CODE = "dkcc:CountryIdentificationCode";
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());
    
    private RecordBuilder currentInstitutionOwnerRecord = new RecordBuilder(SorFullRecordSpecs.INSTITUTIONS_EJER);
    private RecordBuilder currentSorStatusRecord = new RecordBuilder(SorFullRecordSpecs.SOR_STATUS);
    private RecordBuilder currentLocatonCodeRecord = new RecordBuilder(SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
    private RecordBuilder currentAddressInformationRecord = new RecordBuilder(SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);
    private RecordBuilder currentVirtualInformationRecord = new RecordBuilder(SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
    
    //private ArrayList<InstitutionOwner> institutionOwners;
    private ArrayList<RecordBuilder> institutionOwners;
    
	@Inject
	public
    SORFullEventHandler(RecordPersister persister)
    {
        this.persister = persister;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	characterContent = new String();
    	
        if (qName.equals("SnapshotDate")) {

        }

        if (INSTITUTION_OWNER_ENTITY.equals(qName)) {
            institutionOwners = new ArrayList<RecordBuilder>();
        }

        if (INSTITUTION_OWNER.equals(qName)) {
            currentInstitutionOwnerRecord = new RecordBuilder(SorFullRecordSpecs.INSTITUTIONS_EJER);
            institutionOwners.add(currentInstitutionOwnerRecord);
        }
        
        if (currentInstitutionOwnerRecord != null) {
        	if (SOR_STATUS.equals(qName)) {
        		currentSorStatusRecord = new RecordBuilder(SorFullRecordSpecs.SOR_STATUS);
        	}
        	if (EAN_LOCATION_CODE_ENTITY.equals(qName)) {
        		currentLocatonCodeRecord = new RecordBuilder(SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
        	}
        	if (POSTAL_ADDRESS_INFO.equals(qName)) {
        		currentAddressInformationRecord = new RecordBuilder(SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION); 
        	}
        	if (VIRTUAL_ADDRESS_INFO.equals(qName)) {
        		currentVirtualInformationRecord = new RecordBuilder(SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
        	}
        }

        super.startElement(uri, localName, qName, atts);
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("SnapshotDate")) {

        }

        if (INSTITUTION_OWNER.equals(qName)) {
            // FIXME: currentInstitutionOwner
            // institutionOwners.add(currentInstitutionOwner);
            
        	// System.out.println("Institution parsed: " + currentInstitutionOwner);
        	try
	        {
	            persister.persist(currentInstitutionOwnerRecord.build(), SorFullRecordSpecs.INSTITUTIONS_EJER);
	        }
	        catch (SQLException e)
	        {
	            throw new SAXException(e);
	        }
        	currentInstitutionOwnerRecord = null;
        } else if (currentInstitutionOwnerRecord != null ) {
        	if (ENTITY_NAME.equals(qName)) {
        		currentInstitutionOwnerRecord.field("entityName", characterContent);
        	} else if (SOR_IDENTIFIER.equals(qName)) {
        		currentInstitutionOwnerRecord.field("sorIdentifier", Long.valueOf(characterContent));
        	} else if (OWNER_TYPE.equals(qName)) {
        		currentInstitutionOwnerRecord.field("ownerType", Long.valueOf(characterContent));
        	} else if (SOR_STATUS.equals(qName)) {
        		// currentInstitutionOwner.setSorStatus(currentSorStatus);
        		// currentSorStatus = null;
        		// TODO FIXME
        	} else if (EAN_LOCATION_CODE_ENTITY.equals(qName)) {
        		persist(currentLocatonCodeRecord, SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
        		currentLocatonCodeRecord = null;
        		// TODO link current institionowner to this eanlocationcode somehow....
        	} else if (POSTAL_ADDRESS_INFO.equals(qName)) {
        		persist(currentAddressInformationRecord, SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);
        		currentAddressInformationRecord = null;
        		// TODO link current institionowner to this postal address somehow....
        	} else if (VIRTUAL_ADDRESS_INFO.equals(qName)) {
        		persist(currentVirtualInformationRecord, SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
        		currentVirtualInformationRecord = null;
        		// TODO link current institionowner to this virtual info address somehow....
        	}
        }
        if (currentSorStatusRecord != null) {
        	if (FROM_DATE.equals(qName)) {
        		currentSorStatusRecord.field("fromDate", characterContent);
    		} else if (TO_DATE.equals(qName)) {
    			currentSorStatusRecord.field("toDate", characterContent);
    		} else if (UPDATED_AT_DATE.equals(qName)) {
    			currentSorStatusRecord.field("updatedAt", characterContent);
    		} else if (FIRST_FROM_DATE.equals(qName)) {
    			currentSorStatusRecord.field("firstFromDate", characterContent);
    		}
        }
        if (currentLocatonCodeRecord != null) {
        	if (EAN_LOCATION_CODE.endsWith(qName)) {
        		currentLocatonCodeRecord.field("eanLocationCode", Long.valueOf(characterContent));
        	} else if (ONLY_INTERNAL_INDICATOR.endsWith(qName)) {
        		setBoolField(currentLocatonCodeRecord, "onlyInternalIndicator", characterContent);
        	} else if (NON_ACTIVITY_INDICATOR.endsWith(qName)) {
        		setBoolField(currentLocatonCodeRecord, "nonActiveIndicator", characterContent);
        	} else if (SYSTEM_SUPPLIER.endsWith(qName)) {
        		currentLocatonCodeRecord.field("systemSupplier", Long.valueOf(characterContent));
        	} else if (SYSTEM_TYPE.endsWith(qName)) {
        		currentLocatonCodeRecord.field("systemType", Long.valueOf(characterContent));
        	} else if (COMMUNICATION_SUPPLIER.endsWith(qName)) {
        		currentLocatonCodeRecord.field("communicationSupplier", Long.valueOf(characterContent));
        	} else if (REGION_CODE.endsWith(qName)) {
        		currentLocatonCodeRecord.field("regionCode", Long.valueOf(characterContent));
        	} else if (EDI_ADMINISTRATOR.endsWith(qName)) {
        		currentLocatonCodeRecord.field("ediAdministrator", Long.valueOf(characterContent));
        	} else if (SOR_NOTE.endsWith(qName)) {
        		currentLocatonCodeRecord.field("sorNote", characterContent);
        	} else if (SOR_STATUS.equals(qName)) {
        		//currentLocationCode.setSorStatus(currentSorStatus);
        		//currentSorStatus = null;
        	}
        }
        if (currentAddressInformationRecord != null) {
        	if (STAIRWAY.equals(qName)) {
        		currentAddressInformationRecord.field("stairway", characterContent);
        	} else if (MAIL_DELIVERY_SUBLOC_IDENT.equals(qName)) {
        		currentAddressInformationRecord.field("mailDeliverySublocationIdentifier", characterContent);
        	} else if (STREET_NAME.equals(qName)) {
        		currentAddressInformationRecord.field("streetName", characterContent);
        	} else if (STREET_NAME_FORADDRESSING.equals(qName)) {
        		currentAddressInformationRecord.field("streetNameForAddressingName", characterContent);
        	} else if (STREET_BUILDING_IDENTIFIER.equals(qName)) {
        		currentAddressInformationRecord.field("streetBuildingIdentifier", characterContent);
        	} else if (FLOOR_IDENTIFIER.equals(qName)) {
        		currentAddressInformationRecord.field("floorIdentifier", characterContent);
        	} else if (SUITE_IDENTIFIER.equals(qName)) {
        		currentAddressInformationRecord.field("suiteIdentifier", characterContent);
        	} else if (DISTRICT_SUBDIVISION_IDENT.equals(qName)) {
        		currentAddressInformationRecord.field("districtSubdivisionIdentifier", characterContent);
        	} else if (POSTBOX_IDENTIFIER.equals(qName)) {
        		currentAddressInformationRecord.field("postOfficeBoxIdentifier", Long.valueOf(characterContent));
        	} else if (POSTCODE_IDENTIFIER.equals(qName)) {
        		currentAddressInformationRecord.field("postCodeIdentifier", Long.valueOf(characterContent));
        	} else if (DISTRICT_NAME.equals(qName)) {
        		currentAddressInformationRecord.field("districtName", characterContent);
        	} else if (COUNTRY_IDENT_CODE.equals(qName)) {
        		// TODO How to get attribute from element
        		currentAddressInformationRecord.field("countryIdentificationCode", characterContent);
        	}
        }
        if (currentVirtualInformationRecord != null) {
        	if (EMAIL_ADDRESS_IDENTIFIER.equals(qName)) {
        		currentVirtualInformationRecord.field("emailAddressIdentifier", characterContent);
        	} else if (WEBSITE.equals(qName)) {
        		currentVirtualInformationRecord.field("website", characterContent);
        	} else if (TELEPHONE_NUMBER_IDENTIFIER.equals(qName)) {
        		currentVirtualInformationRecord.field("telephoneNumberIdentifier", characterContent);
        	} else if (FAX_NUMBER_IDENTIFIER.equals(qName)) {
        		currentVirtualInformationRecord.field("faxNumberIdentifier", characterContent);
        	}
        }
        super.endElement(uri, localName, qName);
        
    }
    
    private void setBoolField(RecordBuilder rb, String fieldName, String value) 
    {
    	boolean f = Boolean.valueOf(value);
    	if (f)
    		rb.field(fieldName, "1");
    	else
    		rb.field(fieldName, "0");
    }
    
    private void persist(RecordBuilder rb, RecordSpecification specification) throws SAXException
    {
    	try {
    		persister.persist(rb.build(), specification);
    	} catch (SQLException e) {
    		throw new SAXException(e);
    	}
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        characterContent += new String(chars, start, length);
        super.characters(chars, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

}
