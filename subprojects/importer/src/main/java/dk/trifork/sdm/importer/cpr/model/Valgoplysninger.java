package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class Valgoplysninger extends CPREntity {
	private Valgret valgret;
	private Date valgretsdato;
	private Date startdato;
	private Date slettedato;

	public enum Valgret {
		ukendt(""), almindeligValgret("1"), diplomatDerStemmerIKøbenhavn("2"), diplomatOptagetPåValglisteITidligereBopælskommune("3"),
		euValgJa("4"), euValgNej("5"), euValgKøbenhavn("6");

		private final String code;
		private Valgret(String code) {
			this.code = code;
		}
		public String getCode() {
			return code;
		}
		public static Valgret fromCode(String code) {
			for (Valgret valgret : values()) {
				if (valgret.getCode().equals(code)) {
					return valgret;
				}
			}
			throw new IllegalArgumentException("Ugyldig valgret: '" + code + "'");
		}
	}

	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public Valgret getValgret() {
		return valgret;
	}

	public void setValgret(Valgret valgret) {
		this.valgret = valgret;
	}

	@Output
	public String getValgkode() {
		return valgret.getCode();
	}

	public void setValgkode(String valgkode) {
		this.valgret = Valgret.fromCode(valgkode);
	}

	@Output
	public Date getValgretsdato() {
		return valgretsdato;
	}

	public void setValgretsdato(Date valgretsdato) {
		this.valgretsdato = valgretsdato;
	}

	@Output
	public Date getStartdato() {
		return startdato;
	}

	public void setStartdato(Date startdato) {
		this.startdato = startdato;
	}

	@Output
	public Date getSlettedato() {
		return slettedato;
	}

	public void setSlettedato(Date slettedato) {
		this.slettedato = slettedato;
	}
}
