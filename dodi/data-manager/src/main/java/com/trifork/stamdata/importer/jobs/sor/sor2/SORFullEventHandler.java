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

import java.sql.SQLException;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorStatus;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

public class SORFullEventHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(SORFullEventHandler.class);
	private final RecordPersister persister;
	
    private String characterContent;

    private RecordBuilder currentInstitutionOwnerRecord;
    private SorStatus currentSorStatusRecord;
    private RecordBuilder currentLocatonCodeRecord;
    private RecordBuilder currentAddressInformationRecord;
    private RecordBuilder currentVirtualInformationRecord;
    private RecordBuilder currentHealthInstitutionRecord;
    private RecordBuilder currentOrganizationalUnitRecord;
    
	@Inject
	public
    SORFullEventHandler(RecordPersister persister)
    {
        this.persister = persister;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	characterContent = new String();
    	
        if (SORXmlTagNames.INSTITUTION_OWNER.equals(qName)) {
            currentInstitutionOwnerRecord = new RecordBuilder(SorFullRecordSpecs.INSTITUTION_OWNER);
        }
        
        if (currentInstitutionOwnerRecord != null) {
        	if (SORXmlTagNames.SOR_STATUS.equals(qName)) {
        		currentSorStatusRecord = new SorStatus();
        	}
        	if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(qName)) {
        		currentLocatonCodeRecord = new RecordBuilder(SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
        	}
        	if (SORXmlTagNames.ORGANIZATIONAL_UNIT.equals(qName)) {
//        		currentOrganizationalUnitRecord = new RecordBuilder(SorFullRecordSpecs.ORGANIZATIONAL_UNIT);
        	}
        	if (SORXmlTagNames.HEALTH_INSTITUTION.equals(qName)) {
//        		currentHealthInstitutionRecord = new RecordBuilder(SorFullRecordSpecs.HEALTH_INSTITUTION_RECORD_TYPE);
        	}
        	if (SORXmlTagNames.POSTAL_ADDRESS_INFO.equals(qName)) {
        		currentAddressInformationRecord = new RecordBuilder(SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION); 
        	}
        	if (SORXmlTagNames.VIRTUAL_ADDRESS_INFO.equals(qName)) {
        		currentVirtualInformationRecord = new RecordBuilder(SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
        	}
        	if (SORXmlTagNames.COUNTRY_IDENT_CODE.equals(qName) && currentAddressInformationRecord != null) {
        		String scheme = atts.getValue("scheme");
        		currentAddressInformationRecord.field("countryIdentificationCodeScheme", SORFullEventHandler.countrySchemeStringToInt(scheme));
        	}
        }
        //

        super.startElement(uri, localName, qName, atts);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("SnapshotDate")) {
        	//
        }

        if (SORXmlTagNames.INSTITUTION_OWNER.equals(qName)) {
        	persist(currentInstitutionOwnerRecord, SorFullRecordSpecs.INSTITUTION_OWNER);
        	currentInstitutionOwnerRecord = null;
        } else if (SORXmlTagNames.HEALTH_INSTITUTION.equals(qName)) {
            // Persist health institution
/*        } else if (currentHealthInstitutionRecord != null) {
        	parseHealthInstitutionRecordEndTag(qName);
        } else if (currentOrganizationalUnitRecord != null ) {
        	parseOrganizationalRecordEndTag(qName);*/
        } else if (currentLocatonCodeRecord != null) {
        	parseLocationCodeRecordEndTag(qName);
        } else if (currentInstitutionOwnerRecord != null ) {
        	parseInstitutionOwnerRecordEndTag(qName);
        }
        
       
        if (currentAddressInformationRecord != null) {
        	parseCurrentAddressEndTag(qName);
        }
        if (currentVirtualInformationRecord != null) {
        	parseVirtualRecordEndTag(qName);
        }
        super.endElement(uri, localName, qName);
    }

	private void setSorStatusOnRecord(RecordBuilder recordBuilder) {
		recordBuilder.field(SorFullRecordSpecs.FIRST_FROM_DATE, currentSorStatusRecord.getFirstFromDate());
		recordBuilder.field(SorFullRecordSpecs.FROM_DATE, currentSorStatusRecord.getFromDate());
		recordBuilder.field(SorFullRecordSpecs.TO_DATE, currentSorStatusRecord.getToDate());
		recordBuilder.field(SorFullRecordSpecs.UPDATED_AT,currentSorStatusRecord.getUpdatedAt());
		currentSorStatusRecord = null;
	}


    
    
    private void parseSorStatusRecordEndTag(String qName) throws SAXException {
    	if (SORXmlTagNames.FROM_DATE.equals(qName)) {
    		currentSorStatusRecord.setFromDate(characterContent);
		} else if (SORXmlTagNames.TO_DATE.equals(qName)) {
			currentSorStatusRecord.setToDate(characterContent);
		} else if (SORXmlTagNames.UPDATED_AT_DATE.equals(qName)) {
			currentSorStatusRecord.setUpdatedAt(characterContent);
		} else if (SORXmlTagNames.FIRST_FROM_DATE.equals(qName)) {
			currentSorStatusRecord.setFirstFromDate(characterContent);
		}
    }
    
    private void parseInstitutionOwnerRecordEndTag(String qName) throws SAXException {
    	if (SORXmlTagNames.ENTITY_NAME.equals(qName)) {
    		currentInstitutionOwnerRecord.field("entityName", characterContent);
    	} else if (SORXmlTagNames.SOR_IDENTIFIER.equals(qName)) {
    		currentInstitutionOwnerRecord.field("sorIdentifier", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.OWNER_TYPE.equals(qName)) {
    		currentInstitutionOwnerRecord.field("ownerType", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.SOR_STATUS.equals(qName)) {
    		// currentInstitutionOwner.setSorStatus(currentSorStatus);
    		// currentSorStatus = null;
    		// TODO FIXME
    	} else if (SORXmlTagNames.POSTAL_ADDRESS_INFO.equals(qName)) {
    		persist(currentAddressInformationRecord, SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);
    		currentAddressInformationRecord = null;
    		// TODO link current institionowner to this postal address somehow....
    	} else if (SORXmlTagNames.VIRTUAL_ADDRESS_INFO.equals(qName)) {
    		persist(currentVirtualInformationRecord, SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
    		currentVirtualInformationRecord = null;
    		// TODO link current institionowner to this virtual info address somehow....
    	}
    	if (qName.equals(SORXmlTagNames.SOR_STATUS)) {
    		setSorStatusOnRecord(currentInstitutionOwnerRecord);
    	}
    	if (currentSorStatusRecord != null) {
        	parseSorStatusRecordEndTag(qName);
        }
    }
    
    private void parseHealthInstitutionRecordEndTag(String qName) throws SAXException {
    	//
    }
    
    private void parseOrganizationalRecordEndTag(String qName) throws SAXException {
    	//
    }
    
    private void parseLocationCodeRecordEndTag(String qName) throws SAXException {
    	if (SORXmlTagNames.EAN_LOCATION_CODE.endsWith(qName)) {
    		currentLocatonCodeRecord.field("eanLocationCode", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.ONLY_INTERNAL_INDICATOR.endsWith(qName)) {
    		setBoolField(currentLocatonCodeRecord, "onlyInternalIndicator", characterContent);
    	} else if (SORXmlTagNames.NON_ACTIVITY_INDICATOR.endsWith(qName)) {
    		setBoolField(currentLocatonCodeRecord, "nonActiveIndicator", characterContent);
    	} else if (SORXmlTagNames.SYSTEM_SUPPLIER.endsWith(qName)) {
    		currentLocatonCodeRecord.field("systemSupplier", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.SYSTEM_TYPE.endsWith(qName)) {
    		currentLocatonCodeRecord.field("systemType", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.COMMUNICATION_SUPPLIER.endsWith(qName)) {
    		currentLocatonCodeRecord.field("communicationSupplier", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.REGION_CODE.endsWith(qName)) {
    		currentLocatonCodeRecord.field("regionCode", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.EDI_ADMINISTRATOR.endsWith(qName)) {
    		currentLocatonCodeRecord.field("ediAdministrator", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.SOR_NOTE.endsWith(qName)) {
    		currentLocatonCodeRecord.field("sorNote", characterContent);
    	} else if (SORXmlTagNames.SOR_STATUS.equals(qName)) {
    		//currentLocationCode.setSorStatus(currentSorStatus);
    		//currentSorStatus = null;
    	}
    	if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(qName)) {
    		persist(currentLocatonCodeRecord, SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
    		currentLocatonCodeRecord = null;
    	}
    	if (qName.equals(SORXmlTagNames.SOR_STATUS)) {
    		setSorStatusOnRecord(currentLocatonCodeRecord);
    	}
    	if (currentSorStatusRecord != null) {
        	parseSorStatusRecordEndTag(qName);
        }
    }
    
    private void parseVirtualRecordEndTag(String qName) throws SAXException {
    	if (SORXmlTagNames.EMAIL_ADDRESS_IDENTIFIER.equals(qName)) {
    		currentVirtualInformationRecord.field("emailAddressIdentifier", characterContent);
    	} else if (SORXmlTagNames.WEBSITE.equals(qName)) {
    		currentVirtualInformationRecord.field("website", characterContent);
    	} else if (SORXmlTagNames.TELEPHONE_NUMBER_IDENTIFIER.equals(qName)) {
    		currentVirtualInformationRecord.field("telephoneNumberIdentifier", characterContent);
    	} else if (SORXmlTagNames.FAX_NUMBER_IDENTIFIER.equals(qName)) {
    		currentVirtualInformationRecord.field("faxNumberIdentifier", characterContent);
    	}
    }
    
    private void parseCurrentAddressEndTag(String qName) throws SAXException {
    	if (SORXmlTagNames.STAIRWAY.equals(qName)) {
    		currentAddressInformationRecord.field("stairway", characterContent);
    	} else if (SORXmlTagNames.MAIL_DELIVERY_SUBLOC_IDENT.equals(qName)) {
    		currentAddressInformationRecord.field("mailDeliverySublocationIdentifier", characterContent);
    	} else if (SORXmlTagNames.STREET_NAME.equals(qName)) {
    		currentAddressInformationRecord.field("streetName", characterContent);
    	} else if (SORXmlTagNames.STREET_NAME_FORADDRESSING.equals(qName)) {
    		currentAddressInformationRecord.field("streetNameForAddressingName", characterContent);
    	} else if (SORXmlTagNames.STREET_BUILDING_IDENTIFIER.equals(qName)) {
    		currentAddressInformationRecord.field("streetBuildingIdentifier", characterContent);
    	} else if (SORXmlTagNames.FLOOR_IDENTIFIER.equals(qName)) {
    		currentAddressInformationRecord.field("floorIdentifier", characterContent);
    	} else if (SORXmlTagNames.SUITE_IDENTIFIER.equals(qName)) {
    		currentAddressInformationRecord.field("suiteIdentifier", characterContent);
    	} else if (SORXmlTagNames.DISTRICT_SUBDIVISION_IDENT.equals(qName)) {
    		currentAddressInformationRecord.field("districtSubdivisionIdentifier", characterContent);
    	} else if (SORXmlTagNames.POSTBOX_IDENTIFIER.equals(qName)) {
    		currentAddressInformationRecord.field("postOfficeBoxIdentifier", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.POSTCODE_IDENTIFIER.equals(qName)) {
    		currentAddressInformationRecord.field("postCodeIdentifier", Long.valueOf(characterContent));
    	} else if (SORXmlTagNames.DISTRICT_NAME.equals(qName)) {
    		currentAddressInformationRecord.field("districtName", characterContent);
    	} else if (SORXmlTagNames.COUNTRY_IDENT_CODE.equals(qName)) {
    		currentAddressInformationRecord.field("countryIdentificationCode", characterContent);
    	}
    }
    
    public static long countrySchemeStringToInt(String schemeName) throws SAXException
    {
    	if (schemeName.equals("iso3166-alpha2")) {
    		return 0;
    	} else if (schemeName.equals("iso3166-alpha3")) {
    		return 1;
    	} else if (schemeName.equals("un-numeric3")) {
    		return 2;
    	} else if (schemeName.equals("imk")) {
    		return 3;
    	} else {
    		throw new SAXException("Unrecognized country scheme value");
    	}
    }
    
    private void setBoolField(RecordBuilder rb, String fieldName, String value) 
    {
    	boolean f = Boolean.valueOf(value);
    	if (f)
    		rb.field(fieldName, "1");
    	else
    		rb.field(fieldName, "0");
    }
    
    private Long persist(RecordBuilder rb, RecordSpecification specification) throws SAXException
    {
    	try {
    		return persister.persist(rb.build(), specification);
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
