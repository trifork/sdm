package com.trifork.sdm.replication.replication;


import java.io.OutputStream;
import java.util.Date;

import com.google.inject.ImplementedBy;
import com.trifork.stamdata.Record;


/**
 * Writes entity instances to an output stream.
 * 
 * Implementing classes should support a single output format e.g. XML or FastInfoset.
 */
@ImplementedBy(XMLEntityWriter.class)
public interface EntityWriter
{
	void write(OutputStream outputStream, Class<? extends Record> entityType, OutputFormat format, int pageSize, Date sinceDate, long sinceId) throws Exception;
}
