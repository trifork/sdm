// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package dk.trifork.sdm.importer.sor;

import org.xml.sax.helpers.DefaultHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import dk.trifork.sdm.importer.sor.model.*;
import dk.trifork.sdm.importer.sor.xmlmodel.*;
import dk.trifork.sdm.model.CompleteDataset;
import dk.trifork.sdm.util.DateUtils;

public class SOREventHandler extends DefaultHandler {
	private static Logger logger = LoggerFactory.getLogger(SOREventHandler.class);

    private String elementValue;
	
	private InstitutionOwnerEntity curIOE;
	private HealthInstitutionEntity curHIE;
	private OrganizationalUnitEntity curOUE;
	
	private SORDataSets dataSets;
	
	public SOREventHandler(SORDataSets dataSets) {
		super();
		this.dataSets = dataSets;
	}

	private void createDatasets(Calendar snapshotDate) {
		dataSets.setApotekDS(new CompleteDataset<Apotek>(Apotek.class, snapshotDate, DateUtils.FUTURE));
		dataSets.setYderDS(new CompleteDataset<Yder>(Yder.class, snapshotDate, DateUtils.FUTURE));
		dataSets.setPraksisDS(new CompleteDataset<Praksis>(Praksis.class, snapshotDate, DateUtils.FUTURE));
		dataSets.setSygehusDS(new CompleteDataset<Sygehus>(Sygehus.class, snapshotDate, DateUtils.FUTURE));
		dataSets.setSygehusAfdelingDS(new CompleteDataset<SygehusAfdeling>(SygehusAfdeling.class, snapshotDate, DateUtils.FUTURE));
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		elementValue = new String();
		if ("InstitutionOwnerEntity".equals(qName)) {
			curIOE = new InstitutionOwnerEntity();
		}
		if ("HealthInstitutionEntity".equals(qName)) {
			curHIE = new HealthInstitutionEntity();
			curIOE.setHealthInstitutionEntity(curHIE);
		}
		if ("OrganizationalUnitEntity".equals(qName)) {
			if (curOUE == null) {
				// Father is a HealthInstitutionEntity
				curOUE = new OrganizationalUnitEntity(null);
				curHIE.setOrganizationalUnitEntity(curOUE);
				curOUE.setHealthInstitutionEntity(curHIE);
			} else {
				// Father is a OrganizationalUnitEntity
				curOUE = new OrganizationalUnitEntity(curOUE);
			}
			curOUE.setBelongsTo(curHIE);
		}
		
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("SnapshotDate".equals(qName)) {
			try {
				createDatasets(toCalendar(elementValue));
			} catch (ParseException e) {
				throw(new SAXException(e));
			}
		} else if ("InstitutionOwnerEntity".equals(qName)) {
			denormalizeAdress(curIOE);
			for (HealthInstitutionEntity hie : curIOE.getHealthInstitutionEntity()) {
				if (hie.getInstitutionType() == 394761003L || hie.getInstitutionType() == 550671000005100L) {
					// Lægepraksis og special læger
					Praksis p = XMLModelMapper.toPraksis(hie);
					for (OrganizationalUnitEntity oue : hie.getOrganizationalUnitEntities()) {
						// Yder for Lægepraksis
						Yder y = XMLModelMapper.toYder(oue);
						dataSets.getYderDS().addEntity(y);
						if (!oue.getSons().isEmpty()) {
							new SAXException("Lægepraksis skal ikke have en level 2 OrganizationalUnitEntity. SORId=" + oue.getSorIdentifier() + " har!!");
						}
					}
					dataSets.getPraksisDS().addEntity(p);
				} else if (hie.getInstitutionType() == 22232009L) {
					// Sygehus
					Sygehus s = XMLModelMapper.toSygehus(hie);
						
					for (OrganizationalUnitEntity oue : hie.getOrganizationalUnitEntities()) {
						addAfdelinger(oue);
					}
					dataSets.getSygehusDS().addEntity(s);
				} else if (hie.getInstitutionType() == 264372000L) {
					// Apotek
					for (OrganizationalUnitEntity oue : hie.getOrganizationalUnitEntities()) {
						Apotek a = XMLModelMapper.toApotek(oue);
						dataSets.getApotekDS().addEntity(a);
					}
				}
			}
			curIOE = null;
		} else if ("HealthInstitutionEntity".equals(qName)) {
			curHIE = null;
		} else if ("OrganizationalUnitEntity".equals(qName)) {
			curOUE = curOUE.getParrent(); 
		} else {
			try {
				setProperty(stripNS(qName), elementValue);
			} catch (Exception e) {
				throw(new SAXException(e));
			}
		}
	}
	
