package dk.trifork.sdm.importer.cpr.model;

import java.util.Calendar;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Folkekirkeoplysninger extends CPREntity{
	public enum Folkekirkeforhold {
		afventer("A"), medlemAfFolkekirken("F"), medlemAfValgmenighed("M"), medlemAfFolkekirkenMenFritagetForKirkeskat("S"), udenForFolkekirken("U");

		private final String code;
		private Folkekirkeforhold(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static Folkekirkeforhold fromCode(String code) {
			for (Folkekirkeforhold forhold : values()) {
				if (forhold.getCode().equals(code)) {
					return forhold;
				}
			}
			throw new IllegalArgumentException("Ugyldig folkekirkeforhold: '" + code + "'");
		}
	}

	Folkekirkeforhold forhold;
	Calendar validFrom;
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
		return forhold.getCode();
	}

	public void setForholdskode(String forholdskode) {
		forhold = Folkekirkeforhold.fromCode(forholdskode);
	}

	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	public String getStartdatomarkering() {
		return startdatomarkering;
	}

	public void setStartdatomarkering(String startdatomarkering) {
		this.startdatomarkering = startdatomarkering;
	}
}
