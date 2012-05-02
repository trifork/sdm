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

public class EanLocationCode extends SorNode {

	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);

	public EanLocationCode(Attributes attribs, SorNode parent, String parentTag) {
		super(attribs, parent, parentTag);
		this.setHasUniqueKey(false);
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(tagName)) {
			return true;
    	}
		
		if (SORXmlTagNames.EanLocationCode.EAN_LOCATION_CODE.endsWith(tagName)) {
			builder.field("eanLocationCode", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.ONLY_INTERNAL_INDICATOR.endsWith(tagName)) {
    		boolean f = Boolean.valueOf(tagValue);
    		if (f)
    			builder.field("onlyInternalIndicator", "1");
    		else
    			builder.field("onlyInternalIndicator", "0");
    	} else if (SORXmlTagNames.EanLocationCode.NON_ACTIVITY_INDICATOR.endsWith(tagName)) {
    		boolean f = Boolean.valueOf(tagValue);
    		if (f)
    			builder.field("nonActiveIndicator", "1");
    		else
    			builder.field("nonActiveIndicator", "0");
    	} else if (SORXmlTagNames.EanLocationCode.SYSTEM_SUPPLIER.endsWith(tagName)) {
    		builder.field("systemSupplier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.SYSTEM_TYPE.endsWith(tagName)) {
    		builder.field("systemType", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.COMMUNICATION_SUPPLIER.endsWith(tagName)) {
    		builder.field("communicationSupplier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.REGION_CODE.endsWith(tagName)) {
    		builder.field("regionCode", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.EDI_ADMINISTRATOR.endsWith(tagName)) {
    		builder.field("ediAdministrator", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EanLocationCode.SOR_NOTE.endsWith(tagName)) {
    		builder.field("sorNote", tagValue);
    	}
		return false;
	}
	
	/**
	 * Must happen right after persist has been called on all children, to 
	 * make sure we insert an correct id
	 */
	private void updateForeignKeys() {
		for (SorNode node : children) {
			if (node.getClass() == SorStatus.class) {
				builder.field("fkSorStatus", ((SorStatus)node).getPID());
			}
		}
	}
	
	@Override
	public void persist(RecordPersister persister) throws SQLException {
		super.persist(persister);
		updateForeignKeys();
		persister.persist(builder.build(), SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);
	}
	
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher) {
	}
	
}