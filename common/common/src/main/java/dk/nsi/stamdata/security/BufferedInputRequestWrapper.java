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
package dk.nsi.stamdata.security;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;

/**
 * Since a request's input buffer can only be read once,
 * we wrap requests and allow for multiple reads of the input.
 */
class BufferedInputRequestWrapper extends HttpServletRequestWrapper
{
	private final byte[] bytes;
	
	BufferedInputRequestWrapper(HttpServletRequest request) throws IOException
	{
		super(request);
		
		bytes = IOUtils.toByteArray(request.getInputStream());
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		
		return new ServletInputStream()
		{
			public int read() throws IOException
			{
				return byteArrayInputStream.read();
			}
		};
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}
