package com.trifork.sdm.replication.client;

import java.io.InputStream;
import java.net.URL;

import com.trifork.stamdata.Record;


public interface RecordPersister
{

	URL persist(InputStream inputStream, Class<? extends Record> entitySet) throws Exception;
}
