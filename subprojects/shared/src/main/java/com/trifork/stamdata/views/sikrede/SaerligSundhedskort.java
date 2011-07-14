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
@ViewPath("sikrede/saerligsundhedskort/v1")
@Table(name = "SaerligSundhedskort")
public class SaerligSundhedskort extends View {
    @Id
    @GeneratedValue
    @XmlTransient
    @Column(name = "SaerligSundhedskortPID")
    protected BigInteger recordID;

    @XmlElement(required = true)
    @Column(name = "CPR")
    protected String cpr;

    @Temporal(TIMESTAMP)
    @Column(name = "ValidFrom")
    protected Date validFrom;

    @Temporal(TIMESTAMP)
    @Column(name = "ValidTo")
    protected Date validTo;

    @XmlTransient
    @Temporal(TIMESTAMP)
    @Column(name = "ModifiedDate")
    protected Date modifiedDate;

    @XmlElement
    @Column(name = "adresseLinje1")
    protected String adresseLinje1;

    @XmlElement
    @Column(name = "adresseLinje2")
    protected String adresseLinje2;

    @XmlElement
    @Column(name = "bopelsLand")
    protected String bopelsLand;

    @XmlElement
    @Column(name = "bopelsLandKode")
    protected String bopelsLandKode;

    @XmlElement
    @Column(name = "emailAdresse")
    protected String emailAdresse;

    @XmlElement
    @Column(name = "familieRelationCpr")
    protected String familieRelationCpr;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "foedselsDato")
    protected Date foedselsDato;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sskGyldigFra")
    protected Date sskGyldigFra;

    @XmlElement
    @Temporal(DATE)
    @Column(name = "sskGyldigTil")
    protected Date sskGyldigTil;

    @XmlElement
    @Column(name = "mobilNummer")
    protected String mobilNummer;

    @XmlElement
    @Column(name = "postnummerBy")
    protected String postnummerBy;

    
/*
	CreatedDate DATETIME NOT NULL,
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
