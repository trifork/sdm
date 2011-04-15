package dk.trifork.sdm.importer.cpr.model;

import java.util.Calendar;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;

public class KommunaleForhold extends CPREntity {
	public enum Kommunalforholdstype {
		adskilt("1"), plejebarn("2"), pensionsforhold("3"), betalingskommunekode("4"),
		friVaerdimaengde1("5"), friVaerdimaengde2("6"), friVaerdimaengde3("7"), friVaerdimaengde4("8"), friVaerdimaengde5("9");

		private final String code;
		private Kommunalforholdstype(String kode) {
			this.code = kode;
		}
		public String getCode() {
			return code;
		}
		public static Kommunalforholdstype fromCode(String code) {
			for (Kommunalforholdstype forhold : values()) {
				if (forhold.getCode().equals(code)) {
					return forhold;
				}
			}
			throw new IllegalArgumentException("Ugyldigt kommunalt forhold: '" + code + "'");
		}
	}

	private Kommunalforholdstype kommunalforholdstype;
	private String kommunalforholdskode;
	private String startdatomarkering;
	private String bemaerkninger;
	private Calendar validFrom;

	@Id
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setKommunalforholdstype(Kommunalforholdstype kommunalforholdstype) {
		this.kommunalforholdstype = kommunalforholdstype;
	}

	public Kommunalforholdstype getKommunalforholdstype() {
		return kommunalforholdstype;
	}

	public void setKommunalforholdstypekode(String kode) {
		this.kommunalforholdstype = Kommunalforholdstype.fromCode(kode);
	}

	@Output
	public String getKommunalforholdstypekode() {
		return kommunalforholdstype.getCode();
	}

	public void setKommunalforholdskode(String kommunalforholdskode) {
		this.kommunalforholdskode = kommunalforholdskode;
	}

	@Output
	public String getKommunalforholdskode() {
		return kommunalforholdskode;
	}

	public void setStartdatomarkering(String startdatomarkering) {
		this.startdatomarkering = startdatomarkering;
	}

	public String getStartdatomarkering() {
		return startdatomarkering;
	}

	public void setBemaerkninger(String bemaerkninger) {
		this.bemaerkninger = bemaerkninger;
	}

	@Output
	public String getBemaerkninger() {
		return bemaerkninger;
	}
	@Override
	public Calendar getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Calendar validFrom) {
		this.validFrom = validFrom;
	}
}
