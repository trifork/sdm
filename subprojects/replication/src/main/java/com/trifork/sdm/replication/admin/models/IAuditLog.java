package com.trifork.sdm.replication.admin.models;

import static com.trifork.sdm.replication.db.properties.Database.ADMINISTRATION;

import java.sql.SQLException;
import java.util.List;

import com.google.inject.ImplementedBy;
import com.trifork.sdm.replication.db.properties.Transactional;

@ImplementedBy(AuditLog.class)
public interface IAuditLog {

	@Transactional(ADMINISTRATION)
	public abstract List<LogEntry> all() throws SQLException;

	public abstract boolean create(String message, Object... args)
			throws SQLException;

	@Transactional(ADMINISTRATION)
	public abstract boolean create(String message) throws SQLException;

}