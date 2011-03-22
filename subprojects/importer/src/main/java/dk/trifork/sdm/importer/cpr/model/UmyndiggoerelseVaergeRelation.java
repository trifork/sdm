package dk.trifork.sdm.importer.cpr.model;

import java.util.Calendar;
import java.util.Date;

import dk.trifork.sdm.model.Id;
import dk.trifork.sdm.model.Output;
import dk.trifork.sdm.util.DateUtils;

@Output
public class UmyndiggoerelseVaergeRelation  extends CPREntity {
	
	public enum VaergeRelationType {
		ikkeICPR,CPRFindes,adresseFindes
	}
	
	String cpr;
	Date umyndigStartDato;
	String umyndigStartDatoMarkering;
	Date umyndigSletteDato;
	String typeKode;
	VaergeRelationType type;
	String relationCpr;
	Date relationCprStartDato;
	String vaergesNavn;
	Date vaergesNavnStartDato;
	String relationsTekst1;
	String relationsTekst2;
	String relationsTekst3;
	String relationsTekst4;
	String relationsTekst5;

	
	@Id
	@Output
	public String getId() {
		return cpr + "-" + typeKode;
	}
	
	@Output
	public String getCpr() {
		return cpr;
	}

	public void setCpr(String cpr) {
		this.cpr = cpr;
	}

	public Date getUmyndigStartDato() {
		return umyndigStartDato;
	}

	public void setUmyndigStartDato(Date umyndigStartDato) {
		this.umyndigStartDato = umyndigStartDato;
	}

	public String getUmyndigStartDatoMarkering() {
		return umyndigStartDatoMarkering;
	}

	public void setUmyndigStartDatoMarkering(String umyndigStartDatoMarkering) {
		this.umyndigStartDatoMarkering = umyndigStartDatoMarkering;
	}

	public Date getUmyndigSletteDato() {
		return umyndigSletteDato;
	}

	public void setUmyndigSletteDato(Date umyndigSletteDato) {
		this.umyndigSletteDato = umyndigSletteDato;
	}

	@Output
	public String getTypeTekst() {
		if (type == null)
			return "Ukendt værge relation";
		else if (type == VaergeRelationType.ikkeICPR)
            return "Værge findes ikke i CPR";
        else if (type == VaergeRelationType.CPRFindes)
            return "Værges CPR findes";
        else if (type == VaergeRelationType.adresseFindes)
            return "Værges adresse findes";
        return null;
	}

	@Output
	public String getTypeKode() {
		return typeKode;
	}

	public void setType(String type) {
		if (type.equals("0000"))
			this.type = VaergeRelationType.ikkeICPR;
		else if (type.equals("0001"))
			this.type = VaergeRelationType.CPRFindes;
		else if (type.equals("0002"))
			this.type = VaergeRelationType.adresseFindes;
		else
			this.type = null;

		this.typeKode = type;
	}

	@Output
	public String getRelationCpr() {
		return relationCpr;
	}

	public void setRelationCpr(String relationCpr) {
		this.relationCpr = relationCpr;
	}

	@Output
	public Date getRelationCprStartDato() {
		return relationCprStartDato;
	}

	public void setRelationCprStartDato(Date relationCprStartDato) {
		this.relationCprStartDato = relationCprStartDato;
	}

	@Output
	public String getVaergesNavn() {
		return vaergesNavn;
	}

	public void setVaergesNavn(String vaergesNavn) {
		this.vaergesNavn = vaergesNavn;
	}

	@Output
	public Date getVaergesNavnStartDato() {
		return vaergesNavnStartDato;
	}

	public void setVaergesNavnStartDato(Date vaergesNavnStartDato) {
		this.vaergesNavnStartDato = vaergesNavnStartDato;
	}

	@Output
	public String getRelationsTekst1() {
		return relationsTekst1;
	}

	public void setRelationsTekst1(String relationsTekst1) {
		this.relationsTekst1 = relationsTekst1;
	}

	@Output
	public String getRelationsTekst2() {
		return relationsTekst2;
	}

	public void setRelationsTekst2(String relationsTekst2) {
		this.relationsTekst2 = relationsTekst2;
	}

	@Output
	public String getRelationsTekst3() {
		return relationsTekst3;
	}

	public void setRelationsTekst3(String relationsTekst3) {
		this.relationsTekst3 = relationsTekst3;
	}

	@Output
	public String getRelationsTekst4() {
		return relationsTekst4;
	}

	public void setRelationsTekst4(String relationsTekst4) {
		this.relationsTekst4 = relationsTekst4;
	}

	@Output
	public String getRelationsTekst5() {
		return relationsTekst5;
	}

	public void setRelationsTekst5(String relationsTekst5) {
		this.relationsTekst5 = relationsTekst5;
	}
	
	@Override
	public Calendar getValidFrom() {
		// Hvis umyndiggørelses start dato er sat til senere end produktionsdatoen for udtrækket brug det ellers brug produktionsdatoen
		return (umyndigStartDato == null) ? super.getValidFrom() : (umyndigStartDato.after(super.getValidFrom().getTime())) ? DateUtils.toCalendar(umyndigStartDato) : super.getValidFrom();
	}

	@Override
	public Calendar getValidTo() {
		return (umyndigSletteDato == null) ? super.getValidTo() : DateUtils.toCalendar(umyndigSletteDato);
	}
}