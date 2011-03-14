package com.trifork.sdm.replication.mocks;

import javax.persistence.Id;

import com.trifork.sdm.replication.replication.models.Record;

public abstract class MockEntity extends Record {

	@Id
	public String id() {
		return null;
	}
}
