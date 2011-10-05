package dk.nsi.stamdata.cpr.integrationtest.dgws;

import java.util.List;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;

import com.google.common.collect.Lists;


public class SealNamespaceResolver implements HandlerResolver
{
	@Override
	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo)
	{
		return Lists.newArrayList((Handler) new SealNamespacePrefixSoapHandler());
	}
}
