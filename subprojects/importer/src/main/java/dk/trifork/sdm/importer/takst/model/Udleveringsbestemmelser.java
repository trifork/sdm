package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Udleveringsbestemmelser extends TakstEntity {

    private String kode;        //Ref. t. LMS02, felt 10
    private String udleveringsgruppe;        //Recept: A eller B, håndkøb: H
    private String kortTekst;
    private String tekst;

    @Id
    @Output
    public String getKode() {
        return this.kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    @Output
    public String getUdleveringsgruppe() {
        return this.udleveringsgruppe;
    }

    public void setUdleveringsgruppe(String udleveringsgruppe) {
        this.udleveringsgruppe = udleveringsgruppe;
    }

    @Output
    public String getKortTekst() {
        return this.kortTekst;
    }

    public void setKortTekst(String kortTekst) {
        this.kortTekst = kortTekst;
    }

    @Output
    public String getTekst() {
        return this.tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public String getKey() {
        return "" + this.kode;
    }
}