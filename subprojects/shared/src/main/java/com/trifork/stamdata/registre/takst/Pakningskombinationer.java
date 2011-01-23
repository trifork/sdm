package com.trifork.stamdata.registre.takst;

public class Pakningskombinationer extends TakstRecord {

	private Long varenummerOrdineret; // Vnr. på pakningen anført på recepten
	private Long varenummerSubstitueret; // Vnr. på en pakning der evt. kan
											// substitueres til
	private Long varenummerAlternativt; // Vnr. for en mindre, billigere pakning
	private Long antalPakninger; // Antal af den alternative pakning
	private Long ekspeditionensSamledePris; // ESP for den alternative
											// pakningskombination
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


	public Long getEkspeditionensSamledePris() {

		return this.ekspeditionensSamledePris;
	}


	public void setEkspeditionensSamledePris(Long ekspeditionensSamledePris) {

		this.ekspeditionensSamledePris = ekspeditionensSamledePris;
	}


	public String getInformationspligtMarkering() {

		return this.informationspligtMarkering;
	}


	public void setInformationspligtMarkering(String informationspligtMarkering) {

		this.informationspligtMarkering = informationspligtMarkering;
	}


	@Override
	public String getKey() {

		return "" + varenummerOrdineret + '-' + varenummerSubstitueret + '-' + varenummerAlternativt + '-'
				+ antalPakninger;
	}

}