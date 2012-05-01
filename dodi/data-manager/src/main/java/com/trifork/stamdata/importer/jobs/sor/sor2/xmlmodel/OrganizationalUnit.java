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
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SorFullRecordSpecs;

public class OrganizationalUnit extends SorNode {

	private RecordBuilder builder = new RecordBuilder(SorFullRecordSpecs.ORGANIZATIONAL_UNIT);

	public OrganizationalUnit(Attributes attribs, SorNode parent) {
		super(attribs, parent);
		this.setHasUniqueKey(true);
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.ORGANIZATIONAL_UNIT.equals(tagName)) {
			return true;
		}
    	if (SORXmlTagNames.OrganizationalUnit.SOR_IDENTIFIER.equals(tagName)) {
    		builder.field("sorIdentifier", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.OrganizationalUnit.ENTITY_NAME.equals(tagName)) {
			builder.field("entityName", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.UNIT_TYPE.equals(tagName)) {
			builder.field("unitType", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_CODE.equals(tagName)) {
			builder.field("localCode", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.PHARMACY_IDENTIFIER.equals(tagName)) {
			builder.field("pharmacyIdentifier", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.SHAK_IDENTIFIER.equals(tagName)) {
			builder.field("shakIdentifier", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.PROVIDER_IDENTIFIER.equals(tagName)) {
			builder.field("providerIdentifier", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.OPTIONAL_EAN_LOCATION_CODE.equals(tagName)) {
		} else if (SORXmlTagNames.OrganizationalUnit.EAN_ENTITY_INHERITED_INDICATOR.equals(tagName)) {
			boolean f = Boolean.valueOf(tagValue);
			if (f)
				builder.field("eanLocationCodeInheritedIndicator", "1");
			else
				builder.field("eanLocationCodeInheritedIndicator", "0");
		} else if (SORXmlTagNames.OrganizationalUnit.GEOGRAPHICAL_PARENT.equals(tagName)) {
		} else if (SORXmlTagNames.OrganizationalUnit.GEOGRAPHICAL_PARENT_RELATION.equals(tagName)) {
			builder.field("geographicalParentRelation", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.OrganizationalUnit.GEOGRAPHICAL_PARENT_SOR_IDENTIFIER.equals(tagName)) {
			builder.field("geographicalParentSorIdentifier", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.OrganizationalUnit.POSTAL_ADDRESS_INFO.equals(tagName)) {
			// TODO
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.VISITING_ADDRESS_INFO.equals(tagName)) {
			// TODO
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.ACTIVITY_ADDRESS_INFO.equals(tagName)) {
			// TODO
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.VIRTUAL_ADDRESS_INFO.equals(tagName)) {
			// TODO
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.CLINICAL_SPECIALITY_COLLECTION.equals(tagName)) {
			// TODO
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.SOR_STATUS.equals(tagName)) {
			builder.field("fkSorStatus", Long.valueOf(tagValue));
		} else if (SORXmlTagNames.OrganizationalUnit.REPLACES_ENTITY_COLLECTION.equals(tagName)) {
			// TODO fkReplacesSorCollection
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.REPLACED_BY_ENTITY_COLLECTION.equals(tagName)) {
			// TODO fkReplacedByCollection
			System.out.println("TODO : " + tagName);
		} else if (SORXmlTagNames.OrganizationalUnit.AMBULANT_ACTIVITY_INDICATOR.equals(tagName)) {
			boolean f = Boolean.valueOf(tagValue);
			if (f)
				builder.field("ambulantActivityIndicator", "1");
			else
				builder.field("ambulantActivityIndicator", "0");
		} else if (SORXmlTagNames.OrganizationalUnit.PATIENTS_ADMITTED_INDICATOR.equals(tagName)) {
			boolean f = Boolean.valueOf(tagValue);
			if (f)
				builder.field("patientsAdmittedIndicator", "1");
			else
				builder.field("patientsAdmittedIndicator", "0");
		} else if (SORXmlTagNames.OrganizationalUnit.REPORTING_LEVEL_INDICATOR.equals(tagName)) {
			boolean f = Boolean.valueOf(tagValue);
			if (f)
				builder.field("reportingLevelIndicator", "1");
			else
				builder.field("reportingLevelIndicator", "0");
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_ATTRBIBUTE1.equals(tagName)) {
			builder.field("localAttribute1", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_ATTRBIBUTE2.equals(tagName)) {
			builder.field("localAttribute2", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_ATTRBIBUTE3.equals(tagName)) {
			builder.field("localAttribute3", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_ATTRBIBUTE4.equals(tagName)) {
			builder.field("localAttribute4", tagValue);
		} else if (SORXmlTagNames.OrganizationalUnit.LOCAL_ATTRBIBUTE5.equals(tagName)) {
			builder.field("localAttribute5", tagValue);
		} else {
			// TODO back in when sub nodes are implemented
			//throw new SAXException("Encountered an unexpected tag '" + tagName + "' in SorStatus");
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
		setPID(persister.persist(builder.build(), SorFullRecordSpecs.ORGANIZATIONAL_UNIT));
		
		SorNode parent = getParent();
		// Update parent organizational units to point to us
		if (parent != null && parent.getClass() == OrganizationalUnit.class) {
			((OrganizationalUnit)parent).builder.field("fkOrganazationalChildUnit", getPID());
		}
	}
	
	public void compareAgainstDatabaseAndUpdateDirty(RecordFetcher fetcher) throws SQLException {
		// Always set dirty, and reset below if we are sure we are not updated
		dirty = true;
		Record fetched = fetcher.fetchCurrent(builder.getFieldValue("sorIdentifier").toString(), SorFullRecordSpecs.ORGANIZATIONAL_UNIT);
		if (fetched != null) {
			Object sorStatusId = fetched.get("fkSorStatus");
			for (SorNode node : children) {
				if (node.getClass() == SorStatus.class) {
					SorStatus status = (SorStatus)node;
					status.setPID((Long)sorStatusId);
					status.compareAgainstDatabaseAndUpdateDirty(fetcher);
					if (!status.dirty) {
						dirty = false;
					}
				}
			}
		}
		for (SorNode node : children) {
			node.dirty = dirty;
		}
		if (dirty)
			System.out.println("Record dirty");
		else
			System.out.println("Record clean");
	}

	public boolean recordDirty() {
		return true;
	}
	
	@Override
	public String toString() {
		return "OrganizationalUnit [builder=" + builder + ", toString()="
				+ super.toString() + "]";
	}

}
