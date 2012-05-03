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

import java.sql.SQLException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.trifork.stamdata.importer.jobs.sor.sor2.SORXmlTagNames;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

public class PostalAddressInformation extends SorNode {
	
	public enum CountryIdentificationScheme {
        iso3166_alpha2, iso3166_alpha3, un_numeric3, imk
    }
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);

    public PostalAddressInformation(Attributes attribs, SorNode parent, String parentTag) {
		super(attribs, parent, parentTag);
	}
    
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.ADDRESS_POSTAL.equals(tagName)) {
			return true;
		}
		
		if (SORXmlTagNames.PostalAddressInformation.STAIRWAY.equals(tagName)) {
    		builder.field("stairway", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.MAIL_DELIVERY_SUBLOC_IDENT.equals(tagName)) {
    		builder.field("mailDeliverySublocationIdentifier", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.STREET_NAME.equals(tagName)) {
    		builder.field("streetName", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.STREET_NAME_FORADDRESSING.equals(tagName)) {
    		builder.field("streetNameForAddressingName", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.STREET_BUILDING_IDENTIFIER.equals(tagName)) {
    		builder.field("streetBuildingIdentifier", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.FLOOR_IDENTIFIER.equals(tagName)) {
    		builder.field("floorIdentifier", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.SUITE_IDENTIFIER.equals(tagName)) {
    		builder.field("suiteIdentifier", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.DISTRICT_SUBDIVISION_IDENT.equals(tagName)) {
    		builder.field("districtSubdivisionIdentifier", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.POSTBOX_IDENTIFIER.equals(tagName)) {
    		builder.field("postOfficeBoxIdentifier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.PostalAddressInformation.POSTCODE_IDENTIFIER.equals(tagName)) {
    		builder.field("postCodeIdentifier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.PostalAddressInformation.DISTRICT_NAME.equals(tagName)) {
    		builder.field("districtName", tagValue);
    	} else if (SORXmlTagNames.PostalAddressInformation.COUNTRY_IDENT_CODE.equals(tagName)) {
    		builder.field("countryIdentificationCode", tagValue);
    	} else if (SORXmlTagNames.ADDRESS_POSTAL.equals(tagName)) {
    	} else {
			// Throw exception because we encountered an unexpected tag.
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in PostalAddressInformation");
		}
		return false;
	}
	
	@Override
	public void persistCurrentNode(RecordPersister persister) throws SQLException {
		Long id = persister.persist(builder.build(), SorFullRecordSpecs.POSTAL_ADDRESS_INFORMATION);
		if (id == null)
		{
			throw new SQLException("MySql did not respond with an Id of the row inserted in PostalAddressInformation table");
		}
		setPID(id);
	}

	@Override
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher)
			throws SQLException {
		
	}


}
