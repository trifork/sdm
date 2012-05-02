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
import com.trifork.stamdata.persistence.Record;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordFetcher;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.specs.SorFullRecordSpecs;


public class SorStatus extends SorNode {
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.SOR_STATUS);

	public SorStatus(Attributes attribs, SorNode parent, String parentTag) {
		super(attribs, parent, parentTag);
		this.setHasUniqueKey(false);
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.SOR_STATUS.equals(tagName)) {
			return true;
		}
    	if (SORXmlTagNames.FROM_DATE.equals(tagName)) {
    		builder.field("fromDate", tagValue);
		} else if (SORXmlTagNames.TO_DATE.equals(tagName)) {
			builder.field("toDate", tagValue);
		} else if (SORXmlTagNames.UPDATED_AT_DATE.equals(tagName)) {
			builder.field("toDate", tagValue);
		} else if (SORXmlTagNames.FIRST_FROM_DATE.equals(tagName)) {
			builder.field("firstFromDate", tagValue);
		} else {
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in SorStatus");
		}
		return false;
	}
	
	@Override
	public void persist(RecordPersister persister) throws SQLException {
		super.persist(persister);
		Long id = persister.persist(builder.build(), SorFullRecordSpecs.SOR_STATUS);
		if (id == null)
		{
			throw new SQLException("MySql did not respond with an Id of the row inserted in SorStatus table");
		}
		setPID(id);
	}
	
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher) throws SQLException {
		dirty = true;
		// We rely on primaryKey being set before this is called
		String updatedAtInXml = (String) builder.getFieldValue("updatedAt");
		if (getPID() != null) {
			Record fetched = fetcher.fetchCurrent(getPID().toString(), SorFullRecordSpecs.SOR_STATUS);
			if (fetched != null) {
				String updatedAtInDb = (String)fetched.get("updatedAt");
				// Both null so we are clean
				if (updatedAtInXml == null && updatedAtInDb == null) {
					dirty = false;
				// Updates have equal date so we are clean
				} else if (updatedAtInDb.equals(updatedAtInXml)) {
					dirty = false;
				}
			} else {
				throw new SQLException("Could not located Sor Status");
			}
		}
	}

	@Override
	public String toString() {
		return "SorStatus [builder=" + builder + ", toString()="
				+ super.toString() + "]";
	}
	
}
