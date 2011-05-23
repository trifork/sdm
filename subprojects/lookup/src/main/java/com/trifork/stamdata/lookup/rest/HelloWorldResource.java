package com.trifork.stamdata.lookup.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("hello")
public class HelloWorldResource {
	@GET
	@Produces("text/plain")
	public String hello() {
		return "Hello World!";
	}
}
