package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.persistence.Output;

import java.util.Date;

/**
 * User: frj
 * Date: 7/12/11
 * Time: 11:56 AM
 *
 * @Author frj
 */
@Output(name="SaerligSundhedskort")
public class SaerligSundhedskort extends AbstractStamdataEntity {

    private String adresseLinje1;
    private String adresseLinje2;
    private String bopelsLand;
    private String bopelsLandKode;
    private String emailAdresse;
    private String familieRelation;
    private Date foedselsDato;
    private String mobilNummer;
    private String postNummerBy;

    @Override
    public Date getValidTo() {
        return super.getValidTo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public Date getValidFrom() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
