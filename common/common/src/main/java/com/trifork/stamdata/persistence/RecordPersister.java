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
package com.trifork.stamdata.persistence;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.List;

import com.google.inject.Inject;
import com.trifork.stamdata.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;

import com.google.common.collect.Lists;
import com.trifork.stamdata.persistence.RecordSpecification.FieldSpecification;

//FIXME: This class has all the fetcher methods I moved Friday morning ;( Will have to be moved
public class RecordPersister
{
    private final Connection connection;
    private final Instant transactionTime;

    @Inject
    public RecordPersister(Connection connection, Instant transactionTime)
    {
        this.connection = connection;
        this.transactionTime = transactionTime;
    }

    public void persist(Record record, RecordSpecification specification) throws SQLException
    {
        Preconditions.checkNotNull(record);
        Preconditions.checkNotNull(specification);
        Preconditions.checkArgument(specification.conformsToSpecifications(record));

        /*
        PreparedStatement statement = connection.prepareStatement(format("UPDATE %s SET validTo = ?, modifiedDate = ? WHERE %s = ? AND validTo IS NULL", specification.getTable(), specification.getKeyColumn()));
        statement.setObject(1, transactionTime.toDate());
        statement.setObject(2, transactionTime.toDate());
        statement.setObject(3, record.get(specification.getKeyColumn()));
        statement.close();
        */

        // Data dumps from Yderregister and "Sikrede" contains history information and are therefore handled
        // differently from all other register types. The data contained in each input record is appended directly
        // to the database instead of updating existing records.

        PreparedStatement insertRecordStatement = connection.prepareStatement(createInsertStatementSql(specification));

        populateInsertStatement(insertRecordStatement, record, specification);
        insertRecordStatement.execute();

        insertRecordStatement.close();
    }

    public void populateInsertStatement(PreparedStatement preparedStatement, Record record, RecordSpecification recordSpec) throws SQLException
    {
        Preconditions.checkArgument(recordSpec.conformsToSpecifications(record), "The record does not conform to it's spec.");

        int index = 1;

        for (FieldSpecification fieldSpecification: recordSpec.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
                if (fieldSpecification.type == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
                {
                    preparedStatement.setString(index, (String) record.get(fieldSpecification.name));
                }
                else if (fieldSpecification.type == RecordSpecification.RecordFieldType.NUMERICAL)
                {
                    preparedStatement.setInt(index, (Integer) record.get(fieldSpecification.name));
                }
                else if (fieldSpecification.type == RecordSpecification.RecordFieldType.DATE)
                {
                    preparedStatement.setDate(index, (Date) record.get(fieldSpecification.name));
                }
                else
                {
                    throw new AssertionError("RecordType was not set correctly in the specification");
                }

                index++;
            }
        }

        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
        preparedStatement.setTimestamp(index++, new Timestamp(transactionTime.getMillis()));
    }

    public String createInsertStatementSql(RecordSpecification specification)
    {
        StringBuilder builder = new StringBuilder();

        builder.append("INSERT INTO ").append(specification.getTable()).append(" (");

        List<String> fieldNames = Lists.newArrayList();
        List<String> questionMarks = Lists.newArrayList();

        for (FieldSpecification fieldSpecification: specification.getFieldSpecs())
        {
            if(fieldSpecification.persistField)
            {
                fieldNames.add(fieldSpecification.name);
                questionMarks.add("?");
            }
        }

        fieldNames.add("ValidFrom");
        questionMarks.add("?");

        fieldNames.add("ModifiedDate");
        questionMarks.add("?");

        builder.append(StringUtils.join(fieldNames, ", "));
        builder.append(") VALUES (");
        builder.append(StringUtils.join(questionMarks, ", "));
        builder.append(")");

        return builder.toString();
    }
}
