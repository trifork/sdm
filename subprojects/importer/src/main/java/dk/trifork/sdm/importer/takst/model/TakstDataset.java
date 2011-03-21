package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.CompleteDataset;
import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.List;

public class TakstDataset<T extends TakstEntity> extends CompleteDataset<T> {
    private Takst takst;
    Logger logger = Logger.getLogger(getClass());

    public TakstDataset(Takst takst, List<T> entities, Class<T> type) {
        super(type, entities, takst.getValidFrom(), takst.getValidTo());
        for (TakstEntity entity : entities) {
            entity.takst = takst;
        }
        this.takst = takst;
    }

    @Override
    public Calendar getValidFrom() {
        return takst.getValidFrom();
    }

    @Override
    public Calendar getValidTo() {
        return takst.getValidTo();
    }

    @Override
    public void addEntity(T entity) {
        super.addEntity(entity);
        entity.takst = takst;

    }


}
