package dk.nsi.stamdata.cpr;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import dk.sosi.seal.SOSIFactory;
import dk.sosi.seal.model.Request;
import dk.sosi.seal.model.SystemIDCard;
import dk.sosi.seal.modelbuilders.SignatureInvalidModelBuildException;
import dk.sosi.seal.xml.XmlUtil;

public class DenGodeWebserviceSecurityHandler implements SOAPHandler<SOAPMessageContext>
{
	@Override
	public boolean handleMessage(SOAPMessageContext context)
	{
		try
		{
			SOAPEnvelope envelope = context.getMessage().getSOAPPart().getEnvelope();
			String envelopeString = XmlUtil.getTextNodeValue(envelope);
			
			SOSIFactory factory = null;
			Request request = factory.deserializeRequest(envelopeString);
			
			if (request.getIDCard() instanceof SystemIDCard)
			{
				SystemIDCard idCard = (SystemIDCard) request.getIDCard();
				String CVR = idCard.getSystemInfo().getCareProvider().getID();
			
				// Check if the CVR is authorized to call this service.
				
				
			}
			else
			{
				// The ID-Card is a User ID-Card. This is not allowed.
				
				// Return fault.
			}
		}
		catch (SignatureInvalidModelBuildException e)
		{
			
		}
		catch (SOAPException e)
		{
			
		}
		
		
		return false;
	}
	
	@Override
	public void close(MessageContext arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean handleFault(SOAPMessageContext arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<QName> getHeaders()
	{
		return null;
	}
}
