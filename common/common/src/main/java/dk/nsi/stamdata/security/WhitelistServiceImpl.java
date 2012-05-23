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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WhitelistServiceImpl implements WhitelistService {

    private static final String QUERY_IS_CVR_WHITELISTED = "SELECT cvr FROM whitelist_config WHERE component_name=? AND cvr=?";
    private static final String QUERY_GET_ALL_WHITELISTED = "SELECT cvr FROM whitelist_config WHERE component_name=?";

    private Connection con;
    private final PreparedStatement getAllWhitelisted;
    private final PreparedStatement isCVRWhitelisted;

    public WhitelistServiceImpl(Connection con) {
        this.con = con;
        try {
            this.con.setReadOnly(true);
        } catch (SQLException e) {
            throw  new RuntimeException("Unable to set Read Only on connection", e);
        }

        try {
            getAllWhitelisted = con.prepareStatement(QUERY_GET_ALL_WHITELISTED);
        } catch (SQLException e) {
            throw  new RuntimeException("Unable to prepare statement", e);
        }
        try {
            isCVRWhitelisted = con.prepareStatement(QUERY_IS_CVR_WHITELISTED);
        } catch (SQLException e) {
            throw  new RuntimeException("Unable to prepare statement", e);
        }
    }

    @Override
    public List<String> getWhitelist(String serviceName) {
        List<String> cvrs = new ArrayList<String>();
        try {
            getAllWhitelisted.setString(1, serviceName);
            ResultSet resultSet = getAllWhitelisted.executeQuery();
            while (resultSet.next()) {
                cvrs.add(resultSet.getString("cvr"));
            }
            resultSet.close();
            return cvrs;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to get whitelisted CVR numbers using prepared statement", e);
        }

    }

    @Override
    public boolean isCvrWhitelisted(String cvr, String serviceName) {
        List<String> cvrs = new ArrayList<String>();
        try {
            isCVRWhitelisted.setString(1, serviceName);
            isCVRWhitelisted.setString(2, cvr);
            ResultSet resultSet = isCVRWhitelisted.executeQuery();
            while (resultSet.next()) {
                cvrs.add(resultSet.getString("cvr"));
            }
            resultSet.close();
            return cvrs.size() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to check if CVR is whitelisted using prepared statement", e);
        }

    }
}
