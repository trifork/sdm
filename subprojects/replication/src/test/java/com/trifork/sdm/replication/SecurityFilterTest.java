package com.trifork.sdm.replication;


import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_GONE;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.trifork.sdm.replication.replication.ReplicationFilter;
import com.trifork.sdm.replication.settings.Secret;


public class SecurityFilterTest
{
	private String resource = "resource";
	private String secret = "foo";
	private String username = "gateway";
	private URL resourceURL;


	@Test
	public void should_allowed_correct_requests()
	{

	}


	@Test
	public void should_have_a_valid_expires_date()
	{

	}


	@Test
	public void should_rejects_stale_requests()
	{

	}


	@Test
	public void request_with_a_signature_that_does_not_match_should_be_rejected() throws IOException
	{

	}
}
