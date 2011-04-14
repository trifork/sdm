package dk.trifork.sdm.importer.cpr.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dk.trifork.sdm.model.Dataset;
import dk.trifork.sdm.model.StamdataEntity;

public class CPRDataset {
	private final List<Dataset<? extends StamdataEntity>> datasets = new ArrayList<Dataset<? extends StamdataEntity>>() {{
		add(new Dataset<Personoplysninger>(Personoplysninger.class));
		add(new Dataset<Klarskriftadresse>(Klarskriftadresse.class));
		add(new Dataset<NavneBeskyttelse>(NavneBeskyttelse.class));
		add(new Dataset<Navneoplysninger>(Navneoplysninger.class));
		add(new Dataset<UmyndiggoerelseVaergeRelation>(UmyndiggoerelseVaergeRelation.class));
		add(new Dataset<ForaeldreMyndighedRelation>(ForaeldreMyndighedRelation.class));
		add(new Dataset<BarnRelation>(BarnRelation.class));
		add(new Dataset<Folkekirkeoplysninger>(Folkekirkeoplysninger.class));
		add(new Dataset<Udrejseoplysninger>(Udrejseoplysninger.class));
		add(new Dataset<Valgoplysninger>(Valgoplysninger.class));
		add(new Dataset<Foedselsregistreringsoplysninger>(Foedselsregistreringsoplysninger.class));
		add(new Dataset<Statsborgerskab>(Statsborgerskab.class));
		add(new Dataset<KommunaleForhold>(KommunaleForhold.class));
		add(new Dataset<AktuelCivilstand>(AktuelCivilstand.class));
		add(new Dataset<Haendelse>(Haendelse.class));
	}};

	private Calendar validFrom, previousFileValidFrom; 

	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	public Calendar getPreviousFileValidFrom() {
		return previousFileValidFrom;
	}

	public void setPreviousFileValidFrom(Calendar previousFileValidFrom) {
		this.previousFileValidFrom = previousFileValidFrom;
	}

	public <T extends CPREntity > void addEntity(T entity) {
		entity.setDataset(this);
		for (Dataset<? extends StamdataEntity> dataset : datasets) {
			if (dataset.getType().equals(entity.getClass())) {
				@SuppressWarnings("unchecked")
				Dataset<T> typedDataset = (Dataset<T>) dataset;
				typedDataset.addEntity(entity);
			}
		}
	}

	public List<Dataset<? extends StamdataEntity>> getDatasets() {
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
