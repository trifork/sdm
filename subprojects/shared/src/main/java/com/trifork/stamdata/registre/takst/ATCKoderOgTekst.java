package com.trifork.stamdata.registre.takst;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "ATC")
public class ATCKoderOgTekst extends TakstRecord {

	private String aTCNiveau1; // Felt 01-05 ref. t. LMS01, felt 15
	private String aTCNiveau2;
	private String aTCNiveau3;
	private String aTCNiveau4;
	private String aTCNiveau5;
	private String tekst;


	public String getATCNiveau1() {

		return this.aTCNiveau1;
	}


	public void setATCNiveau1(String aTCNiveau1) {

		this.aTCNiveau1 = aTCNiveau1;
	}


	public String getATCNiveau2() {

		return this.aTCNiveau2;
	}


	public void setATCNiveau2(String aTCNiveau2) {

		this.aTCNiveau2 = aTCNiveau2;
	}


	public String getATCNiveau3() {

		return this.aTCNiveau3;
	}


	public void setATCNiveau3(String aTCNiveau3) {

		this.aTCNiveau3 = aTCNiveau3;
	}


	public String getATCNiveau4() {

		return this.aTCNiveau4;
	}


	public void setATCNiveau4(String aTCNiveau4) {

		this.aTCNiveau4 = aTCNiveau4;
	}


	public String getATCNiveau5() {

		return this.aTCNiveau5;
	}


	public void setATCNiveau5(String aTCNiveau5) {

		this.aTCNiveau5 = aTCNiveau5;
	}


	@Column(name = "ATCTekst")
	public String getTekst() {

		return this.tekst;
	}


	public void setTekst(String tekst) {

		this.tekst = tekst;
	}


	@Override
	@Id
	@Column(name = "ATC")
	public String getKey() {

		return aTCNiveau1 + nulltoEmpty(aTCNiveau2) + nulltoEmpty(aTCNiveau3) + nulltoEmpty(aTCNiveau4)
				+ nulltoEmpty(aTCNiveau5);
	}


	private static String nulltoEmpty(String s) {

		if (s == null) return "";
		return s;
	}


	public Boolean isTilHumanAnvendelse() {

		return !aTCNiveau1.startsWith("Q");
	}


	public List<Indikation> getIndikationer() {

		TakstDataset<Indikationskode> indikationskoder = takst.getDatasetOfType(Indikationskode.class);
		List<Indikation> indikationer = new ArrayList<Indikation>();
		for (Indikationskode ik : indikationskoder.getEntities()) {
			if (ik.getATC().equals(this.getKey()))
				indikationer.add(takst.getEntity(Indikation.class, ik.getIndikationskode()));
		}
		return indikationer;
	}

}