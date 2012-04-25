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

import com.sun.istack.SAXException2;
import com.trifork.stamdata.importer.jobs.sor.SORImporter;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.InstitutionOwner;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorStatus;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SORFullEventHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(SORFullEventHandler.class);
	
    private String characterContent;

    private final String INSTITUTION_OWNER_ENTITY = "InstitutionOwnerEntity";
    private final String INSTITUTION_OWNER = "InstitutionOwner";
    
    private final String SOR_IDENTIFIER = "SorIdentifier";
    private final String ENTITY_NAME = "EntityName";
    private final String SOR_STATUS = "SorStatus";
    
    // SorStatusType element children
    private final String FROM_DATE = "FromDate";
    private final String TO_DATE = "ToDate";
    private final String UPDATED_AT_DATE = "UpdatedAt";
    private final String FIRST_FROM_DATE = "FirstFromDate";
    
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DateFormatUtils.ISO_DATE_FORMAT.getPattern());

    private InstitutionOwner currentInstitutionOwner;
    private SorStatus currentSorStatus;
    
    private ArrayList<InstitutionOwner> institutionOwners;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	characterContent = new String();
    	
        if (qName.equals("SnapshotDate")) {

        }

        if (INSTITUTION_OWNER_ENTITY.equals(qName)) {
            institutionOwners = new ArrayList<InstitutionOwner>();
        }

        if (INSTITUTION_OWNER.equals(qName)) {
            currentInstitutionOwner = new InstitutionOwner();
            institutionOwners.add(currentInstitutionOwner);
        }
        
        if (currentInstitutionOwner != null) {
        	if (SOR_STATUS.equals(qName)) {
        		currentSorStatus = new SorStatus();
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
//        	logger.info("Institution parsed: " + currentInstitutionOwner);
//        	System.out.println("Institution parsed: " + currentInstitutionOwner);
        	currentInstitutionOwner = null;
        } else if (currentInstitutionOwner != null ) {
        	if (ENTITY_NAME.equals(qName)) {
        		currentInstitutionOwner.setEntityName(characterContent);
        	} else if (SOR_IDENTIFIER.equals(qName)) {
        		currentInstitutionOwner.setSorIdentifier(Long.valueOf(characterContent));
        	} else if (SOR_STATUS.equals(qName)) {
        		currentInstitutionOwner.setSorStatus(currentSorStatus);
        		currentSorStatus = null;
        	}
        }
        if (currentSorStatus != null) {
        	if (FROM_DATE.equals(qName)) {
        		currentSorStatus.setFromDate(parseISO8601Date(characterContent));
    		} else if (TO_DATE.equals(qName)) {
    			currentSorStatus.setToDate(parseISO8601Date(characterContent));
    		} else if (UPDATED_AT_DATE.equals(qName)) {
    			currentSorStatus.setUpdatedAt(parseISO8601Date(characterContent));
    		} else if (FIRST_FROM_DATE.equals(qName)) {
    			currentSorStatus.setFirstFromDate(parseISO8601Date(characterContent));
    		}
        }
        super.endElement(uri, localName, qName);
    }



	private Date parseISO8601Date(String strDate) throws SAXException {
		try {
			return dateFormat.parse(strDate);
		} catch (ParseException e) {
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
