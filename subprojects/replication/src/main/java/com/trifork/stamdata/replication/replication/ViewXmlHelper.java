package com.trifork.stamdata.replication.replication;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.trifork.stamdata.replication.replication.views.View;

/**
 * We create separate JAXB contexts for each class, because we want to avoid unneccessary namespace declarations.
 * @author ahj
 *
 */
public class ViewXmlHelper {

	private Map<Class<?>, JAXBContext> jaxbContexts = new HashMap<Class<?>, JAXBContext>();

	public ViewXmlHelper(Collection<Class<?>> classes) throws JAXBException {
		this(classes.toArray(new Class<?>[0]));
	}
	
	public ViewXmlHelper(Class<?>... classes) throws JAXBException {
		for (Class<?> cls : classes) {
			jaxbContexts.put(cls, JAXBContext.newInstance(cls));
		}
	}
	
	public String getNamespace(View view) {
		return jaxbContexts.get(view.getClass()).createJAXBIntrospector().getElementName(view).getNamespaceURI();
	}

	public Marshaller createMarshaller(Class<?> classToBeMarshalled) {
		try {
			return jaxbContexts.get(classToBeMarshalled).createMarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
