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
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.PostalAddressInformation;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorStatus;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.SorNode;
import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.VirtualAddressInformation;
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.RecordPersister;

public class SORFullEventHandler extends DefaultHandler {

	private static final Logger logger = Logger.getLogger(SORFullEventHandler.class);
	
	private final RecordFetcher fetcher;
	private final RecordPersister persister;
	
    private String characterContent;
    private String lastTag;

    private SorNode currentNode;
    
	@Inject
	public
    SORFullEventHandler(RecordPersister persister, RecordFetcher fetcher)
    {
        this.persister = persister;
        this.fetcher = fetcher;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
    	characterContent = new String();

    	// TODO Proper factory
    	if (SORXmlTagNames.SOR_STATUS.equals(qName)) 
    	{
    		currentNode = new SorStatus(atts, currentNode, lastTag);
    	} 
    	else if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(qName)) 
    	{
    		currentNode = new EanLocationCode(atts, currentNode, lastTag);
    	} 
    	else if (SORXmlTagNames.ORGANIZATIONAL_UNIT_ENTITY.equals(qName)) 
    	{
    		currentNode = new OrganizationalUnit(atts, currentNode, lastTag);
    	} 
    	else if (SORXmlTagNames.VIRTUAL_ADDRESS_INFO.equals(qName)) 
    	{
    		currentNode = new VirtualAddressInformation(atts, currentNode, lastTag);
    	}
    	else if (SORXmlTagNames.ADDRESS_POSTAL.equals(qName)) 
    	{
    		currentNode = new PostalAddressInformation(atts, currentNode, lastTag);
    	}
    	
    	lastTag = qName;
        super.startElement(uri, localName, qName, atts);
    }
    
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentNode != null)
        {
        	if (currentNode.parseEndTag(qName, characterContent)) {
        		SorNode parent = currentNode.getParentNode();
        		
        		if (currentNode.isUniqueKey()) {
        			try {
        				currentNode.compareAgainstDatabaseAndUpdateDirty(fetcher);
						currentNode.persist(persister);
						if (parent != null) {
							parent.removeChild(currentNode);
						}
					} catch (SQLException e) {
						throw new SAXException(e);
					}
        		}
        		else if (parent != null) {
        			parent.addChild(currentNode);
        			currentNode = currentNode.getParentNode();
        		}
        		currentNode = parent;
        	}
        }
        super.endElement(uri, localName, qName);
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
