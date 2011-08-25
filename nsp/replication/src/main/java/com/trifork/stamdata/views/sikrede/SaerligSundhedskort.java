// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.views.sikrede;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;
import java.util.Date;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

@Entity
@XmlRootElement
@ViewPath("sikrede/saerligsundhedskort/v1")
@Table(name = "SaerligSundhedskort")
public class SaerligSundhedskort extends View {
    @Id
    @GeneratedValue
    @XmlTransient
    @Column(name = "SaerligSundhedskortPID")
    protected BigInteger recordID;

    @XmlElement(required = true)
    @Column(name = "CPR")
    protected String cpr;

    @Temporal(TIMESTAMP)
    @Column(name = "ValidFrom")
    protected Date validFrom;

    @Temporal(TIMESTAMP)
    @Column(name = "ValidTo")
    protected Date validTo;

    @XmlTransient
    @Temporal(TIMESTAMP)
    @Column(name = "ModifiedDate")
    protected Date modifiedDate;

    @XmlElement
    @Column(name = "adresseLinje1")
    protected String adresseLinje1;

    @XmlElement
    @Column(name = "adresseLinje2")
    protected String adresseLinje2;

    @XmlElement
    @Column(name = "bopelsLand")
    protected String bopelsLand;

    @XmlElement
    @Column(name = "bopelsLandKode")
    protected String bopelsLandKode;

    @XmlElement
    @Column(name = "emailAdresse")
    protected String emailAdresse;

    @XmlElement
    @Column(name = "familieRelationCpr")
    protected String familieRelationCpr;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "foedselsDato")
    protected Date foedselsDato;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sskGyldigFra")
    protected Date sskGyldigFra;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sskGyldigTil")
    protected Date sskGyldigTil;

    @XmlElement
    @Column(name = "mobilNummer")
    protected String mobilNummer;

    @XmlElement
    @Column(name = "postnummerBy")
    protected String postnummerBy;

    
/*
	CreatedDate DATETIME NOT NULL,
 */

    @Override
    public String getId() {
        return cpr;
    }

    @Override
    public BigInteger getRecordID() {
        return recordID;
    }

    @Override
    public Date getUpdated() {
        return modifiedDate;
    }

}
