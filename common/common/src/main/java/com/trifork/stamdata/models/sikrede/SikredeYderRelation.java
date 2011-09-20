package com.trifork.stamdata.models.sikrede;

import com.trifork.stamdata.models.BaseTemporalEntity;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

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

    private String cpr;
    private int ydernummer;
    private char sikringsgruppeKode;

    @Temporal(DATE)
    private Date gruppeKodeIkraftDato;


    @Id
    @Column
    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public Date getGruppeKodeIkraftDato() {
        return gruppeKodeIkraftDato;
    }

    public void setGruppeKodeIkraftDato(Date gruppeKodeIkraftDato) {
        this.gruppeKodeIkraftDato = gruppeKodeIkraftDato;
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
