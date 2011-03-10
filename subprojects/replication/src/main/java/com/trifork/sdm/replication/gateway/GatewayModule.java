package com.trifork.sdm.replication.gateway;

import javax.xml.bind.*;

import com.google.inject.Provides;
import com.trifork.sdm.replication.gateway.properties.Secret;
import com.trifork.sdm.replication.util.AuthorizationManager;
import com.trifork.sdm.replication.util.ConfiguredModule;

public class GatewayModule extends ConfiguredModule
{
	private JAXBContext context;


	@Override
	protected void configureServlets()
	{
		bind(RequestProcessor.class).to(SoapProcessor.class);

		// Set up the route.

		serve("/gateway").with(GatewayServlet.class);

		bind(AuthorizationManager.class).toInstance(new AuthorizationManager("21312kjhdskjb123123"));
		
		try
		{
			context = JAXBContext.newInstance(AuthorizationRequestStructure.class, AuthorizationResponseStructure.class);
		}
		catch (Exception e)
		{
			addError(e);
		}
	}


	@Provides
	@Secret
	protected String provideSecret()
	{
		return getConfig().getString("replication.secret");
	}


	@Provides
	protected Marshaller provideMarshaller() throws JAXBException
	{
		return context.createMarshaller();
	}


	@Provides
	protected Unmarshaller provideUnmarshaller() throws JAXBException
	{
		return context.createUnmarshaller();
	}
}
