package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Folkekirkeoplysninger extends CPREntity{
	public enum Folkekirkeforhold {
		afventer("A"), medlemAfFolkekirken("F"), medlemAfValgmenighed("M"), medlemAfFolkekirkenMenFritagetForKirkeskat("S"), udenForFolkekirken("U");

		private final String kode;
		private Folkekirkeforhold(String kode) {
			this.kode = kode;
		}
		public String getKode() {
			return kode;
		}
		public static Folkekirkeforhold fromCode(String code) {
			for (Folkekirkeforhold forhold : values()) {
				if (forhold.getKode().equals(code)) {
					return forhold;
				}
			}
			throw new IllegalArgumentException("Ugyldig folkekirkeforhold: '" + code + "'");
		}
	}


	Folkekirkeforhold forhold;
	Date startdato;
	String startdatomarkering;

	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public Folkekirkeforhold getForhold() {
		return forhold;
	}

	@Output
	public String getForholdskode() {
		return forhold.getKode();
	}

	public void setForholdskode(String forholdskode) {
		forhold = Folkekirkeforhold.fromCode(forholdskode);
	}

	@Output
	public Date getStartdato() {
		return startdato;
	}

	public void setStartdato(Date startdato) {
		this.startdato = startdato;
	}

	public String getStartdatomarkering() {
		return startdatomarkering;
	}

	public void setStartdatomarkering(String startdatomarkering) {
		this.startdatomarkering = startdatomarkering;
	}
}
