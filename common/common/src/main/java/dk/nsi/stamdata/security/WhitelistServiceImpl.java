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
package dk.nsi.stamdata.security;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * In order to use the same connection pooling mechanism as the Hibernate queries, this class
 * uses the workaround of being passed a Hibernate Session in its constructor and using
 * sessionFactory.doWork(...) to execute it's native SQL queries
 */
public class WhitelistServiceImpl implements WhitelistService {
	private static final Logger logger = Logger.getLogger(WhitelistServiceImpl.class);
	private static final String QUERY_IS_CVR_WHITELISTED = "SELECT cvr FROM whitelist_config WHERE component_name=? AND cvr=?";
	private static final String QUERY_GET_ALL_WHITELISTED = "SELECT cvr FROM whitelist_config WHERE component_name=?";

	private SessionFactory sessionFactory;

	public WhitelistServiceImpl(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private PreparedStatement prepareStatement(Connection con, String query) {
		try {
			con.setReadOnly(true);
		} catch (SQLException e) {
			throw new RuntimeException("Unable to set Read Only on connection", e);
		}

		PreparedStatement statement;
		try {
			statement = con.prepareStatement(query);
		} catch (SQLException e) {
			throw new RuntimeException("Unable to prepare statement for query " + query, e);
		}

		return statement;
	}

	@Override
	public List<String> getWhitelist(final String serviceName) {
		final List<String> cvrs = new ArrayList<String>();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("deprecation") Connection con = session.connection();
		try {
			PreparedStatement getAllWhitelisted = prepareStatement(con, QUERY_GET_ALL_WHITELISTED);
			getAllWhitelisted.setString(1, serviceName);
			ResultSet resultSet = getAllWhitelisted.executeQuery();
			while (resultSet.next()) {
				cvrs.add(resultSet.getString("cvr"));
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to get whitelisted CVR numbers using prepared statement", e);
		} finally {
			close(con);
			close(session);
		}

		return cvrs;
	}

	@Override
	public boolean isCvrWhitelisted(final String cvr, final String serviceName) {
		final List<String> cvrs = new ArrayList<String>();
		Session session = sessionFactory.openSession();
		session.beginTransaction();
		@SuppressWarnings("deprecation") Connection con = session.connection();
		try {
			PreparedStatement isCVRWhitelisted = prepareStatement(con, QUERY_IS_CVR_WHITELISTED);
			isCVRWhitelisted.setString(1, serviceName);
			isCVRWhitelisted.setString(2, cvr);
			ResultSet resultSet = isCVRWhitelisted.executeQuery();
			while (resultSet.next()) {
				cvrs.add(resultSet.getString("cvr"));
			}
			resultSet.close();
		} catch (SQLException e) {
			throw new RuntimeException("Unable to check if CVR is whitelisted using prepared statement", e);
		} finally {
			close(con);
			close(session);
		}

		return cvrs.size() > 0;
	}


	private void close(Session session) {
		if (session != null) {
			session.close();
		}
	}

	private void close(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.warn("Unable to close connection", e);
			}
		}
	}
}
