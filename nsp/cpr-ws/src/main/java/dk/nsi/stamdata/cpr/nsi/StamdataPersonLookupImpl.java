package dk.nsi.stamdata.cpr.nsi;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;

@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
	@Override
	public PersonLookupResponseType getPersonDetails(Holder<Security> wsseHeader, Holder<Header> medcomHeader, PersonLookupRequestType parameters) throws DGWSFault
	{
		return null;
	}
}
