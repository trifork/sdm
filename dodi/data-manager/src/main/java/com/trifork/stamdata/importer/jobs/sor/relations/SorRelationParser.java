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
package com.trifork.stamdata.importer.jobs.sor.relations;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import oio.sundhedsstyrelsen.organisation._1_0.HealthInstitutionEntityType;
import oio.sundhedsstyrelsen.organisation._1_0.HealthInstitutionType;
import oio.sundhedsstyrelsen.organisation._1_0.InstitutionOwnerEntityType;
import oio.sundhedsstyrelsen.organisation._1_0.InstitutionOwnerType;
import oio.sundhedsstyrelsen.organisation._1_0.OrganizationalUnitEntityType;
import oio.sundhedsstyrelsen.organisation._1_0.OrganizationalUnitType;
import oio.sundhedsstyrelsen.organisation._1_0.SorStatusType;
import oio.sundhedsstyrelsen.organisation._1_0.SorTreeType;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.joda.time.DateTime;
import org.mortbay.log.Log;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordBuilder;
import com.trifork.stamdata.persistence.RecordPersister;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SorRelationsRecordSpecs;

import dk.sdsd.nsp.slalog.api.SLALogItem;

@ParserInformation(id = "sorRelation", name = "SorRelation")
public class SorRelationParser implements Parser {

    private final RecordSpecification recordSpecification;
    private final RecordSpecification shakYderSpecification;
    private final KeyValueStore keyValueStore;
    private static Logger logger = Logger.getLogger(SorRelationParser.class);
    private Map<String, String> selfRelationsMap;
    private Map<String, HashSet<String>> shakYderMap;
    private DateTime snapshotDate;

    @Inject
    SorRelationParser(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
        this.recordSpecification = SorRelationsRecordSpecs.RELATIONS_RECORD_SPEC;
        this.shakYderSpecification = SorRelationsRecordSpecs.SHAK_YDER_RECORD_SPEC;
    }
    
    @Override
    public void process(File dataSet, RecordPersister persister) throws OutOfSequenceException, ParserException,
            Exception {

        SLALogItem slaLogItem = getSLALogger().createLogItem("SorRelationParser", "dataSet");
        try {
            File files = checkRequiredFiles(dataSet);
            
            List<InstitutionOwnerEntityType> list = unmarshallFile(files);
            selfRelationsMap = new HashMap<String, String>();
            shakYderMap = new HashMap<String, HashSet<String>>();
            for (InstitutionOwnerEntityType institutions : list) {
                persistInstitutionRelations(institutions, persister);
            }
            // persist institutions relations with self
            Set<String> keys = selfRelationsMap.keySet();
            for (String sorSelfRelationId : keys) {
                persistNode(sorSelfRelationId, sorSelfRelationId, persister);
            }
            
            // persist collected shak/yder numbers
            Set<String> syKeys = shakYderMap.keySet();
            for (String shakYderKey : syKeys) {
                persistShakYder(shakYderKey, shakYderMap.get(shakYderKey), persister);
            }
            
            slaLogItem.setCallResultOk();
            slaLogItem.store();
        } catch (Exception e) {
            slaLogItem.setCallResultError("SorRelationParser Failed - Cause: " + e.getMessage());
            slaLogItem.store();
            throw e;
        } finally {
            selfRelationsMap = null;
            shakYderMap = null;
        }
        
    }

