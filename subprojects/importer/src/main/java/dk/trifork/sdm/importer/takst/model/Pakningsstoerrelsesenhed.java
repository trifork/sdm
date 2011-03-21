package dk.trifork.sdm.importer.takst.model;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

@Output
public class Pakningsstoerrelsesenhed extends TakstEntity {

    private final DivEnheder enheder;

    public Pakningsstoerrelsesenhed(DivEnheder enheder) {
        this.enheder = enheder;
    }

    @Id
    @Output
    public String getPakningsstoerrelsesenhedKode() {
        return enheder.getKode();
    }

    @Output
    public String getPakningsstoerrelsesenhedTekst() {
        return enheder.getTekst();
    }
    
    @Output
    public String getPakningsstoerrelsesenhedKortTekst() {
    	return enheder.getKortTekst();
    }

    @Override
    public String getKey() {
        return enheder.getKode();
    }

}
