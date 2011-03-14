package com.trifork.stamdata.client;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.*;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;


class ReplicationIterator<T> implements Iterator<EntityRevision<T>> {

	private URL pageURL;

	private XMLEventReader reader;
	private XMLEventReader filteredReader;

	private Unmarshaller unmarshaller;

	private final Class<T> entityType;

	private int count;
	private String nextOffset;

	private final URL entityURL;

	private final String authorization;

	ReplicationIterator(Class<T> entityType, String authorization, URL entityURL, String offset, int count) throws XMLStreamException, IOException, JAXBException {

		this.entityType = entityType;
		this.authorization = authorization;
		this.entityURL = entityURL;
		this.nextOffset = offset;
		this.count = count;

		// XML TO POJO UNMARSHALLING

		JAXBContext ctx = JAXBContext.newInstance(entityType);
		unmarshaller = ctx.createUnmarshaller();
	}

	protected boolean hasMoreInCurrentPage() throws XMLStreamException {

		return filteredReader != null && filteredReader.peek() != null;
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

				if (!isUpdateCompleted()) {
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

	protected boolean isUpdateCompleted() {

		return nextOffset == null;
	}

	@Override
	public EntityRevision<T> next() {

		if (!hasNext()) throw new NoSuchElementException();

		try {
			filteredReader.next();
			String id = reader.getElementText();
			filteredReader.peek();
			T entity = unmarshaller.unmarshal(reader, entityType).getValue();

			return new EntityRevision<T>(id, entity);
		}
		catch (JAXBException e) {
			throw new RecordStreamException(e);
		}
		catch (XMLStreamException e) {
			throw new RecordStreamException(e);
		}
	}

	@Override
	public void remove() {

		throw new UnsupportedOperationException();
	}

	protected void fetchNextPage() throws Exception {

		// CONNECT TO THE SERVICE
		//
		// The service requires three parameters. The accepted content type,
		// the entity type (specified by the request path), and the
		// authorization
		// token from the authorization service.

		this.pageURL = new URL(entityURL + "?offset=" + nextOffset + "&count=" + count);

		URLConnection connection = pageURL.openConnection();
		connection.setRequestProperty("Accept", "application/atom+xml");
		connection.setRequestProperty("Authentication", "STAMDATA " + authorization);

		connection.connect();

		// DETERMINE IF THERE ARE ANY MORE PAGES

		String link = connection.getHeaderField("Link");

		nextOffset = (link != null) ? parseWebLink(link) : null;

		// READ THE RESPONSE
		//
		// To optimize the parsing we are only interested in 'start element'
		// events that start with the prefix 'sd'.

		XMLInputFactory readerFactory = XMLInputFactory.newInstance();
		reader = readerFactory.createXMLEventReader(connection.getInputStream(), "UTF-8");

		EventFilter filter = new EventFilter() {

			@Override
			public boolean accept(XMLEvent event) {

				return event.isStartElement() && event.asStartElement().getName().getPrefix().equals("sd");
			}
		};

		filteredReader = readerFactory.createFilteredReader(reader, filter);
	}

	protected String parseWebLink(String link) {

		Matcher matcher = Pattern.compile(".*offset=([0-9]+)>.*").matcher(link);
		matcher.find();
		return matcher.group(1);
	}
}