    private void persistInstitutionRelations(InstitutionOwnerEntityType institution, RecordPersister persister) throws SQLException {
        
        InstitutionOwnerType owner = institution.getInstitutionOwner();
        String ownerId = ""+owner.getSorIdentifier();
        if(!hasValidPeriod(owner.getSorStatus())) {
            Log.debug("Institution with SOR id:" +ownerId+"  is is no longer valid, toDate: "+owner.getSorStatus().getToDate());
            return;
        }
        
        List<HealthInstitutionEntityType> childInstitutions = institution.getHealthInstitutionEntity();
        
        selfRelationsMap.put(ownerId, "");

        for (HealthInstitutionEntityType hiChild : childInstitutions) {
            String childId = ""+hiChild.getHealthInstitution().getSorIdentifier();
            persistNode(ownerId, childId, persister);

            List<OrganizationalUnitEntityType> organizationalUnitEntity = hiChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType ouChild : organizationalUnitEntity) {
                traverseOrganizationalUnitEntity(ownerId, ouChild, persister);
            }
            traverseHealthInstitutionChild(hiChild, persister);
        }
    }

    private void traverseHealthInstitutionChild(HealthInstitutionEntityType child, RecordPersister persister) throws SQLException {
        HealthInstitutionType healthInstitution = child.getHealthInstitution();
        
        String hiChildId = ""+healthInstitution.getSorIdentifier();
        if(!hasValidPeriod(healthInstitution.getSorStatus())) {
            Log.debug("Institution with SOR id:" +hiChildId+" is no longer valid, toDate: "+healthInstitution.getSorStatus().getToDate());
            return;
        }
        
        selfRelationsMap.put(hiChildId, "");
        
        String shakIdentifier = healthInstitution.getShakIdentifier();
        if(shakIdentifier != null && shakIdentifier.trim().length() > 0) {
            putValueInShakYderMap("SHAK="+shakIdentifier, hiChildId);
        }

        List<OrganizationalUnitEntityType> organizationalUnitEntity = child.getOrganizationalUnitEntity();
        for (OrganizationalUnitEntityType ouChild : organizationalUnitEntity) {
            traverseOrganizationalUnitEntity(hiChildId, ouChild, persister);
        }
    }

    private void traverseOrganizationalUnitEntity(String parentId, OrganizationalUnitEntityType ouChild, RecordPersister persister) throws SQLException {
        OrganizationalUnitType ou = ouChild.getOrganizationalUnit();
        String childId = ""+ou.getSorIdentifier();
        
        if(!hasValidPeriod(ou.getSorStatus())) {
            Log.debug("Institution with SOR id:" +ou+" is no longer valid, toDate: "+ou.getSorStatus().getToDate());
            return;
        }
        
        // persist relation with owner and with self
        persistNode(parentId, childId, persister);
        selfRelationsMap.put(childId, "");
        
        String shakIdentifier = ou.getShakIdentifier();
        if(shakIdentifier != null && shakIdentifier.trim().length() > 0) {
            putValueInShakYderMap("SHAK="+shakIdentifier, childId);
        }
        String yderIdentifier = ou.getProviderIdentifier();
        if(yderIdentifier != null && yderIdentifier.trim().length() > 0) {
            putValueInShakYderMap("Yder="+yderIdentifier, childId);
        }
        
        if(ouChild.getOrganizationalUnitEntity() != null) {
            // first persist any existing childs with parent relation
            List<OrganizationalUnitEntityType> subChilds = ouChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType subChild : subChilds) {
                traverseOrganizationalUnitEntity(parentId, subChild, persister);
            }
            // then traverse recursively through childs;
            List<OrganizationalUnitEntityType> subChilds2 = ouChild.getOrganizationalUnitEntity();
            for (OrganizationalUnitEntityType subChild : subChilds2) {
                String ouChildId = ""+ouChild.getOrganizationalUnit().getSorIdentifier();
                traverseOrganizationalUnitEntity(ouChildId, subChild, persister);
            }
        }
    }

    private void persistNode(String parent, String child, RecordPersister persister) throws SQLException {
        RecordBuilder OwnerRecord = new RecordBuilder(recordSpecification);
        OwnerRecord.field("sor_parent", parent);
        OwnerRecord.field("sor_child", child);
        persister.persist(OwnerRecord.build(), recordSpecification);
    }
    
    private void persistShakYder(String shakYder, HashSet<String> sorSet, RecordPersister persister) throws SQLException {
        
        if(shakYder == null) {
            throw new IllegalArgumentException("SHAK or YDER must be set");
        }
        
        for (String sor : sorSet) {
            RecordBuilder OwnerRecord = new RecordBuilder(shakYderSpecification);
            OwnerRecord.field("shak_yder", shakYder);
            OwnerRecord.field("sor", sor);
            persister.persist(OwnerRecord.build(), shakYderSpecification);
        }
    }
    
    private void putValueInShakYderMap(String shakYder, String sor) {

        HashSet<String> sorSet = shakYderMap.get(shakYder);
        if(sorSet == null) {
            sorSet = new HashSet<String>();
        }
        sorSet.add(sor);
        shakYderMap.put(shakYder, sorSet);
    }
    
    
    private boolean hasValidPeriod(SorStatusType sorStatus) {
        if(sorStatus != null) {
            XMLGregorianCalendar toDate = sorStatus.getToDate();
            if(toDate != null) {
                DateTime toDateTime = new DateTime(toDate.toGregorianCalendar().getTime());
                if(snapshotDate.isAfter(toDateTime)) {
                    return false;
                }
            }
        }
        // if toDate of sorStatus cannot be found assume entity is valid.
        return true;
    }

    private List<InstitutionOwnerEntityType> unmarshallFile(File dataSet) {
        List<InstitutionOwnerEntityType> list = new ArrayList<InstitutionOwnerEntityType>();
        
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SorTreeType.class.getPackage().getName());
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            File[] input = null;
            if(dataSet.isDirectory()) {
                 input = dataSet.listFiles();
            } else {
                input = new File[] {dataSet};
            }

            for (int i = 0; i < input.length; i++) {
                JAXBElement<SorTreeType> jaxbSOR = (JAXBElement<SorTreeType>) jaxbUnmarshaller.unmarshal(input[i]);
                SorTreeType sor = jaxbSOR.getValue();
                snapshotDate = new DateTime(sor.getSnapshotDate().toGregorianCalendar().getTime());
                List<InstitutionOwnerEntityType> institutionOwnerEntity = sor.getInstitutionOwnerEntity();
                list.addAll(institutionOwnerEntity);
            }
        } catch (JAXBException e) {
            logger.error("", e);
        }
        return list;
    }

    private File checkRequiredFiles(File dataSet) {
        
       checkNotNull(dataSet);
       
       File[] input = null;
       if(dataSet.isDirectory()) {
            input = dataSet.listFiles();
       } else {
           input = new File[] {dataSet};
       }

       for (int i = 0; i < input.length; i++) {
           String fileName = input[i].getName();
           MDC.put("filename", fileName);
       }
       
       return dataSet;
    }

}
