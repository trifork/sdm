package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.persistence.Output;

import java.util.Date;

@Output(name = "Sikrede")
public class Sikrede extends AbstractStamdataEntity {

    private String cpr;
    

    @Override
    public Date getValidFrom() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setCpr(String cpr) {
        this.cpr = cpr;
    }
}
