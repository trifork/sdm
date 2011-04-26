// Stamdata - Copyright (C) 2011 National Board of e-Health (NSI)
// 
// All source code and information supplied as part of Stamdata is
// copyright to National Board of e-Health.
// 
// The source code has been released under a dual license - meaning you can
// use either licensed version of the library with your code.
// 
// It is released under the Common Public License 1.0, a copy of which can
// be found at the link below.
// http://www.opensource.org/licenses/cpl1.0.php
// 
// It is released under the LGPL (GNU Lesser General Public License), either
// version 2.1 of the License, or (at your option) any later version. A copy
// of which can be found at the link below.
// http://www.gnu.org/copyleft/lesser.html

package com.trifork.stamdata.replication.replication;

import static java.lang.Integer.parseInt;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.ScrollableResults;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;
import com.trifork.stamdata.replication.mocks.MockEntity;
import com.trifork.stamdata.replication.replication.views.View;
import com.trifork.stamdata.replication.security.SecurityManager;
import com.trifork.stamdata.replication.usagelog.UsageLogger;

@RunWith(MockitoJUnitRunner.class)
public class RegistryServletTest {

	private RegistryServlet servlet;

	private HttpServletRequest request;
	private HttpServletResponse response;

	private @Mock SecurityManager securityManager;
	private Map<String, Class<? extends View>> registry;
	private Map<String, Class<? extends View>> mappedClasses;
	private @Mock RecordDao recordDao;
	private @Mock AtomFeedWriter writer;
	private @Mock UsageLogger usageLogger;
	private String requestPath;
	private String countParam;
	private String clientId = "CVR:12345678";
	private ScrollableResults records;
	private String acceptHeader;
	private boolean authorized;
	private String offsetParam;
	private String nextOffset;

	@Before
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUp() throws Exception {

		registry = new HashMap<String, Class<? extends View>>();

		Provider securityManagerProvider = mock(Provider.class);
		when(securityManagerProvider.get()).thenReturn(securityManager);

		Provider recordDaoProvider = mock(Provider.class);
		when(recordDaoProvider.get()).thenReturn(recordDao);

		Provider writerProvider = mock(Provider.class);
		when(writerProvider.get()).thenReturn(writer);
		
		Provider usageLoggerProvider = mock(Provider.class);
		when(usageLoggerProvider.get()).thenReturn(usageLogger);

		servlet = new RegistryServlet(registry, usageLoggerProvider, securityManagerProvider, recordDaoProvider, writerProvider);

		setUpValidRequest();
	}

	@Test
	public void Should_accept_valid_request() throws Exception {

		get();

		verify(response).setStatus(200);
		verify(writer).write(eq(MockEntity.class), eq(records), any(OutputStream.class), eq(false));
	}
	
	@Test
	public void Should_give_no_link_if_there_are_no_updates() throws Exception {

		when(records.last()).thenReturn(false);
		
		get();
		
		verify(response).setStatus(200);
		verify(response, never()).setHeader(Matchers.eq("Link"), Matchers.anyString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void Should_deny_access_if_the_client_is_not_authorized_for_the_requested_view() throws Exception {

		authorized = false;
		get();
		verify(response).setStatus(401);
		verify(writer, never()).write(any(Class.class), any(ScrollableResults.class), any(OutputStream.class), Mockito.anyBoolean());
	}

	@Test
	public void Should_return_a_web_link_for_the_next_page_if_there_are_more_records() throws Exception {
		
		countParam = "2";
		get();
		verify(response).addHeader("Link", String.format("<stamdata://foo/bar/v1?offset=%s>; rel=\"next\"", nextOffset));
	}
	
	@Test
	public void Should_perform_usage_logging() throws Exception {
		when(writer.write(Matchers.<Class<? extends View>>anyObject(), Matchers.<ScrollableResults>anyObject(), Matchers.<OutputStream>anyObject(), Matchers.anyBoolean()))
			.thenReturn(2);

		get();

		verify(usageLogger).log(clientId, "foo/bar/v1", 2);
	}

	@Test
	public void Should_not_return_a_web_link_if_there_are_no_more_records() throws Exception {

		countParam = "2";

		records = mock(ScrollableResults.class);

		get();

		verify(response, never()).setHeader(eq("Link"), anyString());
	}

	@Test
	public void Should_return_fast_infoset_if_the_user_requests_it() throws Exception {

		acceptHeader = "application/atom+fastinfoset";

		get();

		verify(response).setContentType("application/atom+fastinfoset; charset=utf-8");
		verify(writer).write(eq(MockEntity.class), eq(records), any(OutputStream.class), eq(true));
	}

	@Test
	public void Should_return_xml_if_the_user_requests_it() throws Exception {

		acceptHeader = "application/atom+xml";
		
		get();
		
		verify(response).setContentType("application/atom+xml; charset=utf-8");
		verify(writer).write(eq(MockEntity.class), eq(records), any(OutputStream.class), eq(false));
	}

	// TODO: Make test for unaccepted content type.

	@Test
	public void Should_return_records_from_the_correct_offset() throws Exception {

		get();
		
		verify(recordDao).findPage(MockEntity.class, "2222222222", new Date(1111111111000L), 2);
	}

	// HELPER METHODS

	public void get() throws Exception {

		when(securityManager.isAuthorized()).thenReturn(authorized);
		when(securityManager.getClientId()).thenReturn(clientId);
		when(recordDao.findPage(MockEntity.class, "2222222222", new Date(1111111111000L), parseInt(countParam))).thenReturn(records);
		when(request.getPathInfo()).thenReturn(requestPath);
		when(request.getHeader("Accept")).thenReturn(acceptHeader);
		when(request.getParameter("offset")).thenReturn(offsetParam);
		when(request.getParameter("count")).thenReturn(countParam);
		registry.putAll(mappedClasses);

		servlet.doGet(request, response);
	}

	public void setUpValidRequest() throws Exception {

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		requestPath = "/foo/bar/v1";
		
		authorized = true;
		
		mappedClasses = new HashMap<String, Class<? extends View>>();
		mappedClasses.put("foo/bar/v1", MockEntity.class);

		countParam = "2";
		offsetParam = "11111111112222222222";
		nextOffset = "11111111113333333333";

		records = mock(ScrollableResults.class);
		when(records.next()).thenReturn(true,false);
		
		MockEntity lastRecord = mock(MockEntity.class);
		when(records.get(0)).thenReturn(lastRecord);
		when(records.last()).thenReturn(true);
		when(lastRecord.getOffset()).thenReturn(nextOffset);
		
		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);

		acceptHeader = "application/atom+xml";
	}
}
