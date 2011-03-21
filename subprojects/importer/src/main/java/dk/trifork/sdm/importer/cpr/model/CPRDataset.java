package dk.trifork.sdm.importer.cpr.model;

import dk.trifork.sdm.model.Dataset;

import java.util.Calendar;

public class CPRDataset {
	
	private Dataset<Personoplysninger> personoplysninger = new Dataset<Personoplysninger>(Personoplysninger.class);
	private Dataset<Klarskriftadresse> klarskriftadresse = new Dataset<Klarskriftadresse>(Klarskriftadresse.class);
	private Dataset<NavneBeskyttelse> navneBeskyttelse = new Dataset<NavneBeskyttelse>(NavneBeskyttelse.class);
	private Dataset<Navneoplysninger> navneoplysninger = new Dataset<Navneoplysninger>(Navneoplysninger.class);
	private Dataset<UmyndiggoerelseVaergeRelation> umyndiggoerelseVaergeRelation = new Dataset<UmyndiggoerelseVaergeRelation>(UmyndiggoerelseVaergeRelation.class);
	private Dataset<ForaeldreMyndighedRelation> foraeldreMyndighedRelation = new Dataset<ForaeldreMyndighedRelation>(ForaeldreMyndighedRelation.class);
	private Dataset<BarnRelation> barnRelation = new Dataset<BarnRelation>(BarnRelation.class);

	
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

	public void addEntity(CPREntity entity) {
		entity.setDataset(this);
		if (entity instanceof Personoplysninger) personoplysninger.addEntity((Personoplysninger) entity);
		else if (entity instanceof Klarskriftadresse) klarskriftadresse.addEntity((Klarskriftadresse) entity);
		else if (entity instanceof NavneBeskyttelse) navneBeskyttelse.addEntity((NavneBeskyttelse) entity);
		else if (entity instanceof Navneoplysninger) navneoplysninger.addEntity((Navneoplysninger) entity);
		else if (entity instanceof UmyndiggoerelseVaergeRelation) umyndiggoerelseVaergeRelation.addEntity((UmyndiggoerelseVaergeRelation) entity);
		else if (entity instanceof ForaeldreMyndighedRelation) foraeldreMyndighedRelation.addEntity((ForaeldreMyndighedRelation) entity);
		else if (entity instanceof BarnRelation) barnRelation.addEntity((BarnRelation) entity);
	}

	public Dataset<Personoplysninger> getPersonoplysninger() {
		return personoplysninger;
	}

	public Dataset<Klarskriftadresse> getKlarskriftadresse() {
		return klarskriftadresse;
	}

	public Dataset<NavneBeskyttelse> getNavneBeskyttelse() {
		return navneBeskyttelse;
	}

	public Dataset<Navneoplysninger> getNavneoplysninger() {
		return navneoplysninger;
	}

	public Dataset<UmyndiggoerelseVaergeRelation> getUmyndiggoerelseVaergeRelation() {
		return umyndiggoerelseVaergeRelation;
	}

	public Dataset<ForaeldreMyndighedRelation> getForaeldreMyndighedRelation() {
		return foraeldreMyndighedRelation;
	}

	public Dataset<BarnRelation> getBarnRelation() {
		return barnRelation;
	}
	

}
