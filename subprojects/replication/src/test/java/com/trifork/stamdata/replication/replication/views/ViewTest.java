package com.trifork.stamdata.replication.replication.views;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.trifork.stamdata.replication.replication.RegistryModule;


public class ViewTest {
	
	private static Injector injector;

	@BeforeClass
	public static void init() {
		
		injector = Guice.createInjector(new RegistryModule());
	}
	
	@Test
	public void Should_be_able_to_marshal_all_views() {
		
		// TODO: Marshal all views to XML.		
	}
}
