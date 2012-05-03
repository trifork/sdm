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

public class VirtualAddressInformation extends SorNode {
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
	
	public VirtualAddressInformation(Attributes attribs, SorNode parent, String parentTag) {
		super(attribs, parent, parentTag);
	}

	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.VIRTUAL_ADDRESS_INFO.equals(tagName)) {
			return true;
		}
		
		if (SORXmlTagNames.VirtualAddressInformation.EMAIL_ADDRESS_IDENTIFIER.equals(tagName)) {
    		builder.field("emailAddressIdentifier", tagValue);
    	} else if (SORXmlTagNames.VirtualAddressInformation.WEBSITE.equals(tagName)) {
    		builder.field("website", tagValue);
    	} else if (SORXmlTagNames.VirtualAddressInformation.TELEPHONE_NUMBER_IDENTIFIER.equals(tagName)) {
    		builder.field("telephoneNumberIdentifier", tagValue);
    	} else if (SORXmlTagNames.VirtualAddressInformation.FAX_NUMBER_IDENTIFIER.equals(tagName)) {
    		builder.field("faxNumberIdentifier", tagValue);
    	} else {
			// Throw exception because we encountered an unexpected tag.
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in VirtualAddressInformation");
		}
		return false;
	}
	
	@Override
	public void persistCurrentNode(RecordPersister persister) throws SQLException {
		Long id = persister.persist(builder.build(), SorFullRecordSpecs.VIRTUAL_ADDRESS_INFORMATION);
		if (id == null)
		{
			throw new SQLException("MySql did not respond with an Id of the row inserted in VirtualAddressInformation table");
		}
		setPID(id);
	}
	
	@Override
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher)
			throws SQLException {
	}
	
}
