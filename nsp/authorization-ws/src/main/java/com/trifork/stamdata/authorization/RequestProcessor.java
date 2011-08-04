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

package com.trifork.stamdata.authorization;

import static com.trifork.stamdata.Preconditions.checkNotNull;
import static dk.sosi.seal.model.constants.FaultCodeValues.EXPIRED_IDCARD;
import static dk.sosi.seal.model.constants.FaultCodeValues.NOT_AUTHORIZED;
import static dk.sosi.seal.model.constants.FaultCodeValues.SECURITY_LEVEL_FAILED;
import static dk.sosi.seal.model.constants.FlowStatusValues.FLOW_FINALIZED_SUCCESFULLY;
import static dk.sosi.seal.model.constants.SubjectIdentifierTypeValues.CVR_NUMBER;

import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import com.google.inject.Inject;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.model.constants.FaultCodeValues;


public class RequestProcessor
{
	private final SOSIFactory sosiFactory;
	private final Set<String> whitelist;
	private final Marshaller marshaller;
	private final Unmarshaller unmarshaller;
	private final AuthorizationDao authorizationDao;

	@Inject
	RequestProcessor(SOSIFactory sosiFactory, Set<String> whitelist, Marshaller marshaller, Unmarshaller unmarshaller, AuthorizationDao authorizationDao)
	{
		this.marshaller = marshaller;
		this.unmarshaller = unmarshaller;
		this.authorizationDao = authorizationDao;
		this.sosiFactory = checkNotNull(sosiFactory);
		this.whitelist = checkNotNull(whitelist);
	}

	public Reply process(Request request) throws JAXBException, ParserConfigurationException
	{

		// AUTHENTICATE THE REQUEST

		if (!(request.getIDCard() instanceof SystemIDCard))
		{
			return sosiFactory.createNewErrorReply(request, SECURITY_LEVEL_FAILED, "This service required VOCES authentication.");
		}

		SystemIDCard idCard = (SystemIDCard) request.getIDCard();

		if (!idCard.isValidInTime())
		{
			return sosiFactory.createNewErrorReply(request, EXPIRED_IDCARD, "STS Token Expired.");
		}

		if (!CVR_NUMBER.equals(idCard.getSystemInfo().getCareProvider().getType()))
		{
			return sosiFactory.createNewErrorReply(request, NOT_AUTHORIZED, "This service requires a CVR care provider.");
		}

		// AUTHORIZE THE REQUEST

		String cvr = idCard.getSystemInfo().getCareProvider().getID();

		if (!whitelist.contains(cvr))
		{
			return sosiFactory.createNewErrorReply(request, NOT_AUTHORIZED, "The provided cvr is not authorized to access this service.");
		}

		// SERVICE THE REQUEST

		AuthorizationRequestStructure requestBody = unmarshaller.unmarshal(request.getBody(), AuthorizationRequestStructure.class).getValue();

		String cpr = requestBody.getCpr();

		if (requestBody != null && cpr == null)
		{

			return sosiFactory.createNewErrorReply(request, FaultCodeValues.PROCESSING_PROBLEM, "You must provide a CPR number in the SOAP body.");
		}

		List<Authorization> authorizations = authorizationDao.getAuthorizations(cpr);

		Reply reply = sosiFactory.createNewReply(request, FLOW_FINALIZED_SUCCESFULLY);

		Document replyXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

		AuthorizationResponseStructure response = new AuthorizationResponseStructure(cpr, authorizations);
		marshaller.marshal(response, replyXML);

		reply.setBody(replyXML.getDocumentElement());

		return reply;
	}
}
