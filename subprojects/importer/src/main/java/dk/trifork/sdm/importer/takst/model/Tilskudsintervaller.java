package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Tilskudsintervaller extends TakstEntity {

    private Long type;        //Patienttype: almen, barn, kroniker, terminal
    private Long niveau;        //fx 1-4 for alment tilskud, 1-3 for barn
    private Long nedreGraense;        //Nedre beløbsgrænse for niveauet (i øre)
    private Long oevreGraense;        //Øvre beløbsgrænse for niveauet (i øre)
    private Double procent;        //Tilskudsprocent

    @Output
    public Long getType() {
        return this.type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    @Output
    public Long getNiveau() {
        return this.niveau;
    }

    public void setNiveau(Long niveau) {
        this.niveau = niveau;
    }

    @Output
    public Long getNedreGraense() {
        return this.nedreGraense;
    }

    public void setNedreGraense(Long nedreGraense) {
        this.nedreGraense = nedreGraense;
    }

    @Output
    public Long getOevreGraense() {
        return this.oevreGraense;
    }

    public void setOevreGraense(Long oevreGraense) {
        this.oevreGraense = oevreGraense;
    }

    @Output
    public Double getProcent() {
        return this.procent;
    }

    public void setProcent(Double procent) {
        this.procent = procent;
    }

    @Id
    @Output(name = "CID")
    public String getKey() {
        return type + "-" + niveau;
    }
}