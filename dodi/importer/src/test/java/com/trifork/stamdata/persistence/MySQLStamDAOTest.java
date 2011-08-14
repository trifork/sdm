// The contents of this file are subject to the Mozilla Public
// License Version 1.1 (the "License"); you may not use this file
// except in compliance with the License. You may obtain a copy of
// the License at http://www.mozilla.org/MPL/
//
// Software distributed under the License is distributed on an "AS
// IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
// implied. See the License for the specific language governing
// rights and limitations under the License.
//
// Contributor(s): Contributors are attributed in the source code
// where applicable.
//
// The Original Code is "Stamdata".
//
// The Initial Developer of the Original Code is Trifork Public A/S.
//
// Portions created for the Original Code are Copyright 2011,
// Lægemiddelstyrelsen. All Rights Reserved.
//
// Portions created for the FMKi Project are Copyright 2011,
// National Board of e-Health (NSI). All Rights Reserved.

package com.trifork.stamdata.persistence;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.trifork.stamdata.Helpers;
import com.trifork.stamdata.importer.jobs.dkma.*;
import com.trifork.stamdata.importer.jobs.dkma.model.*;
import com.trifork.stamdata.importer.persistence.*;
import com.trifork.stamdata.importer.persistence.TableWrapper.StamdataEntityVersion;
import com.trifork.stamdata.importer.util.Dates;


public class MySQLStamDAOTest
{
	private TakstVersion takst;
	private Laegemiddel laegemiddel;
	private AuditingPersister dao;
	private TableWrapper<?> laegemiddeltableMock;

	@Before
	public void setUp() throws Exception
	{
		takst = new TakstVersion(Dates.newDateDK(2009, 7, 1), Dates.newDateDK(2009, 7, 14));

		// Add a dataset to the takst with one member.

		List<Laegemiddel> list = new ArrayList<Laegemiddel>();
		laegemiddel = new Laegemiddel();
		laegemiddel.setDrugID(1l);
		laegemiddel.setNavn("Zymedolinatexafylitungebraekker");
		list.add(laegemiddel);
		TakstDataset<Laegemiddel> dataset = new TakstDataset<Laegemiddel>(takst, list, Laegemiddel.class);
		takst.addDataset(dataset);

		// Add a dataset to the takst, which should be ignored because it is
		// not
		// rootMember.

		List<Enhed> enheder = Lists.newArrayList();
		Enhed enhed = new Enhed();
		enhed.setTekst("millimol pr. gigajoule");
		TakstDataset<Enhed> hiddenDataset = new TakstDataset<Enhed>(takst, enheder, Enhed.class);
		takst.addDataset(hiddenDataset);

		Connection con = Helpers.getConnection();
		AuditingPersister realDao = new AuditingPersister(con);
		dao = spy(realDao);
		laegemiddeltableMock = mock(TableWrapper.class);
		doReturn(laegemiddeltableMock).when(dao).getTable(Laegemiddel.class);
	}

	@Test
	public void testPersistOneLaegemiddel() throws Exception
	{
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(false);

		// Simulate no existing entities

		dao.persist(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(1)).insertRow(eq(laegemiddel), any(Date.class));
	}

	@Test
	public void testDeltaPutChanged() throws Exception
	{
		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		// So it must be updated.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(Dates.newDateDK(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(Dates.THE_END_OF_TIME);

		// Simulate that the entity has changed.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(Record.class))).thenReturn(false);

		dao.persist(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(1)).insertAndUpdateRow(eq(laegemiddel), any(Date.class));

		// Verify that the existing record is updated
		verify(laegemiddeltableMock, times(1)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Date.class));
	}

	@Test
	public void testDeltaPutUnchanged() throws Exception
	{
		// Simulate that the entity is already present.
		when(laegemiddeltableMock.fetchEntityVersions(anyObject(), any(Date.class), any(Date.class))).thenReturn(true);

		// Simulate that the existing row's validity range is 1950 to infinity.
		when(laegemiddeltableMock.getCurrentRowValidFrom()).thenReturn(Dates.newDateDK(1950, 01, 1));
		when(laegemiddeltableMock.getCurrentRowValidTo()).thenReturn(Dates.THE_END_OF_TIME);

		// Simulate that the entity is unchanged.
		when(laegemiddeltableMock.dataInCurrentRowEquals(any(Record.class))).thenReturn(true);

		dao.persist(takst.getDatasets());

		// Verify that the new record is inserted
		verify(laegemiddeltableMock, times(0)).insertRow(eq(laegemiddel), any(Date.class));

		// Verify that the existing record is not updated
		verify(laegemiddeltableMock, times(0)).updateValidToOnCurrentRow(eq(takst.getValidFrom()), any(Date.class));
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testDeltaPutRemoved() throws Exception
	{
		// An empty takst.

		takst = new TakstVersion(Dates.newDateDK(2009, 7, 1), Dates.newDateDK(2009, 7, 14));
		// ..with an empty dataset
		TakstDataset<?> lmr = new TakstDataset(takst, Lists.newArrayList(), Laegemiddel.class);
		takst.addDataset(lmr);

		List<StamdataEntityVersion> sev = new ArrayList<StamdataEntityVersion>();
		// Simulate that there is one record
		StamdataEntityVersion sv = new StamdataEntityVersion();
		sv.id = 1;

		// Simulate that the existing row's validity range is 1950 to infinity.
		sv.validFrom = Dates.newDateDK(1950, 01, 1);
		sev.add(sv);

		when(laegemiddeltableMock.getEntityVersions(any(Date.class), any(Date.class))).thenReturn(sev);

		dao.persist(takst.getDatasets());

		// Verify that the existing record is updated.
		verify(laegemiddeltableMock, times(1)).updateValidToOnEntityVersion(eq(Dates.newDateDK(2009, 7, 1)), any(StamdataEntityVersion.class), any(Date.class));
	}
}
