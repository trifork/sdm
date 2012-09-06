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

package dk.nsi.stamdata.views.vitamin;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewPath;

@Entity
@XmlRootElement
@ViewPath("vitamin/grunddata/v1")
public class Grunddata extends View {

    protected Grunddata() {
    }

    @Id
    @GeneratedValue
    @XmlTransient
    @Column(name = "VitaminGrunddataPID")
    private BigInteger recordID;

    @XmlElement(required = true)
    public String drugID;

    public String varetype;
    public String varedeltype;
    public String alfabetSekvensplads;
    public String specNummer;
    public String navn;
    public String formKode;
    public String formTekst;
    public String kodeYderligereFormOplysninger;
    public String styrkeTekst;
    public Double styrkeNumerisk;
    public String styrkeEnhed;
    public BigInteger mTIndehaverKode;
    public BigInteger repraesentantDistributoerKode;
    public String aTCKode;
    public String administrationsvejKode;
    public String trafikadvarsel;
    public String substitution;
    public String substitutionsgruppe;
    public String dosisdispensering;
    public String karantaeneDato;
    public String sletningsstatus;

    @XmlTransient
    @Temporal(TIMESTAMP)
    public Date modifiedDate;
    
    @Temporal(TIMESTAMP)
    @XmlElement(required = true)
    public Date validFrom;

    @Temporal(TIMESTAMP)
    @XmlElement(required = true)
    public Date validTo;
    
    @Override
    public BigInteger getRecordID() {
        return recordID;
    }

    @Override
    public String getId() {
        return drugID;
    }

    @Override
    public Date getUpdated() {
        return modifiedDate;
    }
}
