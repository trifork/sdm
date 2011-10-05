package dk.nsi.stamdata.cpr.jaxws;

import com.google.common.collect.Lists;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.List;


public class SealNamespaceResolver implements HandlerResolver
{
	@Override
	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo)
	{
		return Lists.newArrayList((Handler) new SealNamespacePrefixSoapHandler());
	}
}
