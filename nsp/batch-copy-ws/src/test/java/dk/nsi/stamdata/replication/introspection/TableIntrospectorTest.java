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
package dk.nsi.stamdata.replication.introspection;

import com.google.inject.Inject;
import dk.nsi.stamdata.replication.webservice.GuiceTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(GuiceTestRunner.class)
public class TableIntrospectorTest {

    @Inject
    private TableIntrospector introspector;


    @Test
    public void testOnNonCopyableKnownTable() throws SQLException {
        TableDescriptor tableDesc = introspector.performIntrospection("Client_permissions");
        assertNotNull(tableDesc);
        assertEquals("id", tableDesc.getPrimaryKeyName());

        Map<String,Integer> columnTypeMap = tableDesc.getColumnTypeMap();
        assertEquals(3, columnTypeMap.size());
        assertEquals(Types.BIGINT, (int)columnTypeMap.get("id"));
        assertEquals(Types.BIGINT, (int)columnTypeMap.get("client_id"));
        assertEquals(Types.LONGVARCHAR, (int)columnTypeMap.get("permissions"));

        assertFalse(tableDesc.isStamdataCopyable());
    }

    @Test
    public void testCopyableTable() throws SQLException {
        TableDescriptor tableDesc = introspector.performIntrospection("TakstVersion");
        assertNotNull(tableDesc);
        assertEquals("TakstVersionPID", tableDesc.getPrimaryKeyName());

        Map<String,Integer> columnTypeMap = tableDesc.getColumnTypeMap();
        assertTrue(tableDesc.isStamdataCopyable());
    }

}
