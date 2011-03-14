package com.trifork.sdm.replication.dgws;

import static com.trifork.sdm.replication.gui.models.RequestAttributes.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.inject.Provider;
import com.trifork.sdm.replication.dgws.AuthorizationServlet;
import com.trifork.sdm.replication.dgws.RequestProcessor;

public class GatewayServletTest
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void test_can_invoke_processor() throws Throwable
	{
		// Arrange
		Provider processorProvider = mock(Provider.class);
		AuthorizationServlet servlet = new AuthorizationServlet(processorProvider);
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
		RequestProcessor processor = mock(RequestProcessor.class);

		when(processorProvider.get()).thenReturn(processor);
		when(processor.getResponse()).thenReturn("This Is The Response");
		when(processor.getContentType()).thenReturn("Content Type");
		when(processor.getResponseCode()).thenReturn(200);

		ServletInputStream inputStream = mockServletInputStream("Hello World");
		when(request.getInputStream()).thenReturn(inputStream);

		ServletOutputStream outputStream = mock(ServletOutputStream.class);
		when(response.getOutputStream()).thenReturn(outputStream);

		when(request.getAttribute(USER_CPR)).thenReturn("12345678");
		when(request.getMethod()).thenReturn("POST");

		// Act
		servlet.processRequest(request, response);

		// Assert
		verify(processor).process("Hello World", "12345678", "POST");
		verify(response).setContentType("Content Type");
		verify(response).setStatus(200);
		verify(processor).getResponse();
	}


	private ServletInputStream mockServletInputStream(String bytes) throws IOException
	{
		ServletInputStream inputStream = mock(ServletInputStream.class);
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes.getBytes());
		when(inputStream.read(any(byte[].class), anyInt(), anyInt())).thenAnswer(new Answer<Integer>()
		{
			@Override
			public Integer answer(InvocationOnMock invocation) throws Throwable
			{
				return byteArrayInputStream.read((byte[]) invocation.getArguments()[0], (Integer) invocation.getArguments()[1], (Integer) invocation.getArguments()[2]);
			}
		});
		return inputStream;
	}
}
