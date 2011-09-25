package dk.nsi.dgws;

import javax.servlet.ServletRequest;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;

import dk.sosi.seal.model.SystemIDCard;

public class DgwsModule extends ServletModule
{
	@Override
	protected void configureServlets()
	{
		bind(DgwsIdcardFilter.class).in(Scopes.SINGLETON);
		filter("/*").through(DgwsIdcardFilter.class);
	}
	
	@Provides
	@RequestScoped
	protected SystemIDCard provideIDCard(ServletRequest request)
	{
		return (SystemIDCard)request.getAttribute(DgwsIdcardFilter.IDCARD_REQUEST_ATTRIBUTE_KEY);
	}
}
