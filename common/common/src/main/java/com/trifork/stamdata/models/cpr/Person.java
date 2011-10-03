package com.trifork.stamdata.models.cpr;

import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.models.BaseTemporalEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import java.util.Date;

import static javax.persistence.TemporalType.DATE;
import static javax.persistence.TemporalType.TIMESTAMP;

// Use the split of different parts that the person table is made up of instead.
// The person table is unstable and its behavior is not well-defined.
@Entity
public class Person extends BaseTemporalEntity
{
	public Person()
	{
	}

    public String cpr;

    @Id
    @Column
    public String getCpr() {
        return cpr;
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }

    public String koen;

    public String fornavn;

	public String mellemnavn;

	public String efternavn;

	public String coNavn;

	public String lokalitet;

	public String vejnavn;

	public String bygningsnummer;

	public String husnummer;

	public String etage;

	public String sideDoerNummer;

	public String bynavn;

	public String postnummer;

	public String postdistrikt;

	public String status;
	private Date statusDato;

	public String gaeldendeCPR;

	public Date foedselsdato;

	public String stilling;

	public String vejKode;

	public String kommuneKode;

	public Date navnebeskyttelseslettedato;
	public Date navnebeskyttelsestartdato;


    public String getKoen() {
        return koen;
    }

    public void setKoen(String koen) {
        this.koen = koen;
    }

    public String getFornavn() {
        return fornavn;
    }

    public void setFornavn(String fornavn) {
        this.fornavn = fornavn;
    }

    public String getMellemnavn() {
        return mellemnavn;
    }

    public void setMellemnavn(String mellemnavn) {
        this.mellemnavn = mellemnavn;
    }

    public String getEfternavn() {
        return efternavn;
    }

    public void setEfternavn(String efternavn) {
        this.efternavn = efternavn;
    }

    public String getCoNavn() {
        return coNavn;
    }

    public void setCoNavn(String coNavn) {
        this.coNavn = coNavn;
    }

    public String getLokalitet() {
        return lokalitet;
    }

    public void setLokalitet(String lokalitet) {
        this.lokalitet = lokalitet;
    }

    public String getVejnavn() {
        return vejnavn;
    }

    public void setVejnavn(String vejnavn) {
        this.vejnavn = vejnavn;
    }

    public String getBygningsnummer() {
        return bygningsnummer;
    }

    public void setBygningsnummer(String bygningsnummer) {
        this.bygningsnummer = bygningsnummer;
    }

    public String getHusnummer() {
        return husnummer;
    }

    public void setHusnummer(String husnummer) {
        this.husnummer = husnummer;
    }

    public String getEtage() {
        return etage;
    }

    public void setEtage(String etage) {
        this.etage = etage;
    }

    public String getSideDoerNummer() {
        return sideDoerNummer;
    }

    public void setSideDoerNummer(String sideDoerNummer) {
        this.sideDoerNummer = sideDoerNummer;
    }

    public String getBynavn() {
        return bynavn;
    }

    public void setBynavn(String bynavn) {
        this.bynavn = bynavn;
    }

    public String getPostnummer() {
        return postnummer;
    }

    public void setPostnummer(String postnummer) {
        this.postnummer = postnummer;
    }

    public String getPostdistrikt() {
        return postdistrikt;
    }

    public void setPostdistrikt(String postdistrikt) {
        this.postdistrikt = postdistrikt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    @Temporal(TIMESTAMP)
    public void setStatusDato(Date statusDato)
    {
    	this.statusDato = statusDato;
    }
    
    public Date getStatusDato()
    {
    	return statusDato;
    }

    public String getGaeldendeCPR() {
        return gaeldendeCPR;
    }

    public void setGaeldendeCPR(String gaeldendeCPR) {
        this.gaeldendeCPR = gaeldendeCPR;
    }

    @Temporal(DATE)
    public Date getFoedselsdato() {
        return foedselsdato;
    }

    public void setFoedselsdato(Date foedselsdato) {
        this.foedselsdato = foedselsdato;
    }

    public String getStilling() {
        return stilling;
    }

    public void setStilling(String stilling) {
        this.stilling = stilling;
    }

    public String getVejKode() {
        return vejKode;
    }

    public void setVejKode(String vejKode) {
        this.vejKode = vejKode;
    }

    public String getKommuneKode() {
        return kommuneKode;
    }

    public void setKommuneKode(String kommuneKode) {
        this.kommuneKode = kommuneKode;
    }

    @Temporal(TIMESTAMP)
    public Date getNavnebeskyttelseslettedato() {
        return navnebeskyttelseslettedato;
    }
    
    public void setNavnebeskyttelseslettedato(Date navnebeskyttelseslettedato) {
        this.navnebeskyttelseslettedato = navnebeskyttelseslettedato;
    }

    @Temporal(TIMESTAMP)
    public Date getNavnebeskyttelsestartdato() {
        return navnebeskyttelsestartdato;
    }

    public void setNavnebeskyttelsestartdato(@Nullable Date navnebeskyttelsestartdato) {
        this.navnebeskyttelsestartdato = navnebeskyttelsestartdato;
    }
    
    private String navnTilAdressering;
    
    public void setNavnTilAdressering(String navnTilAdressering)
    {
    	this.navnTilAdressering = navnTilAdressering;
    }
    
    public String getNavnTilAdressering()
    {
    	return navnTilAdressering;
    }
    
    private String vejnavnTilAdressering;
    
    public void setVejnavnTilAdressering(String vejnavnTilAdressering)
    {
    	this.vejnavnTilAdressering = vejnavnTilAdressering;
    }
    
    public String getVejnavnTilAdressering()
    {
    	return vejnavnTilAdressering;
    }
    
    private boolean foedselsdatoMarkering;
    
    public boolean getFoedselsdatoMarkering()
    {
    	return foedselsdatoMarkering;
    }
    
    public void setFoedselsdatoMarkering(boolean foedselsdatoMarkering)
    {
    	this.foedselsdatoMarkering = foedselsdatoMarkering;
    }
}
