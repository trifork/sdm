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
package com.trifork.stamdata.models.sikrede;

import com.trifork.stamdata.models.BaseTemporalEntity;

import static javax.persistence.TemporalType.DATE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.util.Date;

/**
 * User: frj
 * Date: 9/20/11
 * Time: 11:38 AM
 *
 * @Author frj
 */
@Entity
public class SikredeYderRelation extends BaseTemporalEntity {

    private String id;
    private String cpr;
    private String type;
    private int ydernummer;
    private char sikringsgruppeKode;

    @Temporal(DATE)
    private Date ydernummerIkraftDato;

    @Temporal(DATE)
    private Date ydernummerRegistreringDato;

    @Temporal(DATE)
    private Date gruppeKodeIkraftDato;

    @Temporal(DATE)
    private Date gruppekodeRegistreringDato;


    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    @Id
    @Column
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getYdernummerIkraftDato() {
        return ydernummerIkraftDato;
    }

    public void setYdernummerIkraftDato(Date ydernummerIkraftDato) {
        this.ydernummerIkraftDato = ydernummerIkraftDato;
    }

    public Date getYdernummerRegistreringDato() {
        return ydernummerRegistreringDato;
    }

    public void setYdernummerRegistreringDato(Date ydernummerRegistreringDato) {
        this.ydernummerRegistreringDato = ydernummerRegistreringDato;
    }

    public Date getGruppeKodeIkraftDato() {
        return gruppeKodeIkraftDato;
    }

    public void setGruppeKodeIkraftDato(Date gruppeKodeIkraftDato) {
        this.gruppeKodeIkraftDato = gruppeKodeIkraftDato;
    }

    public Date getGruppekodeRegistreringDato() {
        return gruppekodeRegistreringDato;
    }

    public void setGruppekodeRegistreringDato(Date gruppekodeRegistreringDato) {
        this.gruppekodeRegistreringDato = gruppekodeRegistreringDato;
    }

    public char getSikringsgruppeKode() {
        return sikringsgruppeKode;
    }

    public void setSikringsgruppeKode(char sikringsgruppeKode) {
        this.sikringsgruppeKode = sikringsgruppeKode;
    }

    public int getYdernummer() {
        return ydernummer;
    }

    public void setYdernummer(int ydernummer) {
        this.ydernummer = ydernummer;
    }
}
