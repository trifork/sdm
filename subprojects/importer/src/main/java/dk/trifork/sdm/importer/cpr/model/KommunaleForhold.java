package dk.trifork.sdm.importer.cpr.model;

import java.util.Date;

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
	private Date startdato;
	private String startdatomarkering;
	private String bemaerkninger;

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

	public void setBemaerkninger(String bemaerkninger) {
		this.bemaerkninger = bemaerkninger;
	}

	@Output
	public String getBemaerkninger() {
		return bemaerkninger;
	}
}
