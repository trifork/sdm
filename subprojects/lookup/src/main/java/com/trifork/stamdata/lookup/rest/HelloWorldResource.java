package com.trifork.stamdata.lookup.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.JAXBElement;

import oio.sagdok.person._1_0.PersonType;

@Path("hello")
public class HelloWorldResource {
	@GET
	@Produces("text/plain")
	public String hello() {
		return "Hello World!";
	}
	
	@GET
	@Path("person")
	@Produces("text/xml")
	public JAXBElement<PersonType> helloPerson() {
		return new oio.sagdok.person._1_0.ObjectFactory().createPerson(new PersonType());
	}
}
