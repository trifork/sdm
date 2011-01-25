package com.trifork.sdm.importer.persistence.mysql;


import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.util.*;

import org.junit.Before;
import org.junit.Test;

import com.trifork.stamdata.DateUtils;
import com.trifork.stamdata.Record;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.persistence.MySQLTemporalTable.RecordVersion;
import com.trifork.stamdata.registre.takst.*;


public class MySQLStamDAOTest
{

	private Takst takst;
	private Laegemiddel laegemiddel;
	private MySQLTemporalDao dao;

	private MySQLTemporalTable<Record> laegemiddeltableMock;


	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception
	{
		/*
		takst = new Takst(DateUtils.toDate(2009, 7, 1), DateUtils.toDate(2009, 7, 14));

		// Add a dataset to the takst with one member

		List<Laegemiddel> list = new ArrayList<Laegemiddel>();

		laegemiddel = new Laegemiddel();
		laegemiddel.setDrugid(1l);
		laegemiddel.setNavn("Zymedolinatexafylitungebraekker");

		list.add(laegemiddel);

		TakstDataset<Laegemiddel> dataset = new TakstDataset<Laegemiddel>(takst, list, Laegemiddel.class);

		takst.addDataset(dataset);

		/*
		 * Add an empty dataset to the takst (should be ignored) List<Pakning> tomListe = new
		 * ArrayList<Pakning>(); Dataset<Pakning> tomtDataset = new Dataset<Pakning>(takst,
		 * tomListe, Pakning.class); takst.addDataset(tomtDataset);
		 */

		// Add a dataset to the takst, which should be ignored because it is not
		/*
		List<DivEnheder> enheder = new ArrayList<DivEnheder>();
		DivEnheder enhed = new DivEnheder();

		enhed.setTekst("millimol pr. gigajoule");

		TakstDataset<DivEnheder> hiddenDataset = new TakstDataset<DivEnheder>(takst, enheder, DivEnheder.class);
		takst.addDataset(hiddenDataset);

		// Setup database mocking.

		Connection con = mock(Connection.class);

		MySQLTemporalDao realDao = new MySQLTemporalDao(con);

		dao = spy(realDao);

		laegemiddeltableMock = mock(MySQLTemporalTable.class);

		doReturn(laegemiddeltableMock).when(dao).getTable(Laegemiddel.class);
		*/
	}


	@Test
	public void testPersistOneLaegemiddel() throws Exception
	{
		/*
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(false);

		// Simulate no existing entities.

		dao.persistCompleteDatasets(takst.getDatasets());

		// Verify that the new record is inserted.
		verify(laegemiddeltableMock, times(1)).insertRow(eq(laegemiddel), any(Date.class));
		*/
	}


	@Test
	public void testDeltaPutChanged() throws Exception
	{
		/*
		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		// So it must be updated.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(DateUtils.toDate(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(DateUtils.FUTURE);

		// Simulate that the entity has changed.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(Record.class))).thenReturn(false);

		dao.persistCompleteDatasets(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(1)).insertAndUpdateRow(eq(laegemiddel), any(Date.class));

		// Verify that the existing record is updated
		verify(laegemiddeltableMock, times(1)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Date.class));
		*/
	}


	@Test
	public void testDeltaPutUnchanged() throws Exception
	{
		/*
		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(DateUtils.toDate(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(DateUtils.FUTURE);

		// Simulate that the entity is unchanged.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(Record.class))).thenReturn(true);

		dao.persistCompleteDatasets(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(0)).insertRow(eq(laegemiddel), any(Date.class));

		// Verify that the existing record is not updated
		verify(laegemiddeltableMock, times(0)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Date.class));
		*/
	}


	@Test
	public void testDeltaPutRemoved() throws Exception
	{
		/*
		// An empty takst
		takst = new Takst(DateUtils.toDate(2009, 7, 1), DateUtils.toDate(2009, 7, 14));
		// ..with an empty dataset
		TakstDataset<Laegemiddel> lmr = new TakstDataset<Laegemiddel>(takst, new ArrayList<Laegemiddel>(), Laegemiddel.class);
		takst.addDataset(lmr);

		List<RecordVersion> sev = new ArrayList<RecordVersion>();
		// Simulate that there is one record
		RecordVersion sv = new RecordVersion();
		sv.id = 1;

		// Simulate that the existing row's validity range is 1950 to infinity.
		sv.validFrom = DateUtils.toDate(1950, 01, 1);
		sev.add(sv);

		when(laegemiddeltableMock.getRecordVersions(any(Date.class), any(Date.class))).thenReturn(sev);

		dao.persistCompleteDatasets(takst.getDatasets());

		// Verify that the existing record is updated
		verify(laegemiddeltableMock, times(1)).updateValidToOnEntityVersion(eq(DateUtils.toDate(2009, 7, 1)), any(RecordVersion.class), any(Date.class));
		*/
	}

}
