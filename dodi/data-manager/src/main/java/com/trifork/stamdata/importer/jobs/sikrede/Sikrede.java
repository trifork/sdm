package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Sikrede extends CPREntity
{
	private String kommunekode;
	private Date kommunekodeIkraftDato;

	/* Optional field */
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

    /* Reference value - required by BRS */
    private String reference;

	@Id
	@Column
	public String getCpr()
	{
		return cpr;
	}

	@Column
	public String getKommunekode()
	{
		return kommunekode;
	}

	@Column
	public String getStatus()
	{
		return status;
	}

	public void setKommunekode(String kommunekode)
	{
		this.kommunekode = kommunekode;
	}

	@Column
	public Date getKommunekodeIkraftDato()
	{
		return kommunekodeIkraftDato;
	}

	public void setKommunekodeIKraftDato(Date iKraftDato)
	{
		this.kommunekodeIkraftDato = iKraftDato;
	}

	public void setFoelgeskabsPersonCpr(String foelgeskabsPersonCpr)
	{
		this.foelgeskabsPersonCpr = foelgeskabsPersonCpr;
	}

	public void setKommunekodeIkraftDato(Date kommunekodeIkraftDato)
	{
		this.kommunekodeIkraftDato = kommunekodeIkraftDato;
	}

	public void setFoelgeskabsPerson(String cprFoelgeskabsPerson)
	{
		this.foelgeskabsPersonCpr = cprFoelgeskabsPerson;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setForsikringsinstans(String forsikringsinstans)
	{
		this.forsikringsinstans = forsikringsinstans;
	}

	public void setForsikringsinstansKode(String forsikringsinstansKode)
	{
		this.forsikringsinstansKode = forsikringsinstansKode;
	}

	public void setForsikringsnummer(String forsikringsnummer)
	{
		this.forsikringsnummer = forsikringsnummer;
	}

	public void setSikredesSocialeLand(String land)
	{
		this.socialLand = land;
	}

	public void setSikredesSocialeLandKode(String landekode)
	{
		this.socialLandKode = landekode;
	}

	@Column
	public String getSocialLand()
	{
		return socialLand;
	}

	public void setSocialLand(String socialLand)
	{
		this.socialLand = socialLand;
	}

	@Column
	public String getSocialLandKode()
	{
		return socialLandKode;
	}

	public void setSocialLandKode(String socialLandKode)
	{
		this.socialLandKode = socialLandKode;
	}

	@Column
	public Date getSslGyldigFra()
	{
		return sslGyldigFra;
	}

	public void setSslGyldigFra(Date gyldigFra)
	{
		this.sslGyldigFra = gyldigFra;
	}

	@Column
	public Date getSslGyldigTil()
	{
		return SslGyldigTil;
	}

	public void setSslGyldigTil(Date gyldigTil)
	{
		this.SslGyldigTil = gyldigTil;
	}

	@Column
	public Date getBevisIkraftDato()
	{
		return bevisIkraftDato;
	}

	public void setBevisIkraftDato(Date bevisIkraftDato)
	{
		this.bevisIkraftDato = bevisIkraftDato;
	}

	@Column
	public String getFoelgeskabsPersonCpr()
	{
		return foelgeskabsPersonCpr;
	}

	@Column
	public String getForsikringsinstans()
	{
		return forsikringsinstans;
	}

	@Column
	public String getForsikringsinstansKode()
	{
		return forsikringsinstansKode;
	}

	@Column
	public String getForsikringsnummer()
	{
		return forsikringsnummer;
	}

	@Override
	public String toString()
	{
		return "Sikrede{" + "bevisIkraftDato=" + bevisIkraftDato + ", kommunekode='" + kommunekode + '\'' + ", kommunekodeIkraftDato=" + kommunekodeIkraftDato + ", foelgeskabsPersonCpr='" + foelgeskabsPersonCpr + '\'' + ", status='" + status + '\'' + ", forsikringsinstans='" + forsikringsinstans + '\'' + ", forsikringsinstansKode='" + forsikringsinstansKode + '\'' + ", forsikringsnummer='" + forsikringsnummer + '\'' + ", sslGyldigFra=" + sslGyldigFra + ", SslGyldigTil=" + SslGyldigTil + ", socialLand='" + socialLand + '\'' + ", socialLandKode='" + socialLandKode + '\'' + '}';
	}

    @Column
    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
