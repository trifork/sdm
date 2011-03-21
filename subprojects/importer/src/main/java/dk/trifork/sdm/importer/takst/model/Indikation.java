package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

import java.util.ArrayList;
import java.util.List;

@Output(name = "Indikation")
public class Indikation extends TakstEntity {

    private Long indikationskode;        //Ref. t. LMS25
    private String indikationstekstTotal;        //Felt 03 + 04 + 05
    private String indikationstekstLinie1;
    private String indikationstekstLinie2;
    private String indikationstekstLinie3;
    private String aktivInaktiv;        //A = Aktiv kode. I = Inaktiv kode (bør ikke anvendes)

    public List<ATCKoderOgTekst> getATC() {
        TakstDataset<Indikationskode> indikationskoder = takst.getDatasetOfType(Indikationskode.class);
        List<ATCKoderOgTekst> atcKoder = new ArrayList<ATCKoderOgTekst>();
        for (Indikationskode ik : indikationskoder.getEntities()) {
            if (ik.getIndikationskode().equals(this.getIndikationskode()))
                atcKoder.add(takst.getEntity(ATCKoderOgTekst.class, ik.getATC()));
        }
        return atcKoder;
    }

    @Id
    @Output(name = "IndikationKode")
    public Long getIndikationskode() {
        return this.indikationskode;
    }

    public void setIndikationskode(Long indikationskode) {
        this.indikationskode = indikationskode;
    }

    @Output(name = "IndikationTekst")
    public String getIndikationstekstTotal() {
        return this.indikationstekstTotal;
    }

    public void setIndikationstekstTotal(String indikationstekstTotal) {
        this.indikationstekstTotal = indikationstekstTotal;
    }

    @Output
    public String getIndikationstekstLinie1() {
        return this.indikationstekstLinie1;
    }

    public void setIndikationstekstLinie1(String indikationstekstLinie1) {
        this.indikationstekstLinie1 = indikationstekstLinie1;
    }

    @Output
    public String getIndikationstekstLinie2() {
        return this.indikationstekstLinie2;
    }

    public void setIndikationstekstLinie2(String indikationstekstLinie2) {
        this.indikationstekstLinie2 = indikationstekstLinie2;
    }

    @Output
    public String getIndikationstekstLinie3() {
        return this.indikationstekstLinie3;
    }

    public void setIndikationstekstLinie3(String indikationstekstLinie3) {
        this.indikationstekstLinie3 = indikationstekstLinie3;
    }

    @Output(name="aktiv")
    public Boolean getAktivInaktiv() {
        return "A".equalsIgnoreCase(this.aktivInaktiv);
    }

    public void setAktivInaktiv(String aktivInaktiv) {
        this.aktivInaktiv = aktivInaktiv;
    }

    public Long getKey() {
        return this.indikationskode;
    }


}