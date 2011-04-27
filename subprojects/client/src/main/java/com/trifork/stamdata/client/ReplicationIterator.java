// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
// 
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
// 
// Contributor(s): Contributors are attributed in the source code
// where applicable.
// 
// The Original Code is "Stamdata".
// 
// The Initial Developer of the Original Code is Trifork Public A/S.
// 
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
// 
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

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
