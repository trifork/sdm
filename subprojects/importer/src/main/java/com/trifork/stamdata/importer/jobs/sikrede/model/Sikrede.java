package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.Id;
import com.trifork.stamdata.importer.persistence.Output;

import java.util.Date;

@Output(name = "Sikrede")
public class Sikrede extends CPREntity {

    private String kommunekode;
    private Date kommunekodeIkraftDato;
    private String foelgeskabsPersonCpr;
    private String status;
    private Date bevisIkraftDato;
    /* SSL elementer */
    private String forsikringsinstans;
    private String forsikringsinstansKode;
    private String forsikringsnummer;
    private Date sslGyldigFra;
    private Date SslGyldigTil;
    private String socialLand;
    private String socialLandKode;

    @Id
    @Output
    public String getCpr() {
        return cpr;
    }

    @Output
    public String getKommunekode() {
        return kommunekode;
    }

    @Output
    public String getStatus() {
        return status;
    }

    public void setKommunekode(String kommunekode) {
        this.kommunekode = kommunekode;
    }

    @Output
    public Date getKommunekodeIkraftDato() {
        return kommunekodeIkraftDato;
    }

    public void setKommunekodeIKraftDato(Date iKraftDato) {
        this.kommunekodeIkraftDato = iKraftDato;
    }

    public void setFoelgeskabsPersonCpr(String foelgeskabsPersonCpr) {
        this.foelgeskabsPersonCpr = foelgeskabsPersonCpr;
    }

    public void setKommunekodeIkraftDato(Date kommunekodeIkraftDato) {
        this.kommunekodeIkraftDato = kommunekodeIkraftDato;
    }

    public void setFoelgeskabsPerson(String cprFoelgeskabsPerson) {
        this.foelgeskabsPersonCpr = cprFoelgeskabsPerson;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setForsikringsinstans(String forsikringsinstans) {
        this.forsikringsinstans = forsikringsinstans;
    }

    public void setForsikringsinstansKode(String forsikringsinstansKode) {
        this.forsikringsinstansKode = forsikringsinstansKode;
    }

    public void setForsikringsnummer(String forsikringsnummer) {
        this.forsikringsnummer = forsikringsnummer;
    }

    public void setSikredesSocialeLand(String land) {
        this.socialLand = land;
    }

    public void setSikredesSocialeLandKode(String landekode) {
        this.socialLandKode = landekode;
    }

    @Output
    public String getSocialLand() {
        return socialLand;
    }

    public void setSocialLand(String socialLand) {
        this.socialLand = socialLand;
    }

    @Output
    public String getSocialLandKode() {
        return socialLandKode;
    }

    public void setSocialLandKode(String socialLandKode) {
        this.socialLandKode = socialLandKode;
    }

    @Output
    public Date getSslGyldigFra() {
        return sslGyldigFra;
    }

    public void setSslGyldigFra(Date gyldigFra) {
        this.sslGyldigFra = gyldigFra;
    }

    @Output
    public Date getSslGyldigTil() {
        return SslGyldigTil;
    }

    public void setSslGyldigTil(Date gyldigTil) {
        this.SslGyldigTil = gyldigTil;
    }

    @Output
    public Date getBevisIkraftDato() {
        return bevisIkraftDato;
    }

    public void setBevisIkraftDato(Date bevisIkraftDato) {
        this.bevisIkraftDato = bevisIkraftDato;
    }

    @Output
    public String getFoelgeskabsPersonCpr() {
        return foelgeskabsPersonCpr;
    }

    @Output
    public String getForsikringsinstans() {
        return forsikringsinstans;
    }

    @Output
    public String getForsikringsinstansKode() {
        return forsikringsinstansKode;
    }

    @Output
    public String getForsikringsnummer() {
        return forsikringsnummer;
    }

    @Override
    public String toString() {
        return "Sikrede{" +
                "bevisIkraftDato=" + bevisIkraftDato +
                ", kommunekode='" + kommunekode + '\'' +
                ", kommunekodeIkraftDato=" + kommunekodeIkraftDato +
                ", foelgeskabsPersonCpr='" + foelgeskabsPersonCpr + '\'' +
                ", status='" + status + '\'' +
                ", forsikringsinstans='" + forsikringsinstans + '\'' +
                ", forsikringsinstansKode='" + forsikringsinstansKode + '\'' +
                ", forsikringsnummer='" + forsikringsnummer + '\'' +
                ", sslGyldigFra=" + sslGyldigFra +
                ", SslGyldigTil=" + SslGyldigTil +
                ", socialLand='" + socialLand + '\'' +
                ", socialLandKode='" + socialLandKode + '\'' +
                '}';
    }
}
