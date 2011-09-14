package dk.nsi.dgws;

import com.sun.net.httpserver.HttpServer;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Reply;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SignatureUtil;
import dk.sosi.seal.model.constants.DGWSConstants;
import dk.sosi.seal.model.constants.FaultCodeValues;
import dk.sosi.seal.pki.*;
import dk.sosi.seal.vault.EmptyCredentialVault;
import dk.sosi.seal.xml.XmlUtil;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;
import java.util.Scanner;

/**
 * Extracts IdCard instances from the request and places them in the servlet request
 */
    public static final String IDCARD_REQUEST_ATTRIBUTE_KEY = "dk.nsi.dgws.sosi.idcard";
    public static final String USE_TESTFEDERATION_INIT_PARAM_KEY = "dk.nsi.dgws.sosi.usetestfederation";
    private SOSIFactory factory;

    @Override
        boolean useTestFederation = Boolean.valueOf(filterConfig.getInitParameter(USE_TESTFEDERATION_INIT_PARAM_KEY));

        Properties properties = SignatureUtil.setupCryptoProviderForJVM();
        IntermediateCertificateCache imCertCache = new InMemoryIntermediateCertificateCache();

        Federation federation;
            federation = new SOSITestFederation(properties, imCertCache);
            federation = new SOSIFederation(properties, imCertCache);
        }

        factory = new SOSIFactory(federation, new EmptyCredentialVault(), properties);
    }

    @Override
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse)response;
        HttpServletRequest httpRequest = (HttpServletRequest)request;

            chain.doFilter(request, response);
            return;
        }

        Reply reply;
            Reader input = request.getReader();
            String xml = IOUtils.toString(input);

            Request sealRequest = factory.deserializeRequest(xml);
            request.setAttribute(IDCARD_REQUEST_ATTRIBUTE_KEY, sealRequest.getIDCard());

            chain.doFilter(request, response);
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
    }
public class DgwsIdcardFilter implements Filter
{
	public void init(FilterConfig filterConfig) throws ServletException
	{
		if (useTestFederation)
		{
		else
		{
		if (!(request instanceof HttpServletRequest && response instanceof HttpServletResponse))
		{
		// To allow the JAX-WS client to access the WSDL without
		// having to send DGWS stuff along with the call we
		// make a little hack that pass these called through.
		
		if ("wsdl".equals(httpRequest.getQueryString()))
		{
		try
		{
			// Rip out the ID Card and cram it into the request context.
			
		}
		catch (Exception e)
		{
			// TODO: There should be two catch clauses here.
			// one for when the client has messed up and made
			// some sort of bad request (e.g. bad XML).
			// And one here the server crashes for some reason. (This is bad, and will likely be a bug.)
			
	public void destroy()
	{
}
