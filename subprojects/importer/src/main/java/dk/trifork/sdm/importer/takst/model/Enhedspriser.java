package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Enhedspriser extends TakstEntity {

    private Long drugID;        //Ref. t. LMS01
    private Long varenummer;        //Ref. t. LMS02
    private Long prisPrEnhed;        //Pris = Ekspeditionens samlede pris (ESP)
    private Long prisPrDDD;        //Pris = ESP
    private String billigstePakning;        //Markering af billigste pakning pr. enhed for DrugID

    @Output
    public Long getDrugID() {
        return this.drugID;
    }

    public void setDrugID(Long drugID) {
        this.drugID = drugID;
    }

    @Id
    @Output
    public Long getVarenummer() {
        return this.varenummer;
    }

    public void setVarenummer(Long varenummer) {
        this.varenummer = varenummer;
    }

    @Output
    public Long getPrisPrEnhed() {
        return this.prisPrEnhed;
    }

    public void setPrisPrEnhed(Long prisPrEnhed) {
        this.prisPrEnhed = prisPrEnhed;
    }

    @Output
    public Long getPrisPrDDD() {
        return this.prisPrDDD;
    }

    public void setPrisPrDDD(Long prisPrDDD) {
        this.prisPrDDD = prisPrDDD;
    }

    @Output
    public String getBilligstePakning() {
        return this.billigstePakning;
    }

    public void setBilligstePakning(String billigstePakning) {
        this.billigstePakning = billigstePakning;
    }

    public String getKey() {
        return "" + varenummer;
    }

}