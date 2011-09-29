package dk.nsi.stamdata.cpr.pvit;

import javax.jws.WebService;
import javax.xml.ws.Holder;

import com.google.inject.Inject;

import dk.nsi.stamdata.cpr.jaxws.GuiceInstanceResolver.GuiceWebservice;
import dk.nsi.stamdata.cpr.ws.DGWSFault;
import dk.nsi.stamdata.cpr.ws.Header;
import dk.nsi.stamdata.cpr.ws.PersonLookupRequestType;
import dk.nsi.stamdata.cpr.ws.PersonLookupResponseType;
import dk.nsi.stamdata.cpr.ws.Security;
import dk.nsi.stamdata.cpr.ws.StamdataPersonLookup;
import dk.sosi.seal.model.SystemIDCard;

@GuiceWebservice
@WebService(serviceName = "StamdataPersonLookup", endpointInterface = "dk.nsi.stamdata.cpr.ws.StamdataPersonLookup")
public class StamdataPersonLookupImpl implements StamdataPersonLookup
{
	private static final boolean ALWAYS_CENSOR_PROTECTED_DATA = false;
	private final SystemIDCard idCard;

	@Inject
	StamdataPersonLookupImpl(SystemIDCard idCard)
	{
		this.idCard = idCard;
	}

	@Override
	public PersonLookupResponseType getPersonDetails(Holder<Security> wsseHeader, Holder<Header> medcomHeader, PersonLookupRequestType parameters) throws DGWSFault
	{
		return null; 
	}
}
