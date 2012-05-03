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
	}
	
	@Override
	public boolean parseEndTag(String tagName, String tagValue) throws SAXException {
		if (SORXmlTagNames.CLINICAL_SPECIALITY.equals(tagName)) {
			return true;
		}
    	if (SORXmlTagNames.ClinicalSpeciality.SPECIALITY_CODE.equals(tagName)) {
    		builder.field("specialityCode", tagValue);
		} else if (SORXmlTagNames.ClinicalSpeciality.SPECIALITY_TYPE.equals(tagName)) {
			builder.field("specialityType", tagValue);
		} else {
			throw new SAXException("Encountered an unexpected tag '" + tagName + "' in SorStatus");
		}
		return false;
	}
	
	@Override
	public void persist(RecordPersister persister) throws SQLException {
		super.persist(persister);
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
