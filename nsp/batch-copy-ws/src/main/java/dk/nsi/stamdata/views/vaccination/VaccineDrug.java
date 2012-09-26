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
package dk.nsi.stamdata.views.vaccination;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("ddv/vaccines_drugs/v1")
public class VaccineDrug extends View {

    @Id
    @GeneratedValue
    @XmlTransient
    @Column(name = "PID")
    private BigInteger recordID;

    String ddvModifiedDate;
    String ddvValidFrom;
    String ddvValidTo;

    long vaccineIdentifier;
    long versionID;
    long drugIdentifier;
    String drugName;

    @Temporal(TIMESTAMP)
    protected Date validFrom;
    @Temporal(TIMESTAMP)
    protected Date validTo;
    @XmlTransient
    @Temporal(TIMESTAMP)
    private Date modifiedDate;

    public String getDdvModifiedDate() {
        return ddvModifiedDate;
    }
    public String getDdvValidFrom() {
        return ddvValidFrom;
    }
    public String getDdvValidTo() {
        return ddvValidTo;
    }
    public long getVaccineIdentifier() {
        return vaccineIdentifier;
    }
    public long getVersionID() {
        return versionID;
    }
    public long getDrugIdentifier() {
        return drugIdentifier;
    }
    public String getDrugName() {
        return drugName;
    }

    @Override
    public BigInteger getRecordID() {
        return recordID;
    }

    @Override
    public String getId() {
        return ""+vaccineIdentifier;
    }

    @Override
    public Date getUpdated() {
        return modifiedDate;
    }
} 