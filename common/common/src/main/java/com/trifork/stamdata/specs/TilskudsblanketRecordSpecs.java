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

import com.trifork.stamdata.persistence.RecordSpecification;
import static com.trifork.stamdata.persistence.RecordSpecification.field;


/**
 * Specifikation af feltlænger og -navne for de forskellige balnkettyper
 */
public class TilskudsblanketRecordSpecs {
	
	public static final RecordSpecification BLANKET_RECORD_SPEC = RecordSpecification.createSpecification("Tilskudsblanket", "BlanketId", 
            field("BlanketId", 15).numerical(),
            field("BlanketTekst", 21000));


	public static final RecordSpecification BLANKET_ENKELTTILSKUD_RECORD_SPEC = RecordSpecification.createSpecification("TilskudsblanketEnkelt", "BlanketId", 
            field("BlanketId", 15).numerical(),
            field("Genansoegning", 1).numerical(),
            field("Navn", 100),
            field("Form", 100));

	public static final RecordSpecification BLANKET_FORHOJETTILSKUD_RECORD_SPEC = RecordSpecification.createSpecification("TilskudsblanketForhoejet", "BlanketId", 
            field("BlanketId", 15).numerical(),
            field("DrugId", 12).numerical());


	public static final RecordSpecification BLANKET_KRONIKERTILSKUD_RECORD_SPEC = RecordSpecification.createSpecification("TilskudsblanketKroniker", "BlanketId", 
            field("BlanketId", 15).numerical(),
            field("Genansoegning", 1).numerical());

	public static final RecordSpecification BLANKET_TERMINALTILSKUD_RECORD_SPEC = RecordSpecification.createSpecification("TilskudsblanketTerminal", "BlanketId", 
            field("BlanketId", 15).numerical());


	public static final RecordSpecification FORHOEJETTAKST_RECORD_SPEC = RecordSpecification.createSpecification("TilskudForhoejetTakst", "Varenummer", 
			field("Varenummer", 10).numerical(),
			field("Navn", 30),
			field("Form", 30),
			field("FormTekst", 150),
			field("ATCkode", 10),
			field("Styrke", 30),
            field("DrugID", 12).numerical(),
			field("PakningsTekst", 30),
			field("Udlevering", 10),
			field("Tilskudstype", 10));
}
