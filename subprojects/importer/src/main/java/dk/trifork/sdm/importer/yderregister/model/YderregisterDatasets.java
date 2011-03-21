package dk.trifork.sdm.importer.yderregister.model;

import java.util.Calendar;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.CompleteDataset;

public class YderregisterDatasets {
	private final CompleteDataset<Yderregister> yderregisterDS;
	private final CompleteDataset<YderregisterPerson> yderregisterPersonDS;
	
	public YderregisterDatasets(Calendar validFrom) {
		yderregisterDS = new CompleteDataset<Yderregister>(Yderregister.class, validFrom, AbstractStamdataEntity.FUTURE); 
		yderregisterPersonDS = new CompleteDataset<YderregisterPerson>(YderregisterPerson.class, validFrom, AbstractStamdataEntity.FUTURE); 
	}
	public CompleteDataset<Yderregister> getYderregisterDS() {
		return yderregisterDS;
	}
	public CompleteDataset<YderregisterPerson> getYderregisterPersonDS() {
		return yderregisterPersonDS;
	}
	public void addYderregister(Yderregister entity) {
		yderregisterDS.addEntity(entity);
	}
	public void addYderregisterPerson(YderregisterPerson entity) {
		yderregisterPersonDS.addEntity(entity);
	}
}

