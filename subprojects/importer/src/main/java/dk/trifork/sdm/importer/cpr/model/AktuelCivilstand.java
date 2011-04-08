package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class AktuelCivilstand extends CPREntity {
	public enum Civilstand {
		ugift("U"), gift("G"), fraskilt("F"), enkeEllerEnkemand("E"), registreretPartnerskab("P"),
		ophaevetPartnerskab("O"), laengstlevendePartner("L"), doed("D");

		private final String code;
		private Civilstand(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static Civilstand fromCode(String code) {
			for (Civilstand civilstand : values()) {
				if (civilstand.getCode().equals(code)) {
					return civilstand;
				}
			}
			throw new IllegalArgumentException("Ugyldig civilstand: '" + code + "'");
		}
	}
	
	private Civilstand civilstand;
	private String aegtefaellepersonnummer;
	private Date aegtefaellefoedselsdato;
	private String aegtefaellefoedselsdatomarkering;
	private String aegtefaellenavn;
	private String aegtefaellenavnmarkering;
	private Date startdato;
	private String startdatomarkering;
	private Date separation;
	
	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setCivilstand(Civilstand civilstand) {
		this.civilstand = civilstand;
	}

	public Civilstand getCivilstand() {
		return civilstand;
	}
	
	public void setCivilstandskode(String kode) {
		this.civilstand = Civilstand.fromCode(kode);
	}
	
	@Output
	public String getCivilstandskode() {
		return civilstand.getCode();
	}

	public void setAegtefaellepersonnummer(String aegtefaellepersonnummer) {
		this.aegtefaellepersonnummer = aegtefaellepersonnummer;
	}

	@Output
	public String getAegtefaellepersonnummer() {
		return aegtefaellepersonnummer;
	}

	public void setAegtefaellefoedselsdato(Date aegtefaellefoedselsdato) {
		this.aegtefaellefoedselsdato = aegtefaellefoedselsdato;
	}

	@Output
	public Date getAegtefaellefoedselsdato() {
		return aegtefaellefoedselsdato;
	}

	public void setAegtefaellefoedselsdatomarkering(String aegtefaellefoedselsdatomarkering) {
		this.aegtefaellefoedselsdatomarkering = aegtefaellefoedselsdatomarkering;
	}

	public String getAegtefaellefoedselsdatomarkering() {
		return aegtefaellefoedselsdatomarkering;
	}

	public void setAegtefaellenavn(String aegtefaellenavn) {
		this.aegtefaellenavn = aegtefaellenavn;
	}

	@Output
	public String getAegtefaellenavn() {
		return aegtefaellenavn;
	}

	public void setAegtefaellenavnmarkering(String aegtefaellenavnmarkering) {
		this.aegtefaellenavnmarkering = aegtefaellenavnmarkering;
	}

	public String getAegtefaellenavnmarkering() {
		return aegtefaellenavnmarkering;
	}

	public void setStartdato(Date startdato) {
		this.startdato = startdato;
	}

	@Output
	public Date getStartdato() {
		return startdato;
	}

	public void setStartdatomarkering(String startdatomarkering) {
		this.startdatomarkering = startdatomarkering;
	}

	public String getStartdatomarkering() {
		return startdatomarkering;
	}

	public void setSeparation(Date separation) {
		this.separation = separation;
	}

	@Output
	public Date getSeparation() {
		return separation;
	}
}
