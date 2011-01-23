package com.trifork.stamdata.importer.jobs.autorisationsregisteret;


import java.util.Date;

import com.trifork.stamdata.persistence.CompleteDataset;
import com.trifork.stamdata.registre.autorisation.Autorisation;
import com.trifork.stamdata.util.DateUtils;


public class Autorisationsregisterudtraek extends CompleteDataset<Autorisation>
{

	public Autorisationsregisterudtraek(Date validFrom)
	{

		super(Autorisation.class, validFrom, DateUtils.FOREVER);
	}


	@Override
	public void addRecord(Autorisation aut)
	{

		aut.setValidFrom(getValidFrom());
		super.addRecord(aut);
	}
}
