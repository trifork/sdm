package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Pakningskombinationer extends TakstEntity {

    private Long varenummerOrdineret;        //Vnr. på pakningen anført på recepten
    private Long varenummerSubstitueret;        //Vnr. på en pakning der evt. kan substitueres til
    private Long varenummerAlternativt;        //Vnr. for en mindre, billigere pakning
    private Long antalPakninger;        //Antal af den alternative pakning
    private Long ekspeditionensSamledePris;        //ESP for den alternative pakningskombination
    private String informationspligtMarkering;        //Markering (stjerne *) for informationspligt

    @Output
    public Long getVarenummerOrdineret() {
        return this.varenummerOrdineret;
    }

    public void setVarenummerOrdineret(Long varenummerOrdineret) {
        this.varenummerOrdineret = varenummerOrdineret;
    }

    @Output
    public Long getVarenummerSubstitueret() {
        return this.varenummerSubstitueret;
    }

    public void setVarenummerSubstitueret(Long varenummerSubstitueret) {
        this.varenummerSubstitueret = varenummerSubstitueret;
    }

    @Output
    public Long getVarenummerAlternativt() {
        return this.varenummerAlternativt;
    }

    public void setVarenummerAlternativt(Long varenummerAlternativt) {
        this.varenummerAlternativt = varenummerAlternativt;
    }

    @Output
    public Long getAntalPakninger() {
        return this.antalPakninger;
    }

    public void setAntalPakninger(Long antalPakninger) {
        this.antalPakninger = antalPakninger;
    }

    @Output
    public Long getEkspeditionensSamledePris() {
        return this.ekspeditionensSamledePris;
    }

    public void setEkspeditionensSamledePris(Long ekspeditionensSamledePris) {
        this.ekspeditionensSamledePris = ekspeditionensSamledePris;
    }

    @Output
    public String getInformationspligtMarkering() {
        return this.informationspligtMarkering;
    }

    public void setInformationspligtMarkering(String informationspligtMarkering) {
        this.informationspligtMarkering = informationspligtMarkering;
    }

    @Id
    @Output(name = "CID")
    public String getKey() {
        return "" + varenummerOrdineret + '-' + varenummerSubstitueret + '-' + varenummerAlternativt + '-' + antalPakninger;
    }

}