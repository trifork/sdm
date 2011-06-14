package com.trifork.stamdata.lookup.rest;

import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
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
    public String renderPerson(@Context HttpServletRequest request) {
        InputStream is = servletContext.getResourceAsStream("/client/jquery.html");
        String javascriptClient = new Scanner(is).useDelimiter("\\A").next();
        return StringUtils.replace(javascriptClient, "${context.path}", request.getContextPath());
    }
}
