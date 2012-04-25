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

package com.trifork.stamdata.specs;

import static com.trifork.stamdata.persistence.RecordSpecification.field;

import com.trifork.stamdata.persistence.RecordSpecification;

public class SorFullRecordSpecs {

	public static final RecordSpecification INSTITUTIONS_EJER = RecordSpecification.createSpecification("InstitutionOwner", "pk",
				field("sorIdentifier", 8).numerical(),
				field("entityName",60),
				field("ownerType", 8).numerical(),
				field("eanLocationCodeId", 8).numerical(),
				field("postalAddressInformationId", 8).numerical(),
				field("virtualAddressInformationId", 8).numerical(),
				field("sorStatusId", 8).numerical()
			);
	
	public static final RecordSpecification SOR_STATUS = RecordSpecification.createSpecification("SorStatus", "pk",
				field("fromDate", 10),
				field("toDate",10),
				field("updatedAt",10),
				field("firstFromDate",10)
			);	
	
}
