package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output(name = "Formbetegnelse")
public class LaegemiddelformBetegnelser extends TakstEntity {

    private String kode;        //Ref. t. LMS01, felt 08
    private String tekst;
    private String aktivInaktiv;        //A (Aktiv)=DLS o.l.-I (inaktiv)=Ikke anerkendt term


    @Id
    @Output
    public String getKode() {
        return this.kode;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    @Output
    public String getTekst() {
        return this.tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    @Output(name = "Aktiv")
    public Boolean getAktivInaktiv() {
        return "A".equalsIgnoreCase(this.aktivInaktiv);
    }

    public void setAktivInaktiv(String aktivInaktiv) {
        this.aktivInaktiv = aktivInaktiv;
    }

    public String getKey() {
        return "" + this.kode;
    }

}