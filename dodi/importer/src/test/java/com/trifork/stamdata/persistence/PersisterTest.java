package com.trifork.stamdata.persistence;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.WorkingPersister;


public class PersisterTest
{
	private Connection connection;
	private PersonEntity person1;
	private WorkingPersister<PersonEntity> persister;
	private WorkingPersister<PersonEntity> persister2;
	private PersonEntity person2;

	@Before
	public void setUp() throws SQLException
	{
		connection = Helpers.getConnection();
		
		persister = new WorkingPersister<PersonEntity>(1, true, connection, PersonEntity.class);
		persister2 = new WorkingPersister<PersonEntity>(2, true, connection, PersonEntity.class);
		
		person1 = createEntity("Thomas Børlum", "Bubbiskoven 23A");
		person2 = createEntity("Anders And", "Paradisæblevej 111");
	}

	@Test(expected = Exception.class)
	public void should_throw_exception_if_duplicates_occure_in_the_same_changeset() throws Exception
	{
		persister.persist(person1);
		persister.persist(person1);
		persister.finish();
	}
	
	@Test
	public void should_ignore_a_duplicate_if_it_is_in_another_changeset() throws Exception
	{	
		persister.persist(person1);
		persister.finish();
		
		persister2.persist(person1);
		persister2.finish();
		
		assertPersonCountEquals(1);
	}
	
	@Test
	public void should_create_delete_event_if_the_an_entity_is_not_found_while_persisting_a_complete_registry() throws NoSuchAlgorithmException, IllegalArgumentException, SQLException, IllegalAccessException, InvocationTargetException
	{
		persister.persist(person1);
		persister.persist(person2);
		persister.finish();
		
		persister2.persist(person2);
		persister2.finish();
		
		// At this point person 1 should have a DELETE
		// event in changeset 2. Both records should
		// still exist in the database.
		
		assertPersonCountEquals(2);
		assertEventSequence("CREATE", "CREATE", "DELETE");
	}

	// HELPERS

	public PersonEntity createEntity(String name, String address)
	{
		return new PersonEntity(name, address);
	}
	
	public void assertPersonCountEquals(int expected) throws SQLException
	{
		ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) AS count FROM PersonEntity");
		rs.next();
		assertEquals(expected, rs.getInt("count"));
	}
	
	private void assertEventSequence(String ... expectedSequence) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery("SELECT * FROM VersionEvent ORDER BY EventID");
		
		for (String expectedEventType : expectedSequence)
		{
			rs.next();
			
			// TODO: It would be nice to also check the events are for the correct entities.
			
			assertThat(rs.getString("EventType"), is(expectedEventType));
		}
		
		statement.close();
	}
}
