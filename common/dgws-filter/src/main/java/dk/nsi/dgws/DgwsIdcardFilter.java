package dk.nsi.dgws;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FaultCodeValues;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;

/**
 * Extracts IdCard instances from the request and places them in the servlet
 * request
 */
public class DgwsIdcardFilter implements Filter
{
	public static final String IDCARD_REQUEST_ATTRIBUTE_KEY = "dk.nsi.dgws.sosi.idcard";
	public static final String USE_TEST_FEDERATION_INIT_PARAM_KEY = "dk.nsi.dgws.sosi.usetestfederation";
	private SOSIFactory factory;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		boolean useTestFederation = shouldWeUseTestFederation(filterConfig);

		Properties properties = SignatureUtil.setupCryptoProviderForJVM();
		Federation federation;

		if (useTestFederation)
		{
			federation = new SOSITestFederation(properties);
		}
		else
		{
			federation = new SOSIFederation(properties);
		}

		factory = new SOSIFactory(federation, new EmptyCredentialVault(), properties);
	}

	private Boolean shouldWeUseTestFederation(FilterConfig filterConfig)
	{
		String initParameter = filterConfig.getInitParameter(USE_TEST_FEDERATION_INIT_PARAM_KEY);
		String sysProp = System.getProperty(USE_TEST_FEDERATION_INIT_PARAM_KEY);
		
		if (sysProp != null)
		{
			return Boolean.valueOf(sysProp);
		}
		else
		{
			return Boolean.valueOf(initParameter);
		}
	}

	@Override
	public void doFilter(ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException
	{
		// We only accept HTTP requests.

		if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse))
		{
			return;
		}

		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		// To allow the JAX-WS client to access the WSDL without
		// having to send DGWS stuff along with the call we
		// make a little hack that pass these called through.

		if ("wsdl".equals(httpRequest.getQueryString()))
		{
			chain.doFilter(request, response);
			return;
		}

		try
		{
			// Rip out the ID Card and cram it into the request context.

			Reader input = request.getReader();
			final String xml = IOUtils.toString(input);

			Request sealRequest = factory.deserializeRequest(xml);
			request.setAttribute(IDCARD_REQUEST_ATTRIBUTE_KEY, sealRequest.getIDCard());

			chain.doFilter(new RequestWrapperWithSavedBody(httpRequest, xml), response);
		}
		catch (Exception e)
		{
			// TODO: There should be two catch clauses here.
			// one for when the client has messed up and made
			// some sort of bad request (e.g. bad XML).
			// And one here the server crashes for some reason. (This is bad,
			// and will likely be a bug.)

			e.printStackTrace(); // FIXME remove

			Reply reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "0", "0", FaultCodeValues.PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
			httpResponse.setStatus(500);

			httpResponse.setContentType("text/xml");

			Document replyXml = reply.serialize2DOMDocument();
			String xml = XmlUtil.node2String(replyXml);
			httpResponse.getWriter().write(xml);
		}
	}

	@Override
	public void destroy()
	{
	}
}

class RequestWrapperWithSavedBody extends HttpServletRequestWrapper
{
	private final String body;

	public RequestWrapperWithSavedBody(HttpServletRequest request, String requestBody) throws IOException
	{
		super(request);
		body = requestBody;
	}

	@Override
	public ServletInputStream getInputStream() throws IOException
	{
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());
		ServletInputStream servletInputStream = new ServletInputStream()
		{
			public int read() throws IOException
			{
				return byteArrayInputStream.read();
			}
		};
		return servletInputStream;
	}

	@Override
	public BufferedReader getReader() throws IOException
	{
		return new BufferedReader(new InputStreamReader(this.getInputStream()));
	}
}
