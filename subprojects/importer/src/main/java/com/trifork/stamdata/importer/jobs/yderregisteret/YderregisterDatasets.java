package com.trifork.stamdata.importer.jobs.yderregisteret;


import java.util.Date;

import com.trifork.stamdata.persistence.CompleteDataset;
import com.trifork.stamdata.registre.yder.Yderregister;
import com.trifork.stamdata.registre.yder.YderregisterPerson;
import com.trifork.stamdata.util.DateUtils;


public class YderregisterDatasets
{
	private final CompleteDataset<Yderregister> yderregisterDS;
	private final CompleteDataset<YderregisterPerson> yderregisterPersonDS;


	public YderregisterDatasets(Date validFrom)
	{

		yderregisterDS = new CompleteDataset<Yderregister>(Yderregister.class, validFrom, DateUtils.FOREVER);
		yderregisterPersonDS = new CompleteDataset<YderregisterPerson>(YderregisterPerson.class, validFrom,
				DateUtils.FOREVER);
	}


	public CompleteDataset<Yderregister> getYderregisterDS()
	{

		return yderregisterDS;
	}


	public CompleteDataset<YderregisterPerson> getYderregisterPersonDS()
	{

		return yderregisterPersonDS;
	}


	public void addYderregister(Yderregister entity)
	{

		yderregisterDS.addRecord(entity);
	}


	public void addYderregisterPerson(YderregisterPerson entity)
	{

		yderregisterPersonDS.addRecord(entity);
	}
}
