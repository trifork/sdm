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

package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.CompleteDataset;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TakstDataset<T extends TakstEntity> extends CompleteDataset<T> {
    private Takst takst;
    Logger logger = LoggerFactory.getLogger(getClass());

    public TakstDataset(Takst takst, List<T> entities, Class<T> type) {
        super(type, entities, takst.getValidFrom(), takst.getValidTo());
        for (TakstEntity entity : entities) {
            entity.takst = takst;
        }
        this.takst = takst;
    }

    @Override
    public Calendar getValidFrom() {
        return takst.getValidFrom();
    }

    @Override
    public Calendar getValidTo() {
        return takst.getValidTo();
    }

    @Override
    public void addEntity(T entity) {
        super.addEntity(entity);
        entity.takst = takst;

    }


}
