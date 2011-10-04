package dk.nsi.dgws;

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
