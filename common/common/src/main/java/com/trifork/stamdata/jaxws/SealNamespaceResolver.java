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

import com.google.common.collect.Lists;

import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.List;

/**
 * SEAL is really picky about it's namespaces.
 * Therefore we have to make sure that JAXWS serializes to
 * the same namespace prefixes as SEAL expects.
 * 
 * All JAX-WS clients must use this namespace resolver.
 */
public class SealNamespaceResolver implements HandlerResolver
{
	@Override
	@SuppressWarnings("rawtypes")
	public List<Handler> getHandlerChain(PortInfo portInfo)
	{
		return Lists.newArrayList((Handler) new SealNamespacePrefixSoapHandler());
	}
}
