package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;

import java.util.Date;

@Output(name = "SikredeYderRelation")
public class SikredeYderRelation extends CPREntity {
    protected String cpr;
    protected String ydernummer;
    protected Date ydernummerIkraftDato;
    protected Date ydernummerRegistreringDato;
    protected String sikringsgruppeKode;
    protected Date gruppeKodeIkraftDato;
    protected Date gruppekodeRegistreringDato;

    @Id
    @Output
    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    @Output
    public Date getGruppeKodeIkraftDato() {
        return gruppeKodeIkraftDato;
    }

    public void setGruppeKodeIkraftDato(Date gruppeKodeIkraftDato) {
        this.gruppeKodeIkraftDato = gruppeKodeIkraftDato;
    }

    @Output
    public Date getGruppekodeRegistreringDato() {
        return gruppekodeRegistreringDato;
    }

    public void setGruppekodeRegistreringDato(Date gruppekodeRegistreringDato) {
        this.gruppekodeRegistreringDato = gruppekodeRegistreringDato;
    }

    @Output
    public String getSikringsgruppeKode() {
        return sikringsgruppeKode;
    }

    public void setSikringsgruppeKode(String sikringsgruppeKode) {
        this.sikringsgruppeKode = sikringsgruppeKode;
    }

    @Output
    public String getYdernummer() {
        return ydernummer;
    }

    public void setYdernummer(String ydernummer) {
        this.ydernummer = ydernummer;
    }

    @Output
    public Date getYdernummerIkraftDato() {
        return ydernummerIkraftDato;
    }

    public void setYdernummerIkraftDato(Date ydernummerIkraftDato) {
        this.ydernummerIkraftDato = ydernummerIkraftDato;
    }

    @Output
    public Date getYdernummerRegistreringDato() {
        return ydernummerRegistreringDato;
    }

    public void setYdernummerRegistreringDato(Date ydernummerRegistreringDato) {
        this.ydernummerRegistreringDato = ydernummerRegistreringDato;
    }

    @Override
    @Output
    public Date getValidFrom() {
        return ydernummerIkraftDato;
    }
}
