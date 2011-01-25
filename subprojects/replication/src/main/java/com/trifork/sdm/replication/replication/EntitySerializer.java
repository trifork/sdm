package com.trifork.sdm.replication.replication;

import java.io.OutputStream;

import com.trifork.sdm.replication.db.Query;


/**
 * Writes entity instances to an output stream.
 * 
 * Implementing classes should support a single output format e.g. XML or
 * FastInfoset.
 */
public interface EntitySerializer {

	void output(Query query, OutputStream outputStream, OutputFormat format, int pageSize) throws Exception;
}
