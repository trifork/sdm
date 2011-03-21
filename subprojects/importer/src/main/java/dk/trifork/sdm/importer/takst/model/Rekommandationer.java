package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Rekommandationer extends TakstEntity {

    private Long rekommandationsgruppe;
    private Long drugID;        //Ref. t. LMS01
    private Long varenummer;        //Ref. t. LMS02
    private String rekommandationsniveau;        //Værdier: Anbefales / … med forbehold / …ikke

    @Output
    public Long getRekommandationsgruppe() {
        return this.rekommandationsgruppe;
    }

    public void setRekommandationsgruppe(Long rekommandationsgruppe) {
        this.rekommandationsgruppe = rekommandationsgruppe;
    }

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
    public String getRekommandationsniveau() {
        return this.rekommandationsniveau;
    }

    public void setRekommandationsniveau(String rekommandationsniveau) {
        this.rekommandationsniveau = rekommandationsniveau;
    }

    public Long getKey() {
        return varenummer;
    }

}