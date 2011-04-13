package com.trifork.stamdata.client;

import java.io.FileInputStream;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.trifork.stamdata.replication.replication.views.cpr.Person;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
@Ignore("Some weird gradle problem")
public class ReplicationIteratorTest {
	@Mock ReplicationReader reader;
	ReplicationIterator<Person> iterator;

	@Before
	public void before() throws Exception {
		iterator = new ReplicationIterator<Person>(Person.class, reader);
	}

	@Test
	public void knowsWhenThereIsMoreInResult() throws Exception {
		when(reader.getInputStream()).thenReturn(new FileInputStream("/Users/olefriisstergaard/projects/kombit/code/sdm/subprojects/client/src/test/resources/personResponse.xml"));

		assertTrue(iterator.hasNext());

		verify(reader).fetchNextPage();
	}

	@Test
	public void canGetFirstPersonInResult() throws Exception {
		when(reader.getInputStream()).thenReturn(new FileInputStream("/Users/olefriisstergaard/projects/kombit/code/sdm/subprojects/client/src/test/resources/personResponse.xml"));

		EntityRevision<Person> revision = iterator.next();
		assertEquals("tag:trifork.com,2011:cpr/person/v1/13026170860000000001", revision.getId());
		Person person = revision.getEntity();
		assertEquals("0702614155", person.getId());

		verify(reader).fetchNextPage();
	}

	@Test
	public void findsOutWhenEndOfChunkReached() throws Exception {
		when(reader.getInputStream()).thenReturn(new FileInputStream("/Users/olefriisstergaard/projects/kombit/code/sdm/subprojects/client/src/test/resources/personResponse.xml"));

		// Person with CPR 0901414084 is last in output
		while (!iterator.next().getEntity().getId().equals("0901414084")) {
			// Do nothing
		}

		when(reader.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("personResponseWith2People.xml"));

		Person person = iterator.next().getEntity();
		assertEquals("0802614155", person.getId());
		
		verify(reader, times(2)).fetchNextPage();
		verify(reader, times(2)).getInputStream();
	}

	@Test
	public void knowsWhenAtEndOfInput() throws Exception {
		when(reader.getInputStream()).thenReturn(getClass().getClassLoader().getResourceAsStream("personResponseWith2People.xml"));
		when(reader.isUpdateCompleted()).thenReturn(false);

		iterator.next();
		iterator.next();
		when(reader.isUpdateCompleted()).thenReturn(true);
		assertFalse(iterator.hasNext());
	}
}
