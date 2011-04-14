// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.client;

import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;


class ReplicationIterator<T> implements Iterator<EntityRevision<T>> {

	private final static String ATOM_NS = "http://www.w3.org/2005/Atom";

	private XMLEventReader reader;
	private XMLEventReader filteredReader;
	private Unmarshaller unmarshaller;
	private final Class<T> entityType;
	private final ReplicationReader replicationReader;

	private QName viewQName;

	ReplicationIterator(Class<T> entityType, ReplicationReader replicationReader) throws JAXBException {

		this.entityType = entityType;
		this.replicationReader = replicationReader;

		// XML TO POJO UNMARSHALLING

		JAXBContext ctx = JAXBContext.newInstance(entityType);
		unmarshaller = ctx.createUnmarshaller();

		try {
			viewQName = ctx.createJAXBIntrospector().getElementName(entityType.getConstructor().newInstance());
		}
		catch (Exception e) {
			throw new RuntimeException("The view type could not be instantiated for replication.", e);
		}
	}

	protected boolean hasMoreInCurrentPage() throws XMLStreamException {
		if (filteredReader == null) {
			return false;
		}
		seek(ATOM_NS, "entry", false);
		return filteredReader.peek() != null;
	}

	@Override
	public boolean hasNext() {

		try {
			boolean moreInPage = hasMoreInCurrentPage();

			// IF THERE ARE NO MORE ENTRIES IN THE PAGE
			//
			// This is true if the page is completely traversed.
			// Both readers have to be released.

			if (!moreInPage) {

				if (filteredReader != null) {
					filteredReader.close();
					reader.close();
				}

				// CHECK IF WE NEED TO FETCH THE NEXT PAGE

				if (!replicationReader.isUpdateCompleted()) {
					fetchNextPage();
					moreInPage = hasMoreInCurrentPage();
				}
			}

			return moreInPage;
		}
		catch (Exception e) {
			throw new RecordStreamException(e);
		}
	}

	private void fetchNextPage() {

		replicationReader.fetchNextPage();

		try {
			// READ THE RESPONSE
			//
			// To optimize the parsing we are only interested in 'start
			// elements'.

			XMLInputFactory readerFactory = XMLInputFactory.newInstance();
			reader = readerFactory.createXMLEventReader(replicationReader.getInputStream(), "UTF-8");
			filteredReader = readerFactory.createFilteredReader(reader, new EventFilter() {

				@Override
				public boolean accept(XMLEvent event) {

					return event.isStartElement();
				}
			});
		}
		catch (XMLStreamException e) {
			throw new IllegalStateException("Could not fetch next page", e);
		}
	}

	@Override
	public EntityRevision<T> next() {

		if (!hasNext()) throw new NoSuchElementException();

		try {
			seek(ATOM_NS, "entry", true);
			seek(ATOM_NS, "id", true);
			String id = filteredReader.getElementText();

			seek(viewQName.getNamespaceURI(), viewQName.getLocalPart(), false);

			T entity = unmarshaller.unmarshal(reader, entityType).getValue();

			return new EntityRevision<T>(id, entity);
		}
		catch (Exception e) {
			throw new RecordStreamException(e);
		}
	}

	private void seek(String namespace, String name, boolean consumeEvent) throws XMLStreamException {

		while (filteredReader.hasNext()) {

			XMLEvent event = (consumeEvent) ? filteredReader.nextEvent() : filteredReader.peek();
			QName qName = event.asStartElement().getName();

			if (qName.getNamespaceURI().equals(namespace) && qName.getLocalPart().equals(name)) {
				break;
			}

			if (!consumeEvent) filteredReader.nextEvent();
		}
	}

	@Override
	public void remove() {

		throw new UnsupportedOperationException();
	}
}
