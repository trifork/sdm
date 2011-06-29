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

package com.trifork.stamdata.importer.parsers.takst.model;


import java.util.ArrayList;
import java.util.List;

import com.trifork.stamdata.importer.model.Dataset;
import com.trifork.stamdata.importer.model.Id;
import com.trifork.stamdata.importer.model.Output;
import com.trifork.stamdata.importer.model.StamdataEntity;
import com.trifork.stamdata.importer.util.DateUtils;

@Output(name = "Pakning")
public class Pakning extends TakstEntity {

    private Long drugid; // Ref. t. LMS01, felt 01
    private Long varenummer;
    private Long alfabetSekvensnr;
    private Long varenummerForDelpakning; // Udfyldes for multipakningen
    private Long antalDelpakninger; // Antal delpakn. i stor/multipakning
    private String pakningsstoerrelseKlartekst;
    private Long pakningsstoerrelseNumerisk; // Brutto
    private String pakningsstoerrelseEnhed; // Ref. t. LMS15, enhedstype 4
    private String emballagetype; // Ref. t. LMS14
    private String udleveringsbestemmelse; // Ref. t. LMS18
    private String udleveringSpeciale; // Ref. t. LMS19
    private String medicintilskudskode; // Ref. t. LMS16
    private String klausulForMedicintilskud; // Ref. t. LMS17
    private Long antalDDDPrPakning;
    private Long opbevaringstidNumerisk; // Hos distributør
    private String opbevaringstidEnhed; // Ref. t. LMS15, enhedstype 1
    private String opbevaringsbetingelser; // Ref. t. LMS20
    private Long oprettelsesdato; // Format: ååååmmdd
    private Long datoForSenestePrisaendring; // Format: ååååmmdd
    private Long udgaaetDato; // Format: ååååmmdd
    private String beregningskodeAIPRegpris; // Ref. t. LMS13
    private String pakningOptagetITilskudsgruppe; // 2 muligh.: F eller blank
    private String faerdigfremstillingsgebyr; // 2 muligh.: B eller blank
    private Long pakningsdistributoer; // Ref. t. LMS09

    @Output(name = "DrugID")
    public Long getDrugid() {
        return this.drugid;
    }

    public void setDrugid(Long drugid) {
        this.drugid = drugid;
    }

    @Id
    @Output(name = "Varenummer")
    public Long getVarenummer() {
        return this.varenummer;
    }

    public void setVarenummer(Long varenummer) {
        this.varenummer = varenummer;
    }

    @Output
    public Long getAlfabetSekvensnr() {
        return this.alfabetSekvensnr;
    }

    public void setAlfabetSekvensnr(Long alfabetSekvensnr) {
        this.alfabetSekvensnr = alfabetSekvensnr;
    }

    @Output(name = "VarenummerDelpakning")
    public Long getVarenummerForDelpakning() {
        return this.varenummerForDelpakning;
    }

    public void setVarenummerForDelpakning(Long varenummerForDelpakning) {
        this.varenummerForDelpakning = varenummerForDelpakning;
    }

    @Output
    public Long getAntalDelpakninger() {
        return this.antalDelpakninger;
    }

    public void setAntalDelpakninger(Long antalDelpakninger) {
        this.antalDelpakninger = antalDelpakninger;
    }

    @Output(name = "PakningsstoerrelseTekst")
    public String getPakningsstoerrelseKlartekst() {
        return this.pakningsstoerrelseKlartekst;
    }

    public void setPakningsstoerrelseKlartekst(String pakningsstoerrelseKlartekst) {
        this.pakningsstoerrelseKlartekst = pakningsstoerrelseKlartekst;
    }

    @Output(name = "PakningsstoerrelseNumerisk")
    public Double getPakningsstoerrelseNumerisk() {
        if (this.pakningsstoerrelseNumerisk == 0) {
            return null;
        }
        return this.pakningsstoerrelseNumerisk / 100.0;
    }

    public void setPakningsstoerrelseNumerisk(Long pakningsstoerrelseNumerisk) {
        this.pakningsstoerrelseNumerisk = pakningsstoerrelseNumerisk;
    }

    @Output(name = "Pakningsstoerrelsesenhed")
    public String getPakningsstorrelseEnhed() {
        if (this.pakningsstoerrelseNumerisk == 0) {
            return null;
        }
        return this.pakningsstoerrelseEnhed;
    }

    public void setPakningsstoerrelseEnhed(String pakningsstoerrelseEnhed) {
        this.pakningsstoerrelseEnhed = pakningsstoerrelseEnhed;
    }

