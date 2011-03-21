package dk.trifork.sdm.importer.autorisationsregister.model;

import dk.trifork.sdm.model.AbstractStamdataEntity;
import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.model.StamdataEntity;

import java.util.Calendar;
import java.util.StringTokenizer;

@Output
public class Autorisation extends AbstractStamdataEntity implements StamdataEntity{
    private String nummer;
    private String cpr;
    private String efternavn;
    private String fornavn;
    private String uddKode;
    
    Autorisationsregisterudtraek dataset;

	public Autorisation(String line){
		StringTokenizer st = new StringTokenizer(line, ";");
		nummer = st.nextToken();
		cpr = st.nextToken();
		efternavn = st.nextToken();
		fornavn = st.nextToken();
		uddKode = st.nextToken();
	}
	
	@Output
	@Id
	public String getAutorisationsnummer() {
		return nummer;
	}

	@Output
	public String getCpr() {
		return cpr;
	}

	@Output
	public String getEfternavn() {
		return efternavn;
	}

	@Output
	public String getFornavn() {
		return fornavn;
	}

	@Output
	public String getUddannelsesKode() {
		return uddKode;
	}


	@Override
	public Calendar getValidFrom() {
		return dataset.getValidFrom();
	}

	@Override
	public Calendar getValidTo() {
		return FUTURE;
	}
	
}

