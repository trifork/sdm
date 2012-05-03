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

public class ClinicalSpeciality extends SorNode {
	
	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.CLINICAL_SPECIALITY);

	public ClinicalSpeciality(Attributes attribs, SorNode parent, String parentTag) {
		super(attribs, parent, parentTag);
		persistDependsOnParent = true;
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.CLINICAL_SPECIALITY.equals(tagName)) {
			return true;
		}
    	if (SORXmlTagNames.ClinicalSpeciality.SPECIALITY_CODE.equals(tagName)) {
    		builder.field("specialityCode", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.ClinicalSpeciality.SPECIALITY_TYPE.equals(tagName)) {
			builder.field("specialityType", Long.valueOf(tagValue));
		} else {
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in SorStatus");
		}
		return false;
	}
	
	public void setOrganizationalOwner(Long ownerPID) {
		builder.field("fkOrganizationalUnit", ownerPID);
	}
	
	@Override
	public void persistCurrentNode(RecordPersister persister) throws SQLException {
		Long id = persister.persist(builder.build(), SorFullRecordSpecs.CLINICAL_SPECIALITY);
		if (id == null)
		{
			throw new SQLException("MySql did not respond with an Id of the row inserted in ClinicalSpeciality table");
		}
		setPID(id);
	}

	@Override
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher)
			throws SQLException {
		
	}

}
