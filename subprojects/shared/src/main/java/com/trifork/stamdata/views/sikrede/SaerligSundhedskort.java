package com.trifork.stamdata.views.sikrede;

import com.trifork.stamdata.views.View;
import com.trifork.stamdata.views.ViewPath;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigInteger;
import java.util.Date;

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

    @XmlTransient
    @Temporal(TIMESTAMP)
    @Column(name = "ModifiedDate")
    protected Date modifiedDate;


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
