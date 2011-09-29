package dk.nsi.dgws;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletRequest;

import dk.sosi.seal.model.SystemIDCard;


public class DGWSFilterSystemIDCardProvider implements Provider<SystemIDCard>
{
	private final Provider<ServletRequest> request;


	@Inject
	DGWSFilterSystemIDCardProvider(Provider<ServletRequest> request)
	{
		this.request = request;
	}


	@Override
	public SystemIDCard get()
	{
		return (SystemIDCard) request.get().getAttribute(DgwsIdcardFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);
	}
}
