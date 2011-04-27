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

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.ArrayList;
import java.util.List;

@Output(name = "ATC")
public class ATCKoderOgTekst extends TakstEntity {

    private String aTCNiveau1; // Felt 01-05 ref. t. LMS01, felt 15
    private String aTCNiveau2;
    private String aTCNiveau3;
    private String aTCNiveau4;
    private String aTCNiveau5;
    private String tekst;

    @Output
    public String getATCNiveau1() {
        return this.aTCNiveau1;
    }

    public void setATCNiveau1(String aTCNiveau1) {
        this.aTCNiveau1 = aTCNiveau1;
    }

    @Output
    public String getATCNiveau2() {
        return this.aTCNiveau2;
    }

    public void setATCNiveau2(String aTCNiveau2) {
        this.aTCNiveau2 = aTCNiveau2;
    }

    @Output
    public String getATCNiveau3() {
        return this.aTCNiveau3;
    }

    public void setATCNiveau3(String aTCNiveau3) {
        this.aTCNiveau3 = aTCNiveau3;
    }

    @Output
    public String getATCNiveau4() {
        return this.aTCNiveau4;
    }

    public void setATCNiveau4(String aTCNiveau4) {
        this.aTCNiveau4 = aTCNiveau4;
    }

    @Output
    public String getATCNiveau5() {
        return this.aTCNiveau5;
    }

    public void setATCNiveau5(String aTCNiveau5) {
        this.aTCNiveau5 = aTCNiveau5;
    }

    @Output(name = "ATCTekst")
    public String getTekst() {
        return this.tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    @Id
    @Output(name = "ATC")
    public String getKey() {
        return aTCNiveau1 + nulltoEmpty(aTCNiveau2) + nulltoEmpty(aTCNiveau3) + nulltoEmpty(aTCNiveau4) + nulltoEmpty(aTCNiveau5);
    }

    private static String nulltoEmpty(String s) {
        if (s == null)
            return "";
        return s;
    }

    public Boolean isTilHumanAnvendelse() {
        return !aTCNiveau1.startsWith("Q");
    }

    public List<Indikation> getIndikationer() {
        TakstDataset<Indikationskode> indikationskoder = takst.getDatasetOfType(Indikationskode.class);
        List<Indikation> indikationer = new ArrayList<Indikation>();
        for (Indikationskode ik : indikationskoder.getEntities()) {
            if (ik.getATC().equals(this.getKey()))
                indikationer.add(takst.getEntity(Indikation.class, ik.getIndikationskode()));
        }
        return indikationer;
    }

}