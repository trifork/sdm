package dk.nsi.stamdata.cpr.jaxws;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.InstanceResolverAnnotation;
import com.sun.xml.ws.api.server.ResourceInjector;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;

import dk.nsi.stamdata.cpr.ApplicationController;

/**
 * The instance resolver
 * 
 * Looks at the end-point class and gets the annotation in order to know what
 * Guice module to use when injecting the dependencies into the end-point.
 */
public class GuiceInstanceResolver<T> extends AbstractMultiInstanceResolver<T>
{
	private ResourceInjector resourceInjector;

	private WSWebServiceContext webServiceContext;

	public GuiceInstanceResolver(@NotNull final Class<T> clazz) throws IllegalAccessException, InstantiationException
	{
		super(clazz);
	}

	@Override
	public void start(final WSWebServiceContext wsc, @SuppressWarnings("rawtypes") final WSEndpoint endpoint)
	{
		super.start(wsc, endpoint);

		this.resourceInjector = GuiceInstanceResolver.getResourceInjector(endpoint);
		this.webServiceContext = wsc;
	}

	@Override
	public T resolve(@NotNull final Packet packet)
	{
		final T instance = ApplicationController.injector.getInstance(this.clazz);
		resourceInjector.inject(webServiceContext, instance);

		return instance;
	}

	@Retention(RUNTIME)
	@Target(TYPE)
	@Documented
	@WebServiceFeatureAnnotation(id = GuiceJaxWsFeature.ID, bean = GuiceJaxWsFeature.class)
	@InstanceResolverAnnotation(GuiceInstanceResolver.class)
	public static @interface Guicy
	{
	}

	/**
	 * The feature, just holds a unique ID and sets the enabled flag.
	 */
	public static class GuiceJaxWsFeature extends WebServiceFeature
	{
		public static final String ID = "dk.nsi.guice.jaxws.feature";

		@FeatureConstructor
		public GuiceJaxWsFeature()
		{
			this.enabled = true;
		}

		public String getID()
		{
			return ID;
		}
	}
}