package com.trifork.stamdata.lookup.rest;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.io.InputStream;
import java.util.Scanner;

@Path("personclient")
public class PersonClientResource {
    @Context
    private ServletContext servletContext;

    @GET
    @Produces("text/html")
    public String renderPerson() {
        InputStream is = servletContext.getResourceAsStream("/client/jquery.html");
        return new Scanner(is).useDelimiter("\\A").next();

    }


}
