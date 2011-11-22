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
package com.trifork.stamdata.importer.jobs.sikrede;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.inject.Inject;

// FIXME: Implement correct logic
public class BrsUpdater {

    private static final String _ASSIGNED_DOCTOR_QUERY_START = 
            "REPLACE INTO AssignedDoctor (patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo, reference) " +
            "SELECT UPPER(SHA1(CPRnr)) as patientCpr, SYdernr as doctorOrganisationIdentifier, SIkraftDatoYder as assignedFrom, ydernummerUdlobDato as assignedTo, CONCAT('Stamdata_Sikrede', ' ', modifieddate) " +
            "as Reference from SikredeYderRelation WHERE CPR IN (";
    private static final String ASSIGNED_DOCTOR_QUERY = _ASSIGNED_DOCTOR_QUERY_START + StringUtils.repeat("?,", 19) + "?)"; //Query with 20 parameters
    
    private Connection connection;

    @Inject
    public BrsUpdater(Connection connection) 
    {
        this.connection = connection;
    }
    
    public void syncAssignedDoctorTable(List<String> cprs) throws SQLException 
    {
        /*
        PreparedStatement preparedStatement = connection.prepareStatement(ASSIGNED_DOCTOR_QUERY);
        int numberOfParams = StringUtils.countMatches(ASSIGNED_DOCTOR_QUERY, "?");
        int iterations  = cprs.size() / numberOfParams;

        int cprIdx = 0;

        // First handle all the CPRs that fits the prepared statement (ASSIGNED_DOCTOR_QUERY)
        // This way the number of server round trips are minimized since we can execute updates for 20 CPRs at a time.
        //
        for (int i = 0; i < iterations; i++) 
        {
            for (int paramIdx = 1; paramIdx <= numberOfParams; paramIdx++, cprIdx++) 
            {
                preparedStatement.setString(paramIdx, cprs.get(cprIdx));
            }
            
            preparedStatement.executeUpdate();
        }

        //Handle the remaining CPRs that does not fit into the ASSIGNED_DOCTOR_QUERY prepared statement.
        //Create new query with the proper number of params (always less than the number of params in ASSIGNED_DOCTOR_QUERY)
        int remainder = cprs.size() % numberOfParams;
        
        String remainderQuery = _ASSIGNED_DOCTOR_QUERY_START + StringUtils.repeat("?,", remainder-1) + "?)";
        preparedStatement = connection.prepareStatement(remainderQuery);
        numberOfParams = StringUtils.countMatches(remainderQuery, "?");

        for (int paramIdx = 1; paramIdx <= numberOfParams; paramIdx++, cprIdx++) 
        {
            preparedStatement.setString(paramIdx, cprs.get(cprIdx));
        }
        preparedStatement.executeUpdate();
        */
    }
}
