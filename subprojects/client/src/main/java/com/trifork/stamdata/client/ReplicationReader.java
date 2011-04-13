package com.trifork.stamdata.client;

import java.io.InputStream;

public interface ReplicationReader {
	void fetchNextPage();
	boolean isUpdateCompleted();
	InputStream getInputStream();
}
