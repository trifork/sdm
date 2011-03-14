package com.trifork.stamdata.replication.mocks;

import javax.persistence.Id;

import com.trifork.stamdata.replication.replication.views.View;

public abstract class MockEntity extends View {

	@Id
	public String id() {
		return null;
	}
}
