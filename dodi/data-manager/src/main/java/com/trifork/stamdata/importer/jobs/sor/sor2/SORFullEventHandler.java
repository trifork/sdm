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

import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
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
    
/*    // VirtualAddressInformation children
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
*/    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());

/*    private InstitutionOwner currentInstitutionOwner;
    private SorStatus currentSorStatus;
    private EanLocationCode currentLocationCode;
    private AddressInformation currentAddress;
    private VirtualAddressInformation currentVirtualInformation;*/
    private RecordBuilder currentInstitutionOwnerRecord = new RecordBuilder(SorFullRecordSpecs.INSTITUTIONS_EJER);
    private RecordBuilder currentSorStatusRecord = new RecordBuilder(SorFullRecordSpecs.SOR_STATUS);
    
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
        	/*if (EAN_LOCATION_CODE_ENTITY.equals(qName)) {
        		currentLocationCode = new EanLocationCode();
        	}
        	if (POSTAL_ADDRESS_INFO.equals(qName)) {
        		currentAddress = new AddressInformation();
        	}
        	if (VIRTUAL_ADDRESS_INFO.equals(qName)) {
        		currentVirtualInformation = new VirtualAddressInformation();
        	}*/
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
        		// currentInstitutionOwner.setEanLocationCode(currentLocationCode);
        		// currentInstitutionOwner = null;
        		// TODO FIXME
        	} else if (POSTAL_ADDRESS_INFO.equals(qName)) {
        		// currentInstitutionOwner.setPostalAddressInformation(currentAddress);
        		// currentAddress = null;
        		// TODO FIXME
        	} else if (VIRTUAL_ADDRESS_INFO.equals(qName)) {
        		// currentInstitutionOwner.setVirtualAddressInformation(currentVirtualInformation);
        		// currentVirtualInformation = null;
        		// TODO FIXME
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
        /*if (currentLocationCode != null) {
        	if (EAN_LOCATION_CODE.endsWith(qName)) {
        		currentLocationCode.setEanLocationCode(Long.valueOf(characterContent));
        	} else if (ONLY_INTERNAL_INDICATOR.endsWith(qName)) {
        		currentLocationCode.setOnlyInternalIndicator(Boolean.valueOf(characterContent));
        	} else if (NON_ACTIVITY_INDICATOR.endsWith(qName)) {
        		currentLocationCode.setNonActiveIndicator(Boolean.valueOf(characterContent));
        	} else if (SYSTEM_SUPPLIER.endsWith(qName)) {
        		currentLocationCode.setSystemSupplier(Long.valueOf(characterContent));
        	} else if (SYSTEM_TYPE.endsWith(qName)) {
        		currentLocationCode.setSystemType(Long.valueOf(characterContent));
        	} else if (COMMUNICATION_SUPPLIER.endsWith(qName)) {
        		currentLocationCode.setCommunicationSupplier(Long.valueOf(characterContent));
        	} else if (REGION_CODE.endsWith(qName)) {
        		currentLocationCode.setRegionCode(Long.valueOf(characterContent));
        	} else if (EDI_ADMINISTRATOR.endsWith(qName)) {
        		currentLocationCode.setEdiAdministrator(Long.valueOf(characterContent));
        	} else if (SOR_NOTE.endsWith(qName)) {
        		currentLocationCode.setSorNote(characterContent);
        	} else if (SOR_STATUS.equals(qName)) {
        		currentLocationCode.setSorStatus(currentSorStatus);
        		currentSorStatus = null;
        	}
        }
        if (currentAddress != null) {
        	if (STAIRWAY.equals(qName)) {
        		currentAddress.setStairway(characterContent);
        	} else if (MAIL_DELIVERY_SUBLOC_IDENT.equals(qName)) {
        		currentAddress.setMailDeliverySublocationIdentifier(characterContent);
        	} else if (STREET_NAME.equals(qName)) {
        		currentAddress.setStreetName(characterContent);
        	} else if (STREET_NAME_FORADDRESSING.equals(qName)) {
        		currentAddress.setStreetNameForAddressingName(characterContent);
        	} else if (STREET_BUILDING_IDENTIFIER.equals(qName)) {
        		currentAddress.setStreetBuildingIdentifier(characterContent);
        	} else if (FLOOR_IDENTIFIER.equals(qName)) {
        		currentAddress.setFloorIdentifier(characterContent);
        	} else if (SUITE_IDENTIFIER.equals(qName)) {
        		currentAddress.setSuiteIdentifier(characterContent);
        	} else if (DISTRICT_SUBDIVISION_IDENT.equals(qName)) {
        		currentAddress.setDistrictSubdivisionIdentifier(characterContent);
        	} else if (POSTBOX_IDENTIFIER.equals(qName)) {
        		currentAddress.setPostOfficeBoxIdentifier(Integer.valueOf(characterContent));
        	} else if (POSTCODE_IDENTIFIER.equals(qName)) {
        		currentAddress.setPostCodeIdentifier(Integer.valueOf(characterContent));
        	} else if (DISTRICT_NAME.equals(qName)) {
        		currentAddress.setDistrictName(characterContent);
        	} else if (COUNTRY_IDENT_CODE.equals(qName)) {
        		// TODO How to get attribute from element
        	}
        }
        if (currentVirtualInformation != null) {
        	if (EMAIL_ADDRESS_IDENTIFIER.equals(qName)) {
        		currentVirtualInformation.setEmailAddressIdentifier(characterContent);
        	} else if (WEBSITE.equals(qName)) {
        		currentVirtualInformation.setWebsite(characterContent);
        	} else if (TELEPHONE_NUMBER_IDENTIFIER.equals(qName)) {
        		currentVirtualInformation.setTelephoneNumberIdentifier(characterContent);
        	} else if (FAX_NUMBER_IDENTIFIER.equals(qName)) {
        		currentVirtualInformation.setFaxNumberIdentifier(characterContent);
        	}
        }*/
        super.endElement(uri, localName, qName);
        
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
