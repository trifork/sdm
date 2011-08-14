package com.trifork.stamdata.persistence;

import static java.lang.String.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.sql.*;

import org.junit.*;

import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.persistence.WorkingPersister;


public class PersisterTest
{
	private Connection connection;
	private PersonEntity person1;
	private PersonEntity person2;
	private PetEntity pet1;
	private WorkingPersister<PersonEntity> persister1;
	private WorkingPersister<PersonEntity> persister2;
	private WorkingPersister<PersonEntity> deltaPersister1;
	private WorkingPersister<PersonEntity> deltaPersister2;
	private WorkingPersister<PetEntity> petPersister;

	int changesetCount = 0;

	@Before
	public void setUp() throws SQLException
	{
		connection = Helpers.getConnection();

		persister1 = createPersister(true);
		persister2 = createPersister(true);

		deltaPersister1 = createPersister(false);
		deltaPersister2 = createPersister(false);

		person1 = createPerson("Thomas Børlum", "Bubbiskoven 23A");
		person2 = createPerson("Anders And", "Paradisæblevej 111");

		pet1 = createPet("Snogles McFinigan the 3rd", "The Dog House");
		petPersister = new WorkingPersister<PetEntity>(0, true, connection, PetEntity.class);
	}

	@After
	public void tearDown() throws SQLException
	{
		connection.rollback();
		connection.close();
	}

	@Test(expected = Exception.class)
	public void should_throw_exception_if_duplicates_occure_in_the_same_changeset() throws Exception
	{
		persister1.persist(person1);
		persister1.persist(person1);
		persister1.finish();
	}

	@Test
	public void should_ignore_a_duplicate_if_it_is_in_another_changeset() throws Exception
	{
		persister1.persist(person1);
		persister1.finish();

		persister2.persist(person1);
		persister2.finish();

		assertPersonCountEquals(1);
	}

	@Test
	public void should_create_delete_event_if_the_an_entity_is_not_found_while_persisting_a_complete_registry() throws Exception
	{
		persister1.persist(person1);
		persister1.persist(person2);
		persister1.finish();

		persister2.persist(person2);
		persister2.finish();

		// At this point person 1 should have a DELETE
		// event in changeset 2. Both records should
		// still exist in the database.

		assertPersonCountEquals(2);
		assertEventSequence("CREATE", "CREATE", "DELETE");
	}

	@Test(expected = IllegalStateException.class)
	public void should_not_allow_persisting_any_more_records_after_finish_is_called() throws Exception
	{
		persister1.finish();
		persister1.persist(person1);
	}

	@Test
	public void should_not_create_delete_events_if_a_record_does_not_appear_in_a_delta_import() throws Exception
	{
		deltaPersister1.persist(person1);
		deltaPersister1.persist(person2);
		deltaPersister1.finish();
		deltaPersister2.persist(person1);
		deltaPersister2.finish();

		// At this point both person 1 and 2
		// should be in the db and no DELETE
		// event have been created.

		assertPersonCountEquals(2);
		assertPersonEventSequence("CREATE", "CREATE");
	}

	@Test
	public void should_not_have_different_types_interfearing_with_each_other_even_if_they_have_the_same_column_hash() throws Exception
	{
		persister1.persist(person1);
		persister1.finish();
		
		petPersister.persist(pet1);
		
		PetEntity petLooksLikePerson = new PetEntity(person1.getName(), person1.getAddress());
		petPersister.persist(petLooksLikePerson);
		
		assertPersonCountEquals(1);
		assertPersonEventSequence("CREATE");
		
		assertPetCountEquals(2);
		assertPetEventSequence("CREATE", "CREATE");
	}

	@Test
	public void should_create_update_events_when_entities_exist_in_the_db_but_have_changed_values() throws Exception
	{
		persister1.persist(person1);
		persister1.finish();

		person1.setAddress("Humlebakken 46, Ølgod");
		persister2.persist(person1);
		persister2.finish();

		// The original plus the updated version.

		assertPersonCountEquals(2);
		assertPersonEventSequence("CREATE", "UPDATE");
	}

	// HELPERS

	private WorkingPersister<PersonEntity> createPersister(boolean forCompleteRegistry) throws SQLException
	{
		return new WorkingPersister<PersonEntity>(changesetCount++, forCompleteRegistry, connection, PersonEntity.class);
	}

	public PersonEntity createPerson(String name, String address)
	{
		return new PersonEntity(name, address);
	}

	private PetEntity createPet(String name, String address)
	{
		return new PetEntity(name, address);
	}

	public void assertPersonCountEquals(int expected) throws SQLException
	{
		ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) AS count FROM PersonEntity");
		rs.next();
		assertEquals(expected, rs.getInt("count"));
	}
	
	public void assertPetCountEquals(int expected) throws SQLException
	{
		ResultSet rs = connection.createStatement().executeQuery("SELECT COUNT(*) AS count FROM PetEntity");
		rs.next();
		assertEquals(expected, rs.getInt("count"));
	}
	
	public void assertPersonEventSequence(String ...expectedSequence) throws SQLException
	{
		assertEventSequence("PersonEntity", expectedSequence);
	}
	
	public void assertPetEventSequence(String ...expectedSequence) throws SQLException
	{
		assertEventSequence("PetEntity", expectedSequence);
	}

	private void assertEventSequence(String tableName, String... expectedSequence) throws SQLException
	{
		Statement statement = connection.createStatement();
		ResultSet rs = statement.executeQuery(format("SELECT * FROM VersionEvent WHERE tableName = '%s' ORDER BY EventID", tableName));

		int i = 0;

		while (rs.next())
		{
			String expectedEventType = expectedSequence[i++];

			// TODO: It would be nice to also check the events are for the
			// correct entities.

			assertThat(rs.getString("EventType"), is(expectedEventType));
		}

		statement.close();
	}
}
