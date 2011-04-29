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

package com.trifork.stamdata.importer.takst.model;

// the entities of this type are output by these classes:
// Tidsenhed, Styrkeenhed, Pakningsstoerrelsesenhed

public class DivEnheder extends TakstEntity {

    private static final long ENHEDSTYPE_TID = 1;
    private static final long ENHEDSTYPE_STYRKE = 3;
    private static final long ENHEDSTYPE_PAKNING = 4;

    private Long enhedstype; // Styrke=3, pakning=4, tid=1
    private String kode; // LMS01, felt 12 og LMS02, felt 08 og 16
    private String kortTekst;
    private String tekst;

    public boolean isEnhedstypeTid() {
        return this.enhedstype == ENHEDSTYPE_TID;
    }

    public boolean isEnhedstypeStyrke() {
        return this.enhedstype == ENHEDSTYPE_STYRKE;
    }

    public boolean isEnhedstypePakning() {
        return this.enhedstype == ENHEDSTYPE_PAKNING;
    }

    public void setEnhedstype(Long enhedstype) {
        this.enhedstype = enhedstype;
    }

    public String getKode() {
        return this.kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public String getKortTekst() {
        return this.kortTekst;
    }

    public void setKortTekst(String kortTekst) {
        this.kortTekst = kortTekst;
    }

    public String getTekst() {
        return this.tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public String getKey() {
        /* This is a pseudo table that is not referenced or persisted
           * The 'kode' is not unique alone because the different 'Enhed'
           * can have the same code
           */
        return enhedstype + kode;
    }
}