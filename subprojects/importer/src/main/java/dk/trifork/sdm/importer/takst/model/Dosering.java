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

@Output
public class Dosering extends TakstEntity {

    private Long doseringKode;        //Ref. t. LMS27
    private String doseringKortTekst;
    private String doseringstekstTotal;        //Felt 05 + 06 + 07
    private Long antalEnhDoegn;
    private String doseringstekstLinie1;
    private String doseringstekstLinie2;
    private String doseringstekstLinie3;
    private String aktivInaktiv;        //A = Aktiv kode. I = Inaktiv kode (bør ikke anvendes)

    @Id
    @Output
    public Long getDoseringKode() {
        return this.doseringKode;
    }

    public void setDoseringskode(Long doseringskode) {
        this.doseringKode = doseringskode;
    }

    @Output
    public String getDoseringKortTekst() {
        return this.doseringKortTekst;
    }

    public void setDoseringKortTekst(String doseringKortTekst) {
        this.doseringKortTekst = doseringKortTekst;
    }

    @Output(name = "DoseringTekst")
    public String getDoseringstekstTotal() {
        return this.doseringstekstTotal;
    }

    public void setDoseringstekstTotal(String doseringstekstTotal) {
        this.doseringstekstTotal = doseringstekstTotal;
    }

    @Output(name = "AntalEnhederPrDoegn")
    public Double getAntalEnhDoegn() {
        return this.antalEnhDoegn / 1000.0;
    }

    public void setAntalEnhDoegn(Long antalEnhDoegn) {
        this.antalEnhDoegn = antalEnhDoegn;
    }

    @Output
    public String getDoseringstekstLinie1() {
        return this.doseringstekstLinie1;
    }

    public void setDoseringstekstLinie1(String doseringstekstLinie1) {
        this.doseringstekstLinie1 = doseringstekstLinie1;
    }

    @Output
    public String getDoseringstekstLinie2() {
        return this.doseringstekstLinie2;
    }

    public void setDoseringstekstLinie2(String doseringstekstLinie2) {
        this.doseringstekstLinie2 = doseringstekstLinie2;
    }

    @Output
    public String getDoseringstekstLinie3() {
        return this.doseringstekstLinie3;
    }

    public void setDoseringstekstLinie3(String doseringstekstLinie3) {
        this.doseringstekstLinie3 = doseringstekstLinie3;
    }

    @Output(name = "Aktiv")
    public Boolean getAktivInaktiv() {
        return "A".equalsIgnoreCase(this.aktivInaktiv);
    }

    public void setAktivInaktiv(String aktivInaktiv) {
        this.aktivInaktiv = aktivInaktiv;
    }

    public Long getKey() {
        return this.doseringKode;
    }

}