package com.trifork.sdm.replication.replication;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;

import com.google.inject.Provider;
import com.trifork.sdm.replication.gui.models.PermissionDao;
import com.trifork.sdm.replication.mocks.MockEntity;
import com.trifork.sdm.replication.replication.models.Record;
import com.trifork.sdm.replication.util.AuthorizationManager;
import com.trifork.sdm.replication.util.HistoryOffset;

public class ReplicationServletTest {

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test() throws Exception {

		// REQUEST

		HttpServletRequest request = mock(HttpServletRequest.class);

		long now = new Date().getTime() / 1000;

		when(request.getParameter("offset")).thenReturn("" + now + "0000000001");
		when(request.getParameter("count")).thenReturn("999");
		when(request.getHeader("Accept")).thenReturn("application/atom+xml");

		when(request.getPathInfo()).thenReturn("/foo/bar/v1");

		// RESPONSE

		HttpServletResponse response = mock(HttpServletResponse.class);

		AtomFeedWriter writer = mock(AtomFeedWriter.class);

		Provider writerProvider = mock(Provider.class);
		when(writerProvider.get()).thenReturn(writer);

		// SECURITY

		AuthorizationManager authorizationManager = mock(AuthorizationManager.class);
		PermissionDao permissionDao = mock(PermissionDao.class);

		when(permissionDao.canAccessEntity("", "foo/bar/v1")).thenReturn(true);

		// RECORDS

		Map<String, Class<? extends Record>> registry = new HashMap<String, Class<? extends Record>>();
		registry.put("foo/bar/v1", MockEntity.class);

		String entityOffset = "10000000001000000000";
		MockEntity entity = mock(MockEntity.class);
		when(entity.getOffset()).thenReturn(entityOffset);

		List<MockEntity> records = new ArrayList<MockEntity>();
		records.add(entity);

		RecordDao recordDao = mock(RecordDao.class);
		when(recordDao.find(eq(MockEntity.class), any(HistoryOffset.class), eq(999))).thenReturn(records);

		// UNIT UNDER TEST

		RegistryServlet servlet = new RegistryServlet(registry, authorizationManager, permissionDao, recordDao, writerProvider);

		servlet.doGet(request, response);

		verify(writer).write(eq("foo/bar/v1"), eq(records), any(XMLStreamWriter.class));
	}

	// TODO: Test access denied.

	// TODO: Test that the correct headers are set.

	// TODO: Test fast infoset.
}
