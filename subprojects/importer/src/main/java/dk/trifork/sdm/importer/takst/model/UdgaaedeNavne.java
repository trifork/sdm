package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.util.DateUtils;

@Output
public class UdgaaedeNavne extends TakstEntity {
    private Long drugid;        //Ref. t. LMS01
    private Long datoForAendringen;
    private String tidligereNavn;

    @Output
    public Long getDrugid() {
        return this.drugid;
    }

    public void setDrugid(Long drugid) {
        this.drugid = drugid;
    }

    @Output
    public String getDatoForAendringen() {
        return DateUtils.toISO8601date(this.datoForAendringen);
    }

    public void setDatoForAendringen(Long datoForAendringen) {
        this.datoForAendringen = datoForAendringen;
    }

    @Output
    public String getTidligereNavn() {
        return this.tidligereNavn;
    }

    public void setTidligereNavn(String tidligereNavn) {
        this.tidligereNavn = tidligereNavn;
    }

    @Id
    @Output(name = "CID")
    public String getKey() {
        return datoForAendringen + '-' + tidligereNavn + '-' + drugid;
    }
}