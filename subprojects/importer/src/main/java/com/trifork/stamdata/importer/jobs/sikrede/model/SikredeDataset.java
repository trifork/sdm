package com.trifork.stamdata.importer.jobs.sikrede.model;

import com.trifork.stamdata.importer.persistence.AbstractStamdataEntity;
import com.trifork.stamdata.importer.persistence.Dataset;
import com.trifork.stamdata.importer.persistence.StamdataEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SikredeDataset {

    private final List<Dataset<? extends CPREntity>> datasets =
            (List<Dataset<? extends CPREntity>>) Arrays.asList(
                    new Dataset<Sikrede>(Sikrede.class),
                    new Dataset<SikredeYderRelation>(SikredeYderRelation.class),
                    new Dataset<SaerligSundhedskort>(SaerligSundhedskort.class)
            );

    private Date validFrom, previousFileValidFrom;

    public Date getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    public Date getPreviousFileValidFrom() {
        return previousFileValidFrom;
    }

    public void setPreviousFileValidFrom(Date previousFileValidFrom) {
        this.previousFileValidFrom = previousFileValidFrom;
    }

    public <T extends CPREntity> void addEntity(T entity) {
        entity.setDataset(this);
        for (Dataset<? extends StamdataEntity> dataset : datasets) {
            if (dataset.getType().equals(entity.getClass())) {
                @SuppressWarnings("unchecked")
                Dataset<T> typedDataset = (Dataset<T>) dataset;
                typedDataset.addEntity(entity);
            }
        }
    }

    public List<Dataset<? extends CPREntity>> getDatasets() {
        return datasets;
    }

    @SuppressWarnings("unchecked")
    public <T extends StamdataEntity> Dataset<T> getDataset(Class<T> entityClass) {
        for (Dataset<? extends StamdataEntity> dataset : datasets) {
            if (dataset.getType().equals(entityClass)) {
                return (Dataset<T>) dataset;
            }
        }

        throw new IllegalArgumentException("Ukendt entitetsklasse: " + entityClass);
    }
}