	private void addAfdelinger(OrganizationalUnitEntity oue) {
		if (oue.getShakIdentifier() != null) {
			// Ignore all 'SygehusAfdeling' with no SKS
			SygehusAfdeling sa = XMLModelMapper.toSygehusAfdeling(oue);
			dataSets.getSygehusAfdelingDS().addEntity(sa);
			
			for (OrganizationalUnitEntity soue : oue.getSons()) {
				addAfdelinger(soue);
			}
		}
	}

	private static String stripNS(String qName) {
		return (qName.indexOf(':') != -1) ? qName.substring(qName.indexOf(':')+1) : qName;
	}
	
	private static void denormalizeAdress(InstitutionOwnerEntity ioe) {
		for (HealthInstitutionEntity hie : ioe.getHealthInstitutionEntity()) {
			pushdownAdress(ioe, hie);
			for (OrganizationalUnitEntity oue : hie.getOrganizationalUnitEntities()) {
				pushdownAdress(hie, oue);
				for (OrganizationalUnitEntity son : oue.getSons()) {
					pushdownAdress(oue, son);
				}
			}
		}
	}	
	private static void pushdownAdress(AddressInformation parrent, AddressInformation son) {
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
		if (son.isEntityInheritedIndicator() != null && son.isEntityInheritedIndicator()) {
			son.setEanLocationCode(parrent.getEanLocationCode());
		}
		
	}

	@Override
	public void characters(char[] chars, int start, int length) {
		elementValue += new String(chars, start, length);
	}
	
	private boolean setProperty(String qName, String value) throws Exception {
		boolean found = false;
		Method method = null;
		Object object = null;
		try {
			if (curOUE != null) {
				object = curOUE; 
			} else if (curHIE != null) {
				object = curHIE;
			} else if (curIOE != null) {
				object = curIOE;
			}
			
			if (object != null) {
				Class<?> target = object.getClass();
				while (target != null && method == null) {
					Method methods[] = target.getDeclaredMethods();
					for (Method prop : methods) {
						if (prop.getName().equals("set" + qName)) {
							method = prop;		
							break;
						}
					}
					target = target.getSuperclass();
				}
			}
			if (method != null) {
				Class<?> param = method.getParameterTypes()[0];
				try {
					
					if (param.isAssignableFrom(String.class)) {
						method.invoke(object, value);
						found = true;
					} else if (param.isAssignableFrom(Long.class)) {
						Long convValue = null;
						try {
							convValue = Long.parseLong(value);
						} catch (NumberFormatException e) {
							logger.error("Numberformat exception on property " + qName + ", method " + object.getClass().getName() + "." + method.getName() + "(" + param.getName() + ")", e);
							throw e;
						}
						method.invoke(object, convValue);
						found = true;
					} else if (param.isAssignableFrom(Calendar.class)) {
						Calendar convValue = null;
						convValue = toCalendar(value);
						method.invoke(object, convValue);
						found = true;
					} else if (param.isAssignableFrom(Boolean.class)) {
						Boolean b = ("true".equalsIgnoreCase(value)) ? new Boolean(true) : new Boolean(false);
						method.invoke(object, b);
					} else {
						logger.error("Unsupported datatype for property " + qName + ", datatype was " + param.getClass().getName());
						throw new Exception("Unsupported datatype for property " + qName + ", datatype was " + param.getClass().getName());
					}
				} catch (IllegalArgumentException e) {
					logger.error("Illegal argument exception on property " + qName + ", method " + object.getClass().getName() + "." + method.getName() + "(" + param.getName() + ")", e);
					throw e;
				} catch (IllegalAccessException e) {
					logger.error("Illegal access exception on property " + qName + ", method " + object.getClass().getName() + "." + method.getName() + "(" + param.getName() + ")", e);
					throw e;
				} catch (InvocationTargetException e) {
					logger.error("Invocation target exception on property " + qName + ", method " + object.getClass().getName() + "." + method.getName() + "(" + param.getName() + ")", e);
					throw e;
				}
			}
			
		} catch (SecurityException e) {
			logger.error("Security exception on property " + qName, e);
			throw e;
		}
		return found;
	}

	public static Calendar toCalendar(String xmlDate) throws ParseException {
		String datePattern = "yyyy-MM-dd";
		DateFormat df = new SimpleDateFormat(datePattern);
		Calendar cal = null;
		try {
			Date sn = df.parse(xmlDate);
			cal = DateUtils.toCalendar(sn);
		} catch (ParseException e) {
			logger.error("Invalid date found in xml. Expected  " + datePattern + " found " + xmlDate, e);
			throw e;
		}
		return cal;
	}
}
