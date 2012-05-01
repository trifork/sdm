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


public class SorStatus extends SorNode {
//	private String fromDate;
//	private String toDate;
//	private String updatedAt;
//	private String firstFromDate;
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.SOR_STATUS);

	public SorStatus(Attributes attribs, SorNode parent) {
		super(attribs, parent);
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
			// Throw exception because we encountered an unexpected tag.
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in SorStatus");
		}
		return false;
	}
	
	public boolean recordDirty() {
		return true;
	}

	@Override
	public String toString() {
		return "SorStatus [builder=" + builder + ", toString()="
				+ super.toString() + "]";
	}
	
}
