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

public class HealthInstitution extends SorNode {
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.HEALTH_INSTITUTION_RECORD_TYPE);

	public HealthInstitution(Attributes attribs, SorNode parent,
			String parentTag) {
		super(attribs, parent, parentTag);
	}
	
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.HEALTH_INSTITUTION.equals(tagName)) {
			return true;
		}
    	if (SORXmlTagNames.HealthInstitution.SOR_IDENTIFIER.equals(tagName)) {
    		builder.field("sorIdentifier", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.HealthInstitution.ENTITY_NAME.equals(tagName)) {
			builder.field("entityName", tagValue);
		} else if (SORXmlTagNames.HealthInstitution.INSTITUTION_TYPE.equals(tagName)) {
			builder.field("institutionType", Long.valueOf(tagValue));
		}
		return false;
	}

	@Override
	public void persistCurrentNode(RecordPersister persister)
			throws SQLException {
		
	}

	@Override
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher)
			throws SQLException {
		
	}

}