    @Output(name = "EmballageTypeKode")
    public String getEmballagetype() {
        return emballagetype;
    }

    public void setEmballagetype(String emballagetype) {
        this.emballagetype = emballagetype;
    }

    public Udleveringsbestemmelser getUdleveringsbestemmelseRef() {
        return takst.getEntity(Udleveringsbestemmelser.class, udleveringsbestemmelse);
    }

    @Output
    public String getUdleveringsbestemmelse() {
	return this.udleveringsbestemmelse;
    }

    public void setUdleveringsbestemmelse(String udleveringsbestemmelse) {
        this.udleveringsbestemmelse = udleveringsbestemmelse;
    }

    public SpecialeForNBS getUdleveringSpecialeRef() {
        return takst.getEntity(SpecialeForNBS.class, udleveringSpeciale);
    }

    @Output
    public String getUdleveringSpeciale() {
	return this.udleveringSpeciale;
    }

    public void setUdleveringSpeciale(String udleveringSpeciale) {
        this.udleveringSpeciale = udleveringSpeciale;
    }

    @Output(name = "MedicintilskudsKode")
    public String getMedicintilskudskode() {
        return this.medicintilskudskode;
    }

    public void setMedicintilskudskode(String medicintilskudskode) {
        this.medicintilskudskode = medicintilskudskode;
    }

    @Output(name = "KlausuleringsKode")
    public String getKlausulForMedicintilskud() {
        return this.klausulForMedicintilskud;
    }

    public void setKlausulForMedicintilskud(String klausulForMedicintilskud) {
        this.klausulForMedicintilskud = klausulForMedicintilskud;
    }

    @Output
    public Double getAntalDDDPrPakning() {
        return (this.antalDDDPrPakning) / 1000.0;
    }

    public void setAntalDDDPrPakning(Long antalDDDPrPakning) {
        this.antalDDDPrPakning = antalDDDPrPakning;
    }

    @Output
    public Long getOpbevaringstidNumerisk() {
        return this.opbevaringstidNumerisk;
    }

    public void setOpbevaringstidNumerisk(Long opbevaringstidNumerisk) {
        this.opbevaringstidNumerisk = opbevaringstidNumerisk;
    }

    @Output
    public Long getOpbevaringstid() {
	return this.opbevaringstidNumerisk;
    }

    public NumeriskMedEnhed getOpbevaringstidRef() {
        final int enhedstype = 1;
        DivEnheder enhed = takst.getDatasetOfType(DivEnheder.class).getEntityById(opbevaringstidEnhed + "-" + enhedstype);
        return new NumeriskMedEnhed(takst, null, opbevaringstidNumerisk, enhed);

    }

    public void setOpbevaringstidEnhed(String opbevaringstidEnhed) {
        this.opbevaringstidEnhed = opbevaringstidEnhed;
    }

    public Opbevaringsbetingelser getOpbevaringsbetingelserRef() {
        return takst.getDatasetOfType(Opbevaringsbetingelser.class).getEntityById(opbevaringsbetingelser);
    }

    @Output
    public String getOpbevaringsbetingelser() {
	return this.opbevaringsbetingelser;
    }

    public void setOpbevaringsbetingelser(String opbevaringsbetingelser) {
        this.opbevaringsbetingelser = opbevaringsbetingelser;
    }

    @Output
    public String getOprettelsesdato() {
        return DateUtils.toISO8601date(this.oprettelsesdato);
    }

    public void setOprettelsesdato(Long oprettelsesdato) {
        this.oprettelsesdato = oprettelsesdato;
    }

    @Output
    public String getDatoForSenestePrisaendring() {
        return DateUtils.toISO8601date(this.datoForSenestePrisaendring);
    }

    public void setDatoForSenestePrisaendring(Long datoForSenestePrisaendring) {
        this.datoForSenestePrisaendring = datoForSenestePrisaendring;
    }

    @Output
    public String getUdgaaetDato() {
        return DateUtils.toISO8601date(this.udgaaetDato);
    }

    public void setUdgaaetDato(Long udgaaetDato) {
        this.udgaaetDato = udgaaetDato;
    }

    public Beregningsregler getBeregningskodeAIPRegprisRef() {
        return takst.getEntity(Beregningsregler.class, this.beregningskodeAIPRegpris);
    }

    @Output
    public String getBeregningskodeAIRegpris() {
	return this.beregningskodeAIPRegpris;
    }

