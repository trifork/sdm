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
