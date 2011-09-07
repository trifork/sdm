package dk.nsi.stamdata.cpr;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.google.inject.Inject;

import dk.nsi.stamdata.cpr.annotations.Whitelist;
import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.AuthenticationLevel;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.modelbuilders.ModelBuildException;
import dk.sosi.seal.modelbuilders.SignatureInvalidModelBuildException;
import dk.sosi.seal.xml.XmlUtil;

public class DGWSSecurityHandler implements SOAPHandler<SOAPMessageContext>
{
	private static final boolean CONTINUE_PROCESSING = true;
	
	private final Set<String> whitelist;
	private final SOSIFactory factory;

	@Inject
	DGWSSecurityHandler(@Whitelist Set<String> whitelist, SOSIFactory factory)
	{
		this.factory = checkNotNull(factory);
		this.whitelist = checkNotNull(whitelist);
	}

	@Override
	public boolean handleMessage(SOAPMessageContext context)
	{
		// Inquire incoming or outgoing message.  
		
        if (context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY) == Boolean.TRUE) return CONTINUE_PROCESSING;
		
		Request request;
		String envelope;

		// Convert the SOAP Node to a String so SEAL can validate it.

		try
		{
			envelope = XmlUtil.getTextNodeValue(context.getMessage().getSOAPPart().getEnvelope());
		}
		catch (Exception e)
		{
			throw new RuntimeException("Der opstod en fejl under parsning af requestet.", e);
		}

		// Let SEAL validate the ID-Card's integrity.

		try
		{
			request = factory.deserializeRequest(envelope);
		}
		catch (SignatureInvalidModelBuildException e)
		{
			throw new RuntimeException("Beskedens ID-kort er ikke gyldigt.", e);
		}
		catch (ModelBuildException e)
		{
			throw new RuntimeException("Der opstod en fejl under læsning af beskedens ID-kort.", e);
		}

		// Check that the client has the correct NIST authorization level (3 = VOCES).

		if (request.getIDCard().getAuthenticationLevel() == AuthenticationLevel.VOCES_TRUSTED_SYSTEM)
		{
			throw new RuntimeException("Denne web-service kræver et NIST niveau 3 STS-signeret IDkort (dvs. autentifikation på basis af VOCES certifikater).");
		}

		// If the NIST Level is 3 we know that the ID Card is a
		// System Card and can safely cast it to a SystemIDCard.

		SystemIDCard idCard = (SystemIDCard) request.getIDCard();
		String cvr = idCard.getSystemInfo().getCareProvider().getID();

		// Check if the CVR is authorized to call this service.

		if (!whitelist.contains(cvr))
		{
			throw new RuntimeException("Dette CVR-nummer har ikke tilladelse til at bruge denne web-service.");
		}

		return CONTINUE_PROCESSING;
	}

	@Override
	public void close(MessageContext arg0)
	{

	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0)
	{
		return false;
	}

	@Override
	public Set<QName> getHeaders()
	{
		return null;
	}
}
