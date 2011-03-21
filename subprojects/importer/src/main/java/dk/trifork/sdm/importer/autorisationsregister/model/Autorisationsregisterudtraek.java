package dk.trifork.sdm.importer.autorisationsregister.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.CompleteDataset;

import java.util.Calendar;


public class Autorisationsregisterudtraek extends CompleteDataset<Autorisation> {

	public Autorisationsregisterudtraek(Calendar validFrom) {

		super(Autorisation.class, validFrom, AbstractStamdataEntity.FUTURE);
	}

	@Override
	public void addEntity(Autorisation aut) {
		aut.dataset = this;
		super.addEntity(aut);
	}
}
