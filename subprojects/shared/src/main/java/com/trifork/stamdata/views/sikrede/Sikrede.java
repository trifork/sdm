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
@ViewPath("sikrede/sikrede/v1")
@Table(name = "Sikrede")
public class Sikrede extends View {
    @Id
    @GeneratedValue
    @XmlTransient
    @Column(name = "SikredePID")
    protected BigInteger recordID;

    @XmlElement(required = true)
    @Column(name = "CPR")
    protected String cpr;

    @XmlElement
    @Column(name = "kommunekode")
    protected String kommunekode;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "kommunekodeIkraftDato")
    protected Date kommunekodeIkraftDato;

    @Temporal(TIMESTAMP)
    @Column(name = "ValidFrom")
    protected Date validFrom;

    @XmlTransient
    @Temporal(TIMESTAMP)
    @Column(name = "ModifiedDate")
    protected Date modifiedDate;

    @XmlElement(required = false)
    @Column(name = "foelgeskabsPersonCpr")
    protected String foelgeskabsPersonCpr;

    @XmlElement(required = false)
    @Column(name = "status")
    protected String status;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "bevisIkraftDato")
    protected Date bevisIkraftDato;

    @XmlElement
    @Column(name = "forsikringsinstans")
    protected String forsikringsinstans;

    @XmlElement
    @Column(name = "forsikringsinstansKode")
    protected String forsikringsinstansKode;

    @XmlElement
    @Column(name = "forsikringsnummer")
    protected String forsikringsnummer;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sslGyldigFra")
    protected Date sslGyldigFra;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sslGyldigTil")
    protected Date sslGyldigTil;

    @XmlElement
    @Column(name = "socialLand")
    protected String socialLand;

    @XmlElement
    @Column(name = "socialLandKode")
    protected String socialLandKode;
    /*

    ValidTo DATETIME,
    CreatedDate DATETIME
    NOT NULL,


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
