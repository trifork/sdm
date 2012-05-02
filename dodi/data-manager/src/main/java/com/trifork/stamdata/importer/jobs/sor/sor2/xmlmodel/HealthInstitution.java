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


import java.util.List;

public class HealthInstitution {
    private long sorIdentifier; //required
    private String entityName; //required - String up to 60 chars
    private long institutionType; //required
    private String pharmacyIdentifier; //String up to 20 chars, can be null
    private String shakIdentifier; //String 4-7 characters, can be null

    private PostalAddressInformation postalAddressInformation;
    private PostalAddressInformation visitingAddressInformation;
    private VirtualAddressInformation virtualAddressInformation;

    private SorStatus sorStatus;//required

    private List<Long> replacesEntityCollection; //can be null - contains list of sorIDentifiers
    private List<Long> replacedByEntityCollection; //can be null - contains list of sorIDentifiers

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public long getInstitutionType() {
        return institutionType;
    }

    public void setInstitutionType(long institutionType) {
        this.institutionType = institutionType;
    }

    public String getPharmacyIdentifier() {
        return pharmacyIdentifier;
    }

    public void setPharmacyIdentifier(String pharmacyIdentifier) {
        this.pharmacyIdentifier = pharmacyIdentifier;
    }

    public PostalAddressInformation getPostalAddressInformation() {
        return postalAddressInformation;
    }

    public void setPostalAddressInformation(PostalAddressInformation postalAddressInformation) {
        this.postalAddressInformation = postalAddressInformation;
    }

    public List<Long> getReplacedByEntityCollection() {
        return replacedByEntityCollection;
    }

    public void setReplacedByEntityCollection(List<Long> replacedByEntityCollection) {
        this.replacedByEntityCollection = replacedByEntityCollection;
    }

    public List<Long> getReplacesEntityCollection() {
        return replacesEntityCollection;
    }

    public void setReplacesEntityCollection(List<Long> replacesEntityCollection) {
        this.replacesEntityCollection = replacesEntityCollection;
    }

    public String getShakIdentifier() {
        return shakIdentifier;
    }

    public void setShakIdentifier(String shakIdentifier) {
        this.shakIdentifier = shakIdentifier;
    }

    public long getSorIdentifier() {
        return sorIdentifier;
    }

    public void setSorIdentifier(long sorIdentifier) {
        this.sorIdentifier = sorIdentifier;
    }

    public SorStatus getSorStatus() {
        return sorStatus;
    }

    public void setSorStatus(SorStatus sorStatus) {
        this.sorStatus = sorStatus;
    }

    public VirtualAddressInformation getVirtualAddressInformation() {
        return virtualAddressInformation;
    }

    public void setVirtualAddressInformation(VirtualAddressInformation virtualAddressInformation) {
        this.virtualAddressInformation = virtualAddressInformation;
    }

    public PostalAddressInformation getVisitingAddressInformation() {
        return visitingAddressInformation;
    }

    public void setVisitingAddressInformation(PostalAddressInformation visitingAddressInformation) {
        this.visitingAddressInformation = visitingAddressInformation;
    }
}
