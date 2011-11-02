/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */


package com.trifork.stamdata.importer.jobs.sor;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.trifork.stamdata.importer.jobs.sor.model.Apotek;
import com.trifork.stamdata.importer.jobs.sor.model.Praksis;
import com.trifork.stamdata.importer.jobs.sor.model.Sygehus;
import com.trifork.stamdata.importer.jobs.sor.model.SygehusAfdeling;
import com.trifork.stamdata.importer.jobs.sor.model.Yder;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.AddressInformation;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.HealthInstitutionEntity;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.InstitutionOwnerEntity;
import com.trifork.stamdata.importer.jobs.sor.xmlmodel.OrganizationalUnitEntity;
import com.trifork.stamdata.importer.persistence.CompleteDataset;
import com.trifork.stamdata.importer.util.Dates;


public class SOREventHandler extends DefaultHandler
{
	private String elementValue;

	private InstitutionOwnerEntity curIOE;
	private HealthInstitutionEntity curHIE;
	private OrganizationalUnitEntity curOUE;

	private SORDataSets dataSets;

	public SOREventHandler(SORDataSets dataSets)
	{
		this.dataSets = dataSets;
	}

	private void createDatasets(Date snapshotDate)
	{
		dataSets.setApotekDS(new CompleteDataset<Apotek>(Apotek.class, snapshotDate, Dates.THE_END_OF_TIME));
		dataSets.setYderDS(new CompleteDataset<Yder>(Yder.class, snapshotDate, Dates.THE_END_OF_TIME));
		dataSets.setPraksisDS(new CompleteDataset<Praksis>(Praksis.class, snapshotDate, Dates.THE_END_OF_TIME));
		dataSets.setSygehusDS(new CompleteDataset<Sygehus>(Sygehus.class, snapshotDate, Dates.THE_END_OF_TIME));
		dataSets.setSygehusAfdelingDS(new CompleteDataset<SygehusAfdeling>(SygehusAfdeling.class, snapshotDate, Dates.THE_END_OF_TIME));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException
	{
		elementValue = new String();

		if ("InstitutionOwnerEntity".equals(qName))
		{
			curIOE = new InstitutionOwnerEntity();
		}
		if ("HealthInstitutionEntity".equals(qName))
		{
			curHIE = new HealthInstitutionEntity();
			curIOE.setHealthInstitutionEntity(curHIE);
		}
		if ("OrganizationalUnitEntity".equals(qName))
		{
			if (curOUE == null)
			{
				// Father is a HealthInstitutionEntity
				curOUE = new OrganizationalUnitEntity(null);
				curHIE.setOrganizationalUnitEntity(curOUE);
				curOUE.setHealthInstitutionEntity(curHIE);
			}
			else
			{
				// Father is a OrganizationalUnitEntity
				curOUE = new OrganizationalUnitEntity(curOUE);
			}

			curOUE.setBelongsTo(curHIE);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException
	{
		if ("SnapshotDate".equals(qName))
		{
			try
			{
				createDatasets(parseXSDDate(elementValue));
			}
			catch (ParseException e)
			{
				throw new SAXException(e);
			}
		}
		else if ("InstitutionOwnerEntity".equals(qName))
		{
			denormalizeAdress(curIOE);

			for (HealthInstitutionEntity institutuinEntity : curIOE.getHealthInstitutionEntity())
			{
				if (institutuinEntity.getInstitutionType() == 394761003L || institutuinEntity.getInstitutionType() == 550671000005100L)
				{
					// Lægepraksis og special læger.

					Praksis praksis = XMLModelMapper.toPraksis(institutuinEntity);
					for (OrganizationalUnitEntity oue : institutuinEntity.getOrganizationalUnitEntities())
					{
						// Yder for Lægepraksis.

						Yder yder = XMLModelMapper.toYder(oue);
						dataSets.getYderDS().addEntity(yder);

						if (!oue.getSons().isEmpty())
						{
							new SAXException("Lægepraksis skal ikke have en level 2 OrganizationalUnitEntity. SORId=" + oue.getSorIdentifier() + " har!!");
						}
					}

					dataSets.getPraksisDS().addEntity(praksis);
				}
				else if (institutuinEntity.getInstitutionType() == 22232009L)
				{
					// Sygehus.

					Sygehus s = XMLModelMapper.toSygehus(institutuinEntity);

					for (OrganizationalUnitEntity oue : institutuinEntity.getOrganizationalUnitEntities())
					{
						addAfdelinger(oue);
					}

					dataSets.getSygehusDS().addEntity(s);
				}
				else if (institutuinEntity.getInstitutionType() == 264372000L)
				{
					// Apotek.

					for (OrganizationalUnitEntity oue : institutuinEntity.getOrganizationalUnitEntities())
					{
						Apotek a = XMLModelMapper.toApotek(oue);
						dataSets.getApotekDS().addEntity(a);
					}
				}
			}

			curIOE = null;
		}
		else if ("HealthInstitutionEntity".equals(qName))
		{
			curHIE = null;
		}
		else if ("OrganizationalUnitEntity".equals(qName))
		{
			curOUE = curOUE.getParrent();
		}
		else
		{
			try
			{
				setProperty(stripNS(qName), elementValue);
			}
			catch (Exception e)
			{
				throw (new SAXException(e));
			}
		}
	}

	private void addAfdelinger(OrganizationalUnitEntity oue)
	{
		if (oue.getShakIdentifier() != null)
		{
			// Ignore all 'SygehusAfdeling' with no SKS
			SygehusAfdeling sa = XMLModelMapper.toSygehusAfdeling(oue);
			dataSets.getSygehusAfdelingDS().addEntity(sa);

			for (OrganizationalUnitEntity soue : oue.getSons())
			{
				addAfdelinger(soue);
			}
		}
	}

	private static String stripNS(String qName)
	{
		return (qName.indexOf(':') != -1) ? qName.substring(qName.indexOf(':') + 1) : qName;
	}

	private static void denormalizeAdress(InstitutionOwnerEntity ioe)
	{
		for (HealthInstitutionEntity hie : ioe.getHealthInstitutionEntity())
		{
			pushdownAdress(ioe, hie);
			for (OrganizationalUnitEntity oue : hie.getOrganizationalUnitEntities())
			{
				pushdownAdress(hie, oue);
				for (OrganizationalUnitEntity son : oue.getSons())
				{
					pushdownAdress(oue, son);
				}
			}
		}
	}

	private static void pushdownAdress(AddressInformation parrent, AddressInformation son)
	{
		if (parrent == null || son == null) return;
		if (son.getCountryIdentificationCode() == null) son.setCountryIdentificationCode(parrent.getCountryIdentificationCode());
		if (son.getDistrictName() == null) son.setDistrictName(parrent.getDistrictName());
		if (son.getEmailAddressIdentifier() == null) son.setEmailAddressIdentifier(parrent.getEmailAddressIdentifier());
		if (son.getFaxNumberIdentifier() == null) son.setFaxNumberIdentifier(parrent.getFaxNumberIdentifier());
		if (son.getPostCodeIdentifier() == null) son.setPostCodeIdentifier(parrent.getPostCodeIdentifier());
		if (son.getStreetBuildingIdentifier() == null) son.setStreetBuildingIdentifier(parrent.getStreetBuildingIdentifier());
		if (son.getStreetName() == null) son.setStreetName(parrent.getStreetName());
		if (son.getTelephoneNumberIdentifier() == null) son.setTelephoneNumberIdentifier(parrent.getTelephoneNumberIdentifier());
		if (son.getWebsite() == null) son.setWebsite(parrent.getWebsite());
		if (son.isEntityInheritedIndicator() != null && son.isEntityInheritedIndicator())
		{
			son.setEanLocationCode(parrent.getEanLocationCode());
		}
	}

	@Override
	public void characters(char[] chars, int start, int length)
	{
		elementValue += new String(chars, start, length);
	}

	private boolean setProperty(String qName, String value) throws Exception
	{
		boolean found = false;
		Method method = null;
		Object object = null;

		if (curOUE != null)
		{
			object = curOUE;
		}
		else if (curHIE != null)
		{
			object = curHIE;
		}
		else if (curIOE != null)
		{
			object = curIOE;
		}

		if (object != null)
		{
			Class<?> target = object.getClass();
			while (target != null && method == null)
			{
				Method methods[] = target.getDeclaredMethods();

				for (Method prop : methods)
				{
					if (prop.getName().equals("set" + qName))
					{
						method = prop;
						break;
					}
				}

				target = target.getSuperclass();
			}
		}

		if (method != null)
		{
			Class<?> param = method.getParameterTypes()[0];

			if (param.isAssignableFrom(String.class))
			{
				method.invoke(object, value);
				found = true;
			}
			else if (param.isAssignableFrom(Long.class))
			{
				Long convValue = Long.parseLong(value);

				method.invoke(object, convValue);
				found = true;
			}
			else if (param.isAssignableFrom(Date.class))
			{
				Date convValue = parseXSDDate(value);
				method.invoke(object, convValue);
				found = true;
			}
			else if (param.isAssignableFrom(Boolean.class))
			{
				Boolean b = Boolean.valueOf(value);
				method.invoke(object, b);
			}
			else
			{
				String message = "Unsupported datatype for property " + qName + ", expected datatype was " + param.getCanonicalName();
				throw new Exception(message);
			}
		}

		return found;
	}

	public static Date parseXSDDate(String xmlDate) throws ParseException
	{
		String datePattern = "yyyy-MM-dd";
		DateFormat df = new SimpleDateFormat(datePattern);

		return df.parse(xmlDate);
	}
}
