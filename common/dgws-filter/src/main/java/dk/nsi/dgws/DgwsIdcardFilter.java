package dk.nsi.dgws;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.IDCard;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FaultCodeValues;
import dk.sosi.seal.modelbuilders.SignatureInvalidModelBuildException;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;
import dk.sosi.seal.xml.XmlUtilException;


/**
 * Extracts IdCard instances from the request and places them in the servlet
 * request.
 * 
 * This module does not support soap 1.2.
 * To add support for soap 1.2, seal would have to support soap 1.2, and some of the 
 * code below would have to be rewritten.
 */
public class DgwsIdcardFilter implements Filter
{
	public static final String IDCARD_REQUEST_ATTRIBUTE_KEY = "dk.nsi.dgws.sosi.idcard";
	public static final String USE_TEST_FEDERATION_INIT_PARAM_KEY = "dk.nsi.dgws.sosi.usetestfederation";
	public static final String USE_TEST_FEDERATION_PARAMETER = "useSOSITestFederation";
	
	private Boolean useTestFederation;
	private SOSIFactory factory;
	
	public DgwsIdcardFilter() { }

	@Inject
	DgwsIdcardFilter(@Named(USE_TEST_FEDERATION_PARAMETER) @Nullable String useTestFederation)
	{
		this.useTestFederation = Boolean.valueOf(useTestFederation);
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		boolean useTestFederation = shouldWeUseTestFederation(filterConfig);

		Properties properties = SignatureUtil.setupCryptoProviderForJVM();
		Federation federation = useTestFederation ? new SOSITestFederation(properties) : new SOSIFederation(properties);
		factory = new SOSIFactory(federation, new EmptyCredentialVault(), properties);
	}

	private boolean shouldWeUseTestFederation(FilterConfig filterConfig)
	{
		String initParameter = filterConfig.getInitParameter(USE_TEST_FEDERATION_INIT_PARAM_KEY);
		String sysProp = System.getProperty(USE_TEST_FEDERATION_INIT_PARAM_KEY);
		
		if (sysProp != null)
		{
			return Boolean.valueOf(sysProp);
		}
		else if (initParameter != null)
		{
			return Boolean.valueOf(initParameter);
		}
		else
		{
			return useTestFederation;
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
		HttpServletRequest httpRequest = new BufferedInputRequestWrapper((HttpServletRequest) request);

		// HACK: To allow the JAX-WS client to access the WSDL without
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

			final String xml = IOUtils.toString(httpRequest.getReader());
			
			// NB. SEAL throws an exception if the id card is not valid in time
			// or invalid in some other way.

			IDCard idCard = factory.deserializeRequest(xml).getIDCard();
			
			// We have to make sure ourselves that the ID Cards NIST level etc.
			// is as expected.
			
			request.setAttribute(IDCARD_REQUEST_ATTRIBUTE_KEY, idCard);

			// Since w
			
			chain.doFilter(httpRequest, response);
		}
		catch (SignatureInvalidModelBuildException e)
		{
			// The signature was invalid. This sender is to blame.
			
			// Unfortunately SEAL's API is not made with the user in mind and
			// we cannot access e.g. the flow ID without re-parsing the XML
			// again ourselves. Therefore we simply return "0".
			
			// FIXME: To enable protection against replay attacks,
			// the response embeds the ID of the corresponding request (see the inResponseToID).
			// We do not set this, because it is difficult to get because using SEAL's API is
			// up hill.
			
			Reply reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "0", "0", FaultCodeValues.INVALID_SIGNATURE, "The signature used to sign the message was incorrectly signed or no longer valid.");
			writeFaultToResponse(httpResponse, reply);
		}
		catch (XmlUtilException e)
		{
			// The message could not be read. The sender is to blame.
			
			Reply reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "0", "0", FaultCodeValues.PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
			writeFaultToResponse(httpResponse, reply);
		}
		catch (Exception e)
		{
			// This is bad and will likely be a bug.

			Reply reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "0", "0", FaultCodeValues.PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
			writeFaultToResponse(httpResponse, reply);
		}
	}

	private void writeFaultToResponse(HttpServletResponse httpResponse, Reply reply) throws IOException
	{
		httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

		httpResponse.setContentType("text/xml"); // TODO: Shouldn't this depend on it being SOAP 1.1 or 1.2?

		Document replyXml = reply.serialize2DOMDocument();
		String xml = XmlUtil.node2String(replyXml);
		httpResponse.getWriter().write(xml);
	}

	@Override
	public void destroy() { }
	
	/**
	 * This annotation allows dependency injectors know
	 * that the annotated element can be null.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Nullable { }
}
