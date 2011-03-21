package dk.trifork.sdm.model;

import java.util.Calendar;


public interface StamdataEntity {

	public Object getKey();

	public Calendar getValidFrom();

	public Calendar getValidTo();
}
