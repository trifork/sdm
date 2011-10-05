/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package com.trifork.stamdata.replication.models;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;

import com.google.inject.Inject;

/**
 * Data access layer for the Client Model.
 * 
 * @author Thomas Borlum <thb@trifork.com>
 */
public class ClientDao
{
	private final Session session;

	@Inject
	ClientDao(Session session)
	{
		this.session = session;
	}

	public Client find(String id)
	{
		return (Client) session.load(Client.class, id);
	}

	public Client findByCvr(String cvr)
	{
		ClassMetadata classMetadata = session.getSessionFactory().getClassMetadata(Client.class);
		String ssnlike = "CVR:" + cvr + "-%";
		Query query = session.createQuery("FROM Client WHERE subjectSerialNumber LIKE :subjectSerialNumber");
		query.setParameter("subjectSerialNumber", ssnlike);
		return (Client) query.uniqueResult();
	}

	public Client findBySubjectSerialNumber(String subjectSerialNumber)
	{
		Query query = session.createQuery("FROM Client WHERE subjectSerialNumber = :subjectSerialNumber");
		query.setParameter("subjectSerialNumber", subjectSerialNumber);
		return (Client) query.uniqueResult();
	}

	public boolean delete(String id)
	{
		Query query = session.createQuery("DELETE Client WHERE id = :id");
		query.setParameter("id", id);
		return query.executeUpdate() == 1;
	}

	public Client create(String name, String subjectSerialNumber)
	{
		Client client = new Client(name, subjectSerialNumber);
		session.persist(client);
		return client;
	}

	@SuppressWarnings("unchecked")
	public List<Client> findAll()
	{
		return session.createQuery("FROM Client ORDER BY name").list();
	}

	public void update(Client client)
	{
		session.persist(client);
	}
}
