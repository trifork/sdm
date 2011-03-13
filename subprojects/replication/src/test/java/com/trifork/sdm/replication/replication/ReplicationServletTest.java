package com.trifork.sdm.replication.replication;

import static com.trifork.sdm.replication.replication.URLParameters.*;
import static org.mockito.Mockito.*;

import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import com.google.inject.Provider;
import com.trifork.stamdata.Record;
import com.trifork.stamdata.registre.sor.Apotek;

public class ReplicationServletTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void test() throws Throwable {
		// Arrange
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		Provider provider = mock(Provider.class);
		EntityWriter writer = mock(EntityWriter.class);

		Calendar nowCal = Calendar.getInstance();
		nowCal.set(Calendar.MILLISECOND, 0);
		Date nowDate = nowCal.getTime();
		long now = nowDate.getTime() / 1000;
		when(request.getParameter(HISTORY_ID)).thenReturn("" + now + "0000000001");
		when(request.getParameter(FORMAT)).thenReturn("XML");
		when(request.getParameter(PAGE_SIZE)).thenReturn("100");
		when(request.getParameter(ENTITY_TYPE)).thenReturn("Apotek");

		when(provider.get()).thenReturn(writer);

		RegistryServlet replicationServlet = new RegistryServlet();
		Map<String, Class<? extends Record>> routes = new HashMap<String, Class<? extends Record>>();
		routes.put("Apotek", Apotek.class);
		replicationServlet.routes = routes;
		replicationServlet.writerProvider = provider;

		// Act
		replicationServlet.doGet(request, response);

		// Assert
		verify(writer).write((OutputStream) isNull(), eq(Apotek.class), eq(OutputFormat.XML), eq(100), eq(nowDate), eq(1L));
	}
}
