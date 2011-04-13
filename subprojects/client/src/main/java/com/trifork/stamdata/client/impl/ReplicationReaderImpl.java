package com.trifork.stamdata.client.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trifork.stamdata.client.ReplicationReader;

public class ReplicationReaderImpl implements ReplicationReader {
	private final URL entityURL;
	private final String authorization;
	private int count;
	private String nextOffset;
	private URLConnection connection;

	public ReplicationReaderImpl(String authorization, URL entityURL, String offset, int count) {
		this.authorization = authorization;
		this.entityURL = entityURL;
		this.count = count;
		this.nextOffset = offset;
	}

	@Override
	public void fetchNextPage() {
		try {
			if (connection != null) {
				connection.getInputStream().close();
			}
			connectTo(entityURL + "?offset=" + nextOffset + "&count=" + count);
			nextOffset = findNextOffset();
		} catch (IOException e) {
			throw new IllegalStateException("Could not fetch next page", e);
		}
	}

	private void connectTo(String pageUrl) throws IOException {
		connection = new URL(pageUrl).openConnection();
		connection.setRequestProperty("Accept", "application/atom+xml");
		connection.setRequestProperty("Authentication", "STAMDATA " + authorization);
		connection.connect();
	}

	@Override
	public InputStream getInputStream() {
		try {
			return connection.getInputStream();
		} catch (IOException e) {
			throw new IllegalStateException("Could not get input stream from connection", e);
		}
	}

	@Override
	public boolean isUpdateCompleted() {
		return nextOffset == null;
	}

	private String findNextOffset() {
		String link = connection.getHeaderField("Link");
		return link != null ? parseWebLink(link) : null;
	}

	private String parseWebLink(String link) {
		Matcher matcher = Pattern.compile(".*offset=([0-9]+)>.*").matcher(link);
		matcher.find();
		return matcher.group(1);
	}
}
