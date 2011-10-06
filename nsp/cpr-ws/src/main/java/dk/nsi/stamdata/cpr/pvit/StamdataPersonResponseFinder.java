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
package dk.nsi.stamdata.cpr.pvit;

import com.google.common.collect.Maps;
import com.trifork.stamdata.Fetcher;
import dk.nsi.stamdata.cpr.PersonMapper;
import dk.nsi.stamdata.cpr.models.Person;
import dk.nsi.stamdata.cpr.ws.NamePersonQueryType;
import dk.nsi.stamdata.cpr.ws.PersonInformationStructureType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class StamdataPersonResponseFinder {
	private final static Logger logger = LoggerFactory.getLogger(StamdataPersonResponseFinder.class);

	private final Fetcher fetcher;
	private final PersonMapper personMapper;

	@Inject
	StamdataPersonResponseFinder(Fetcher fetcher, PersonMapper personMapper) {
		this.fetcher = fetcher;
		this.personMapper = personMapper;
	}

	protected PersonLookupResponseType answerCprRequest(String cvr, String cpr) throws SQLException, DatatypeConfigurationException {

		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		Person person = fetcher.fetch(Person.class, cpr);
		boolean wasFound = (person != null);

		logger.info("type=auditlog, client_cvr={}, requested_cpr={}, record_was_returned={}", new Object[]{cvr, cpr, wasFound});

		if (wasFound) {
			personInformationStructure.add(personMapper.map(person, PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities, PersonMapper.CPRProtectionLevel.DoNotCensorCPR));
		}

		return response;
	}

	protected PersonLookupResponseType answerCivilRegistrationNumberListPersonRequest(String cvr, List<String> civilRegistrationNumberList) throws SQLException {
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		for (String cpr : civilRegistrationNumberList) {
			Person person = fetcher.fetch(Person.class, cpr);
			boolean wasFound = (person != null);

			logger.info("type=auditlog, client_cvr={}, requested_cpr={}, record_was_returned={}", new Object[]{cvr, cpr, wasFound});

			if (wasFound) {
				personInformationStructure.add(personMapper.map(person, PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities, PersonMapper.CPRProtectionLevel.DoNotCensorCPR));
			}
		}

		return response;
	}

	protected PersonLookupResponseType answerBirthDatePersonRequest(String cvr, XMLGregorianCalendar birthDate) throws SQLException, DatatypeConfigurationException {
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		List<Person> persons = fetcher.fetch(Person.class, "Foedselsdato", birthDate.toGregorianCalendar().getTime());

		logger.info("type=auditlog, client_cvr={}, search_birthday_param={}", cvr, birthDate.toGregorianCalendar());

		for (Person person : persons) {
			logger.info("type=auditlog, client_cvr={}, cpr_of_returned_person={}", cvr, person.getCpr());

			personInformationStructure.add(personMapper.map(person, PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities, PersonMapper.CPRProtectionLevel.CensorCPR));
		}

		return response;
	}

	protected PersonLookupResponseType answerNamePersonRequest(String cvr, NamePersonQueryType namePerson) throws SQLException, DatatypeConfigurationException {
		PersonLookupResponseType response = new PersonLookupResponseType();
		List<PersonInformationStructureType> personInformationStructure = response.getPersonInformationStructure();

		Map<String, Object> columnValuePairs = Maps.newHashMap();

		columnValuePairs.put("Fornavn", namePerson.getPersonGivenName());
		columnValuePairs.put("Efternavn", namePerson.getPersonSurnameName());

		if (!StringUtils.isBlank(namePerson.getPersonMiddleName())) {
			columnValuePairs.put("Mellemnavn", namePerson.getPersonMiddleName());
		}

		logger.info("type=auditlog, client_cvr={}, requested_name={} {} {}", new Object[]{cvr, namePerson.getPersonGivenName(), namePerson.getPersonMiddleName(), namePerson.getPersonSurnameName()});

		for (Person person : fetcher.fetch(Person.class, columnValuePairs)) {
			logger.info("type=auditlog, client_cvr={}, cvr_of_returned_person={}", cvr, person.getCpr());

			personInformationStructure.add(personMapper.map(person, PersonMapper.ServiceProtectionLevel.CensorProtectedDataForNonAuthorities, PersonMapper.CPRProtectionLevel.CensorCPR));
		}

		return response;
	}
}