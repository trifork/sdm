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

    public static final RecordSpecification HEALTH_INSTITUTION_RECORD_TYPE = RecordSpecification.createSpecification("HealthInstitution", "SorIdentifier",
                field("SorIdentifier", 20).numerical(), //Den første komponent kan blive lige så lang som der er cifre i antallet af enheder:  SOR-kode der relaterer til enheden. Genereres automatisk. En SOR-kode består af fire komponenter: - Fortløbende nummer - Namespace (7 cifre: "1000016") - Partition-ID (2 cifre) - Checksum-ciffer (1 cifre). Den første mulige SOR-kode er dermed '11000016002'.
                field("EntityName", 60),
                field("InstitutionType", 8).numerical(),
                field("PharmacyIdentifier", 20),
                field("ShakIdentifier", 7)
                //,field("PostalAddressInformation")
                //,field("VisitingAddressInformation")
                //,field("VirtualAddressInformation")
                ,field("OptionalEanLocationCode", 20).numerical()
                ,
                ,field("GeographicalParentRelation", 8).numerical() //Angiver hvilken geografisk tilknytning enheden har. Enten: Selvstændig geografisk placering (1), Den organisatoriske mor (2), Anden geografisk mor (3), Ingen geografisk placering (4), Ukendt geografisk placering (5).
                ,field("GeographicalParentSorIdentifier", 8).numerical() //Angiver enhedens geografiske mor.

                //,field("SorStatus") //FromDate - Angiver dato for hvornår den sidste ændring gælder fra.,
                //,field("SorStatus") //ToDate - "Lukke dato" - Angiver den sidste gyldige dato for enheden/lokationsnummeret. Ikke obligatorisk. ,
                //,field("SorStatus") //UpdatedAt - Angiver dato for hvornår enheden sidst er ændret.
                //,field("SorStatus") //FirstFromDate - Angiver dato for hvornår enheden er gældende fra.

                //,field("ReplacesEntityCollection") //this is a list of SorIdentifier
                //,field("ReplacedByEntityCollection") //this is a list of SorIdentifier
    );




	
}
