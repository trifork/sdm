package com.trifork.stamdata.ssl;

import com.google.inject.throwingproviders.CheckedProvider;

public interface UncheckedProvider<T> extends CheckedProvider<T> {
	@Override
	T get();
}
