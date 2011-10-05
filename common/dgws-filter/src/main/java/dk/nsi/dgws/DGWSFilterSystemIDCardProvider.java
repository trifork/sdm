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
