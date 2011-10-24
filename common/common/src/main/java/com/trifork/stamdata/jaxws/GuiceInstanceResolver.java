/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */
package com.trifork.stamdata.jaxws;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceFeature;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.spi.WebServiceFeatureAnnotation;

import com.google.inject.Injector;
import com.sun.istack.NotNull;
import com.sun.xml.ws.api.FeatureConstructor;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.server.InstanceResolverAnnotation;
import com.sun.xml.ws.api.server.WSEndpoint;
import com.sun.xml.ws.api.server.WSWebServiceContext;
import com.sun.xml.ws.server.AbstractMultiInstanceResolver;

/**
 * A JAX-WS instance resolver for Guice.
 * 
 * Looks at the end-point class and gets the annotation in order to know what
 * Guice module to use when injecting the dependencies into the end-point.
 */
public class GuiceInstanceResolver<T> extends AbstractMultiInstanceResolver<T>
{
	static final String INJECTOR_NAME = Injector.class.getName();
	private Injector injector;
	
	private WSWebServiceContext webServiceContext;
	
	public GuiceInstanceResolver(@NotNull final Class<T> clazz) throws IllegalAccessException, InstantiationException
	{
		super(clazz);
	}
	
    @Override
    public void start(final WSWebServiceContext webServiceContext, @SuppressWarnings("rawtypes") final WSEndpoint endpoint)
    {
        super.start(webServiceContext, endpoint);
        
        this.webServiceContext = webServiceContext;
	}

	@Override
	public T resolve(@NotNull final Packet packet)
	{
		// Initialize the injector if it has not been done yet.
		
		if (injector == null)
		{
			ServletContext context = (ServletContext) webServiceContext.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
			injector = (Injector) context.getAttribute(INJECTOR_NAME);
		}
		
		// This is the meat of the class, lets Guice create the dependencies,
		// while still supporting @Resource injection.
		
		final T instance = injector.getInstance(this.clazz);
		
		prepare(instance);
		
		return instance;
	}

	@Retention(RUNTIME)
	@Target(TYPE)
	@Documented
	@WebServiceFeatureAnnotation(id = GuiceJaxWsFeature.JAXWS_FEATURE_ID, bean = GuiceJaxWsFeature.class)
	@InstanceResolverAnnotation(GuiceInstanceResolver.class)
	public static @interface GuiceWebservice
	{
	}

	/**
	 * The feature, just holds a unique ID and sets the enabled flag.
	 */
	public static class GuiceJaxWsFeature extends WebServiceFeature
	{
		public static final String JAXWS_FEATURE_ID = "dk.nsi.guice.jaxws.feature";

		@FeatureConstructor
		public GuiceJaxWsFeature()
		{
			this.enabled = true;
		}

		public String getID()
		{
			return JAXWS_FEATURE_ID;
		}
	}
}