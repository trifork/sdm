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

import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.EanLocationCode;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.OrganizationalUnit;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorStatus;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorNode;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;

public class SORFullEventHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(SORFullEventHandler.class);
	private final RecordPersister persister;
	
    private String characterContent;

    /*private RecordBuilder currentInstitutionOwnerRecord;
    private SorStatus currentSorStatusRecord;
    private RecordBuilder currentLocatonCodeRecord;
    private RecordBuilder currentAddressInformationRecord;
    private RecordBuilder currentVirtualInformationRecord;*/
//    private RecordBuilder currentHealthInstitutionRecord;
//    private RecordBuilder currentOrganizationalUnitRecord;

    private SorNode currentNode;
    
	@Inject
	public
    SORFullEventHandler(RecordPersister persister)
    {
        this.persister = persister;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	characterContent = new String();
    	
//        if (SORXmlTagNames.INSTITUTION_OWNER.equals(qName)) {
//            currentInstitutionOwnerRecord = new RecordBuilder(SorFullRecordSpecs.INSTITUTION_OWNER);
//        }

    	// TODO Proper factory
    	if (SORXmlTagNames.SOR_STATUS.equals(qName)) {
    		currentNode = new SorStatus(atts, currentNode);
    	}
    	if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(qName)) {
    		currentNode = new EanLocationCode(atts, currentNode);
    	}
    	if (SORXmlTagNames.ORGANIZATIONAL_UNIT.equals(qName)) {
    		currentNode = new OrganizationalUnit(atts, currentNode);
    	}

        super.startElement(uri, localName, qName, atts);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentNode != null)
        {
        	if (currentNode.parseEndTag(qName, characterContent)) {
        		SorNode parent = currentNode.getParent();
        		
        		if (currentNode.isHasUniqueKey()) {
        			currentNode.updateDirty();
        			currentNode.persist(persister);
        		}
        		else if (parent != null) {
        			parent.addChild(currentNode);
        			currentNode = currentNode.getParent();
        		} 
        		if (parent == null) {
        			System.out.println(currentNode);
        		}
        		currentNode = parent;
        	}
        }
        super.endElement(uri, localName, qName);
    }
/*
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
    	}
    	if (qName.equals(SORXmlTagNames.SOR_STATUS)) {
    		setSorStatusOnRecord(currentInstitutionOwnerRecord);
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
    	if (SORXmlTagNames.VIRTUAL_ADDRESS_INFO.equals(qName)) {
    		persist(currentVirtualInformationRecord, SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
    		currentVirtualInformationRecord = null;
    		// TODO link current institionowner to this virtual info address somehow....
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
    	if (SORXmlTagNames.POSTAL_ADDRESS_INFO.equals(qName)) {
    		persist(currentAddressInformationRecord, SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);
    		currentAddressInformationRecord = null;
    	}
    }
    */
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
