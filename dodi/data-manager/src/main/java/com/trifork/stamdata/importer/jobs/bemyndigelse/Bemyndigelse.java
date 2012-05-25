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
package com.trifork.stamdata.importer.jobs.bemyndigelse;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(name="Bemyndigelse", namespace="http://nsi.dk/bemyndigelser/2012/04/")
public class Bemyndigelse {
    
    String kode;
    String bemyndigendeCPR;
    String bemyndigedeCPR;
    String bemyndigedeCVR;
    String system;
    private String arbejdsfunktion;
    String rettighed;
    String status;
    String godkendelsesDato;
    String oprettelsesDato;
    String modificeretDato;
    String gyldigFraDato;
    String gyldigTilDato;
    
    @XmlElement(name="kode")
    public String getKode() {
        return kode;
    }
    public void setKode(String kode) {
        this.kode = kode;
    }
    @XmlElement(name="bemyndigende_cpr")
    public String getBemyndigendeCPR() {
        return bemyndigendeCPR;
    }
    public void setBemyndigendeCPR(String bemyndigendeCPR) {
        this.bemyndigendeCPR = bemyndigendeCPR;
    }
    @XmlElement(name="bemyndigede_cpr")
    public String getBemyndigedeCPR() {
        return bemyndigedeCPR;
    }
    public void setBemyndigedeCPR(String bemyndigedeCPR) {
        this.bemyndigedeCPR = bemyndigedeCPR;
    }
    @XmlElement(name="bemyndigede_cvr")
    public String getBemyndigedeCVR() {
        return bemyndigedeCVR;
    }
    public void setBemyndigedeCVR(String bemyndigedeCVR) {
        this.bemyndigedeCVR = bemyndigedeCVR;
    }
    @XmlElement(name="system")
    public String getSystem() {
        return system;
    }
    public void setSystem(String system) {
        this.system = system;
    }
    @XmlElement(name="rettighed")
    public String getRettighed() {
        return rettighed;
    }
    public void setRettighed(String rettighed) {
        this.rettighed = rettighed;
    }
    @XmlElement(name="godkendelsesdato")
    public String getGodkendelsesDato() {
        return godkendelsesDato;
    }
    public void setGodkendelsesDato(String godkendelsesDato) {
        this.godkendelsesDato = godkendelsesDato;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    @XmlElement(name="status")
    public String getStatus() {
        return status;
    }
    @XmlElement(name="CreatedDate")
    public String getOprettelsesDato() {
        return oprettelsesDato;
    }
    public void setOprettelsesDato(String oprettelsesDato) {
        this.oprettelsesDato = oprettelsesDato;
    }
    @XmlElement(name="ModifiedDate")
    public String getModificeretDato() {
        return modificeretDato;
    }
    public void setModificeretDato(String modificeretDato) {
        this.modificeretDato = modificeretDato;
    }
    @XmlElement(name="ValidFrom")
    public String getGyldigFraDato() {
        return gyldigFraDato;
    }
    public void setGyldigFraDato(String gyldigFraDato) {
        this.gyldigFraDato = gyldigFraDato;
    }
    @XmlElement(name="ValidTo")
    public String getGyldigTilDato() {
        return gyldigTilDato;
    }
    public void setGyldigTilDato(String gyldigTilDato) {
        this.gyldigTilDato = gyldigTilDato;
    }
    public void setArbejdsfunktion(String arbejdsfunktion) {
        this.arbejdsfunktion = arbejdsfunktion;
    }
    @XmlElement(name="arbejdsfunktion")
    public String getArbejdsfunktion() {
        return arbejdsfunktion;
    }

}
