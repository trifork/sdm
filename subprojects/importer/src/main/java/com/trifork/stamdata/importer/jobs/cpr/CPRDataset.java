package com.trifork.stamdata.importer.jobs.cpr;


import java.util.Date;

import com.trifork.stamdata.persistence.Dataset;
import com.trifork.stamdata.registre.cpr.BarnRelation;
import com.trifork.stamdata.registre.cpr.CPRRecord;
import com.trifork.stamdata.registre.cpr.Foraeldremyndighedsrelation;
import com.trifork.stamdata.registre.cpr.Klarskriftadresse;
import com.trifork.stamdata.registre.cpr.Navnebeskyttelse;
import com.trifork.stamdata.registre.cpr.Navneoplysninger;
import com.trifork.stamdata.registre.cpr.Personoplysninger;
import com.trifork.stamdata.registre.cpr.UmyndiggoerelseVaergeRelation;


public class CPRDataset
{

	private Dataset<Personoplysninger> personoplysninger = new Dataset<Personoplysninger>(Personoplysninger.class);
	private Dataset<Klarskriftadresse> klarskriftadresse = new Dataset<Klarskriftadresse>(Klarskriftadresse.class);
	private Dataset<Navnebeskyttelse> navneBeskyttelse = new Dataset<Navnebeskyttelse>(Navnebeskyttelse.class);
	private Dataset<Navneoplysninger> navneoplysninger = new Dataset<Navneoplysninger>(Navneoplysninger.class);
	private Dataset<UmyndiggoerelseVaergeRelation> umyndiggoerelseVaergeRelation = new Dataset<UmyndiggoerelseVaergeRelation>(
			UmyndiggoerelseVaergeRelation.class);
	private Dataset<Foraeldremyndighedsrelation> foraeldreMyndighedRelation = new Dataset<Foraeldremyndighedsrelation>(
			Foraeldremyndighedsrelation.class);
	private Dataset<BarnRelation> barnRelation = new Dataset<BarnRelation>(BarnRelation.class);

	private Date validFrom, previousFileValidFrom;


	public Date getEffectuationDate()
	{

		return validFrom;
	}


	public void setValidFrom(Date validFrom)
	{

		this.validFrom = validFrom;
	}


	public Date getExpectedPreviousVersion()
	{

		return previousFileValidFrom;
	}


	public void setPreviousFileValidFrom(Date previousFileValidFrom)
	{

		this.previousFileValidFrom = previousFileValidFrom;
	}


	public void addEntity(CPRRecord entity)
	{

		entity.setValidFrom(getEffectuationDate());

		if (entity instanceof Personoplysninger)
			personoplysninger.addRecord((Personoplysninger) entity);
		else if (entity instanceof Klarskriftadresse)
			klarskriftadresse.addRecord((Klarskriftadresse) entity);
		else if (entity instanceof Navnebeskyttelse)
			navneBeskyttelse.addRecord((Navnebeskyttelse) entity);
		else if (entity instanceof Navneoplysninger)
			navneoplysninger.addRecord((Navneoplysninger) entity);
		else if (entity instanceof UmyndiggoerelseVaergeRelation)
			umyndiggoerelseVaergeRelation.addRecord((UmyndiggoerelseVaergeRelation) entity);
		else if (entity instanceof Foraeldremyndighedsrelation)
			foraeldreMyndighedRelation.addRecord((Foraeldremyndighedsrelation) entity);
		else if (entity instanceof BarnRelation) barnRelation.addRecord((BarnRelation) entity);
	}


	public Dataset<Personoplysninger> getPersonoplysninger()
	{

		return personoplysninger;
	}


	public Dataset<Klarskriftadresse> getKlarskriftadresse()
	{

		return klarskriftadresse;
	}


	public Dataset<Navnebeskyttelse> getNavneBeskyttelse()
	{

		return navneBeskyttelse;
	}


	public Dataset<Navneoplysninger> getNavneoplysninger()
	{

		return navneoplysninger;
	}


	public Dataset<UmyndiggoerelseVaergeRelation> getUmyndiggoerelseVaergeRelation()
	{

		return umyndiggoerelseVaergeRelation;
	}


	public Dataset<Foraeldremyndighedsrelation> getForaeldreMyndighedRelation()
	{

		return foraeldreMyndighedRelation;
	}


	public Dataset<BarnRelation> getBarnRelation()
	{

		return barnRelation;
	}
}
