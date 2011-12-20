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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.trifork.stamdata.Preconditions;
import com.trifork.stamdata.persistence.Record;

public class BrsUpdater {

    static final long NO_EXISTING_RELATIONSHIP = -1;
    private Connection connection;

    @Inject
    public BrsUpdater(Connection connection) 
    {
        this.connection = connection;
    }
    
    public void updateRecord(Record record) throws SQLException
    {
        String hashedCpr = hashCpr((String) record.get("CPRnr"));
        
        updateExistingRelationship(hashedCpr, (String) record.get("SYdernrGl"), parseSikredeRecordDate((String) record.get("SIkraftDatoYderGl")), parseSikredeRecordDate((String) record.get("SIkraftDatoYder")));
        insertRelationship(hashedCpr, (String) record.get("SYdernr"), parseSikredeRecordDate((String) record.get("SIkraftDatoYder")), null);
    }
    
    void updateExistingRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom, DateTime assignedTo) throws SQLException
    {
        long primaryKeyFromExistingRelationship = openRelationshipExists(patientCpr, doctorOrganisationIdentifier);
        if(primaryKeyFromExistingRelationship == NO_EXISTING_RELATIONSHIP)
        {
            insertRelationship(patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo);
        }
        else
        {
            closeRelationship(primaryKeyFromExistingRelationship, assignedTo);
        }
    }
    
    long openRelationshipExists(String patientCpr, String doctorOrganisationIdentifier) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT pk FROM AssignedDoctor WHERE patientCpr = ? AND doctorOrganisationIdentifier = ? AND assignedTo IS NULL");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        ResultSet resultSet = preparedStatement.executeQuery();
        if(resultSet.next())
        {
            return resultSet.getLong(1);
        }
        else
        {
            return NO_EXISTING_RELATIONSHIP;
        }
    }
    
    void closeRelationship(long primaryKey, DateTime assignedTo) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE AssignedDoctor SET assignedTo = ? WHERE pk = ?");
        preparedStatement.setDate(1, new Date(assignedTo.getMillis()));
        preparedStatement.setLong(2, primaryKey);
        preparedStatement.executeUpdate();
    }
    
    void insertRelationship(String patientCpr, String doctorOrganisationIdentifier, DateTime assignedFrom, DateTime assignedTo) throws SQLException
    {
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AssignedDoctor (patientCpr, doctorOrganisationIdentifier, assignedFrom, assignedTo, reference) VALUES (?, ?, ?, ?, ?)");
        preparedStatement.setString(1, patientCpr);
        preparedStatement.setString(2, doctorOrganisationIdentifier);
        preparedStatement.setDate(3, new Date(assignedFrom.getMillis()));
        if(assignedTo != null)
        {
            preparedStatement.setDate(4, new Date(assignedTo.getMillis()));
        }
        else
        {
            preparedStatement.setNull(4, java.sql.Types.NULL);
        }
        preparedStatement.setString(5, new DateTime().toString());
        preparedStatement.executeUpdate();
    }
    
    static String hashCpr(String cpr)
    {
        Preconditions.checkArgument(cpr.length() == 10);
        try {
            return hash(cpr);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    static DateTime parseSikredeRecordDate(String date)
    {
        Preconditions.checkArgument(date.length() == 8);
        return new DateTime(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)), Integer.parseInt(date.substring(6, 8)), 0, 0, 0);
    }
    
    private static final String SHA_1 = "SHA-1";
    
    private static String hash(String string) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(SHA_1);
        digest.reset();
        byte[] bytes = digest.digest(string.getBytes());
        return getHex(bytes);
    }

    private static final String HEXES = "0123456789ABCDEF";

    private static String getHex(byte[] raw) {
        if (raw == null) {
            return null;
        }
        final StringBuilder hex = new StringBuilder(2 * raw.length);
        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
        }
        return hex.toString();
    }
}
