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
package dk.nsi.stamdata.replication.dynamic;

import com.google.inject.Inject;
import dk.nsi.stamdata.replication.dao.DynamicViewDAO;
import dk.nsi.stamdata.replication.exceptions.DynamicViewException;
import dk.nsi.stamdata.replication.vo.ViewMapVO;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DynamicViewMapper {

    private Map<String, Set<ViewMapVO>> viewMaps = new HashMap<String, Set<ViewMapVO>>();

    @Inject
    private DynamicViewDAO dao;

    public ViewMapVO getViewMapForView(String register, String datatype, long version) {
        ViewMapVO result = getViewMapIfExist(register, datatype, version);
        if (result == null) {
            result = createAndAddViewMap(register, datatype, version);
        }
        return result;
    }

    private ViewMapVO getViewMapIfExist(String register, String datatype, long version) {
        Set<ViewMapVO> viewMapVOs = viewMaps.get(register);
        if (viewMapVOs != null) {
            for (ViewMapVO viewMap : viewMapVOs) {
                if (viewMap.getDatatype().equals(datatype) && viewMap.getVersion() == version) {
                    return viewMap;
                }
            }
        }
        return null;
    }

    private ViewMapVO createAndAddViewMap(String register, String datatype, long version) {
        try {
            ViewMapVO viewMap = dao.getViewMapForView(register, datatype, version);
            Set<ViewMapVO> viewMapSet = viewMaps.get(register);
            if (viewMapSet == null) {
                viewMapSet = new HashSet<ViewMapVO>();
            }
            viewMapSet.add(viewMap);
            viewMaps.put(register, viewMapSet);
            return viewMap;
        } catch (SQLException e) {
            throw new DynamicViewException("Failed to load view for register:" + register + " datatype:" + datatype +
                    " version:" + version);
        }
    }

}
