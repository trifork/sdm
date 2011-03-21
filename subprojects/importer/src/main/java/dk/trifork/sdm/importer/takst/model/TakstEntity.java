package dk.trifork.sdm.importer.takst.model;

import java.util.Calendar;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.util.DateUtils;

public abstract class TakstEntity extends AbstractStamdataEntity {
    protected Takst takst;

    @Override
    public Calendar getValidFrom() {
        return takst.getValidFrom();
    }

    @Override
    public Calendar getValidTo() {
        return DateUtils.FUTURE;
    }
}
