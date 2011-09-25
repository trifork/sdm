package dk.nsi.stamdata.cpr.pvit;

import java.util.Set;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import com.google.inject.Inject;

import dk.nsi.stamdata.cpr.annotations.Whitelist;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;
import dk.sosi.seal.model.SystemIDCard;

@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
	private final Set<String> whitelist;
	private final SystemIDCard idCard;

	@Inject
	StamdataPersonLookupImpl(@Whitelist Set<String> whitelist, SystemIDCard idCard)
	{
		this.whitelist = whitelist;
		this.idCard = idCard;
	}
	
	@Override
	public PersonLookupResponseType getPersonDetails(Holder<Security> wsseHeader, Holder<Header> medcomHeader, PersonLookupRequestType parameters) throws DGWSFault
	{
		return null;
	}
}
