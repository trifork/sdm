package com.trifork.stamdata.registre.takst;

public class PakningskombinationerUdenPriser extends TakstRecord {

	private Long varenummerOrdineret; // Vnr. på pakningen anført på recepten
	private Long varenummerSubstitueret; // Vnr. på en pakning der evt. kan
											// substitueres til
	private Long varenummerAlternativt; // Vnr. for en mindre, billigere pakning
	private Long antalPakninger; // Antal af den alternative pakning
	private String informationspligtMarkering; // Markering (stjerne *) for
												// informationspligt


	public Long getVarenummerOrdineret() {

		return this.varenummerOrdineret;
	}


	public void setVarenummerOrdineret(Long varenummerOrdineret) {

		this.varenummerOrdineret = varenummerOrdineret;
	}


	public Long getVarenummerSubstitueret() {

		return this.varenummerSubstitueret;
	}


	public void setVarenummerSubstitueret(Long varenummerSubstitueret) {

		this.varenummerSubstitueret = varenummerSubstitueret;
	}


	public Long getVarenummerAlternativt() {

		return this.varenummerAlternativt;
	}


	public void setVarenummerAlternativt(Long varenummerAlternativt) {

		this.varenummerAlternativt = varenummerAlternativt;
	}


	public Long getAntalPakninger() {

		return this.antalPakninger;
	}


	public void setAntalPakninger(Long antalPakninger) {

		this.antalPakninger = antalPakninger;
	}


	public String getInformationspligtMarkering() {

		return this.informationspligtMarkering;
	}


	public void setInformationspligtMarkering(String informationspligtMarkering) {

		this.informationspligtMarkering = informationspligtMarkering;
	}


	@Override
	public Long getKey() {

		return this.varenummerOrdineret;
	}

}