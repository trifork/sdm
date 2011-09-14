package dk.nsi.dgws;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

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
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FaultCodeValues;
import dk.sosi.seal.pki.Federation;
import dk.sosi.seal.pki.InMemoryIntermediateCertificateCache;
import dk.sosi.seal.pki.IntermediateCertificateCache;
import dk.sosi.seal.pki.SOSIFederation;
import dk.sosi.seal.pki.SOSITestFederation;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;

/**
 * Extracts IdCard instances from the request and places them in the servlet request
 */
public class DgwsIdcardFilter implements Filter
{
	public static final String IDCARD_REQUEST_ATTRIBUTE_KEY = "dk.nsi.dgws.sosi.idcard";
	public static final String USE_TESTFEDERATION_INIT_PARAM_KEY = "dk.nsi.dgws.sosi.usetestfederation";
	private SOSIFactory factory;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		boolean useTestFederation = Boolean.valueOf(filterConfig.getInitParameter(USE_TESTFEDERATION_INIT_PARAM_KEY));

		Properties properties = SignatureUtil.setupCryptoProviderForJVM();
		IntermediateCertificateCache imCertCache = new InMemoryIntermediateCertificateCache();

		Federation federation;
		
		if (useTestFederation)
		{
			federation = new SOSITestFederation(properties, imCertCache);
		}
		else
		{
			federation = new SOSIFederation(properties, imCertCache);
		}

		factory = new SOSIFactory(federation, new EmptyCredentialVault(), properties);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
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

		Reply reply;
		try
		{
			// Rip out the ID Card and cram it into the request context.
			
			Reader input = request.getReader();
			String xml = IOUtils.toString(input);

			Request sealRequest = factory.deserializeRequest(xml);
			request.setAttribute(IDCARD_REQUEST_ATTRIBUTE_KEY, sealRequest.getIDCard());

			chain.doFilter(request, response);
		}
		catch (Exception e)
		{
			// TODO: There should be two catch clauses here.
			// one for when the client has messed up and made
			// some sort of bad request (e.g. bad XML).
			// And one here the server crashes for some reason. (This is bad, and will likely be a bug.)
			
			e.printStackTrace(); // FIXME remove

			reply = factory.createNewErrorReply(DGWSConstants.VERSION_1_0_1, "0", "0", FaultCodeValues.PROCESSING_PROBLEM, "An unexpected error occured while proccessing the request.");
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
