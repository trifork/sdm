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
    private static final String FILE_START_TOKEN = "\\A";
    @Context
    private ServletContext servletContext;

    @GET
    @Produces("text/html")
    public String renderPerson(@Context HttpServletRequest request) {
        InputStream is = servletContext.getResourceAsStream("/client/client.html");
        String javascriptClient = convertInputStreamToString(is);

        return substitutePlaceholders(request, javascriptClient);
    }

    private String substitutePlaceholders(HttpServletRequest request, String javascriptClient) {
        return StringUtils.replace(javascriptClient, "${context.path}", request.getContextPath());
    }

    private String convertInputStreamToString(InputStream is) {
        return new Scanner(is).useDelimiter(FILE_START_TOKEN).next();
    }
}
