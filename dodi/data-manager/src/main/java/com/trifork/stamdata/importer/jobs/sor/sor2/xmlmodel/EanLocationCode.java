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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.trifork.stamdata.importer.jobs.sor.sor2.SORXmlTagNames;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

public class EanLocationCode extends SorNode {
//	private long eanLocationCode;
//	private boolean onlyInternalIndicator;
//	private boolean nonActiveIndicator;
//	private long systemSupplier;
//	private long systemType;
//	private long communicationSupplier;
//	private long regionCode;
//	private long ediAdministrator;
//	private String sorNote;
	
	private Long primaryKey;
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.EAN_LOCATION_CODE_ENTITY);

	public EanLocationCode(Attributes attribs, SorNode parent) {
		super(attribs, parent);
		this.setHasUniqueKey(false);
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.EAN_LOCATION_CODE_ENTITY.equals(tagName)) {
			return true;
    	}
		
		if (SORXmlTagNames.EAN_LOCATION_CODE.endsWith(tagName)) {
			builder.field("eanLocationCode", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.ONLY_INTERNAL_INDICATOR.endsWith(tagName)) {
    		boolean f = Boolean.valueOf(tagValue);
    		if (f)
    			builder.field("onlyInternalIndicator", "1");
    		else
    			builder.field("onlyInternalIndicator", "0");
    	} else if (SORXmlTagNames.NON_ACTIVITY_INDICATOR.endsWith(tagName)) {
    		boolean f = Boolean.valueOf(tagValue);
    		if (f)
    			builder.field("nonActiveIndicator", "1");
    		else
    			builder.field("nonActiveIndicator", "0");
    	} else if (SORXmlTagNames.SYSTEM_SUPPLIER.endsWith(tagName)) {
    		builder.field("systemSupplier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.SYSTEM_TYPE.endsWith(tagName)) {
    		builder.field("systemType", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.COMMUNICATION_SUPPLIER.endsWith(tagName)) {
    		builder.field("communicationSupplier", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.REGION_CODE.endsWith(tagName)) {
    		builder.field("regionCode", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.EDI_ADMINISTRATOR.endsWith(tagName)) {
    		builder.field("ediAdministrator", Long.valueOf(tagValue));
    	} else if (SORXmlTagNames.SOR_NOTE.endsWith(tagName)) {
    		builder.field("sorNote", tagValue);
    	}
		return false;
	}
	
	public void setPrimaryKey(Long key) {
		primaryKey = key;
	}
	
	public Long getPrimaryKey() {
		return primaryKey;
	}
		
	public boolean recordDirty() {
		return true;
	}

	@Override
	public String toString() {
		return "EanLocationCode [primaryKey=" + primaryKey + ", builder="
				+ builder + ", toString()=" + super.toString() + "]";
	}
	
}
