package dk.trifork.sdm.importer.cpr.model;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.util.DateUtils;

public class Valgoplysninger extends CPREntity {
	private Valgret valgret;
	private Date valgretsdato;
	private Calendar validFrom;
	private Calendar validTo;

	public enum Valgret {
		ukendt(""), almindeligValgret("1"), diplomatDerStemmerIKøbenhavn("2"), diplomatOptagetPaaValglisteITidligereBopaelskommune("3"),
		euValgJa("4"), euValgNej("5"), euValgKoebenhavn("6");

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

	@Override
	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}

	@Override
	public Calendar getValidTo() {
		return validTo;
	}

	public void setValidTo(Calendar validTo) {
		if(validTo == null) {
			this.validTo = DateUtils.FUTURE;
		}
		else {
			this.validTo = validTo;
		}
	}


}
