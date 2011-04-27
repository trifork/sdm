package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class PakningskombinationerUdenPriser extends TakstEntity {

    private Long varenummerOrdineret; // Vnr. på pakningen anført på recepten
    private Long varenummerSubstitueret; // Vnr. på en pakning der evt. kan substitueres til
    private Long varenummerAlternativt; // Vnr. for en mindre, billigere pakning
    private Long antalPakninger; // Antal af den alternative pakning
    private String informationspligtMarkering; // Markering (stjerne *) for informationspligt

    @Id
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
    public String getInformationspligtMarkering() {
        return this.informationspligtMarkering;
    }

    public void setInformationspligtMarkering(String informationspligtMarkering) {
        this.informationspligtMarkering = informationspligtMarkering;
    }

    public Long getKey() {
        return this.varenummerOrdineret;
    }

}