    public void setBeregningskodeAIPRegpris(String beregningskodeAIPRegpris) {
        this.beregningskodeAIPRegpris = beregningskodeAIPRegpris;
    }

    @Output
    public boolean getPakningOptagetITilskudsgruppe() {
        return "F".equalsIgnoreCase(this.pakningOptagetITilskudsgruppe);
    }

    public void setPakningOptagetITilskudsgruppe(String pakningOptagetITilskudsgruppe) {
        this.pakningOptagetITilskudsgruppe = pakningOptagetITilskudsgruppe;
    }

    @Output
    public boolean getFaerdigfremstillingsgebyr() {
        return "B".equalsIgnoreCase(this.faerdigfremstillingsgebyr);
    }

    public void setFaerdigfremstillingsgebyr(String faerdigfremstillingsgebyr) {
        this.faerdigfremstillingsgebyr = faerdigfremstillingsgebyr;
    }

    public Firma getPakningsdistributoerRef() {
        return takst.getEntity(Firma.class, pakningsdistributoer);
    }

    @Output
    public Long getPakningsdistributoer() {
	return this.pakningsdistributoer;
    }

    public void setPakningsdistributoer(Long pakningsdistributoer) {
        this.pakningsdistributoer = pakningsdistributoer;
    }

    public Laegemiddel getLaegemiddel() {
        Dataset<Laegemiddel> laegemidler = takst.getDatasetOfType(Laegemiddel.class);
        for (StamdataEntity sde : laegemidler.getEntities()) {
            Laegemiddel lm = (Laegemiddel) sde;
            if (drugid.equals(lm.getDrugid()))
                return lm;
        }
        return null;
    }

    public List<Pakning> getSubstitutioner() {
        Dataset<Substitution> subst = takst.getDatasetOfType(Substitution.class);
        Dataset<SubstitutionAfLaegemidlerUdenFastPris> substufp = takst.getDatasetOfType(SubstitutionAfLaegemidlerUdenFastPris.class);
        List<Long> substitutionsgrupper = new ArrayList<Long>();
        for (Substitution substitution : subst.getEntities()) {
            if (substitution.getReceptensVarenummer().equals(varenummer))
                substitutionsgrupper.add(substitution.getSubstitutionsgruppenummer());
        }
        for (SubstitutionAfLaegemidlerUdenFastPris substitutionufp : substufp.getEntities()) {
            if (substitutionufp.getVarenummer().equals(varenummer))
                substitutionsgrupper.add(substitutionufp.getSubstitutionsgruppenummer());
        }

        Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
        List<Pakning> substitutioner = new ArrayList<Pakning>();
        for (Long substgruppe : substitutionsgrupper) {
            for (Substitution substitution : subst.getEntities()) {
                if (substitution.getSubstitutionsgruppenummer().equals(substgruppe)
                        && !substitution.getReceptensVarenummer().equals(this.varenummer))
                    substitutioner.add(pakninger.getEntityById(substitution.getReceptensVarenummer()));
            }
            for (SubstitutionAfLaegemidlerUdenFastPris substitution : substufp.getEntities()) {
                if (substitution.getSubstitutionsgruppenummer().equals(substgruppe)
                        && !substitution.getVarenummer().equals(this.varenummer))
                    substitutioner.add(pakninger.getEntityById(substitution.getVarenummer()));
            }
        }
        return substitutioner;
    }

    public List<Pakning> getBilligsteSubstitution() {
        Dataset<Substitution> subst = takst.getDatasetOfType(Substitution.class);
        Dataset<Pakning> pakninger = takst.getDatasetOfType(Pakning.class);
        List<Pakning> substitutioner = new ArrayList<Pakning>();
        for (Substitution substitution : subst.getEntities()) {
            if (substitution.getReceptensVarenummer().equals(varenummer)
                    && !this.varenummer.equals(substitution.getBilligsteVarenummer())) {
                Pakning p = pakninger.getEntityById(substitution.getBilligsteVarenummer());
                if (p != null)
                    substitutioner.add(p);
            }
        }

        if (substitutioner.size() == 0)
            return null;
        return substitutioner;
    }

    public Priser getPriser() {
        return takst.getEntity(Priser.class, varenummer);
    }

    public Boolean isTilHumanAnvendelse() {
        Laegemiddel lm = takst.getEntity(Laegemiddel.class, drugid);
        if (lm == null)
            return null;
        return lm.isTilHumanAnvendelse();
    }

    @Output
    public Integer getDosisdispenserbar() {
        return takst.getEntity(Laegemiddel.class, drugid).getEgnetTilDosisdispensering();
    }
}