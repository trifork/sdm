package com.trifork.stamdata.importer.jobs.sikrede;

import java.util.Date;

import com.trifork.stamdata.importer.persistence.*;

@Output(name = "SikredeYderRelation")
public class SikredeYderRelation extends CPREntity {
    protected String ydernummer;
    protected Date ydernummerIkraftDato;
    protected Date ydernummerRegistreringDato;
    protected String sikringsgruppeKode;
    protected Date gruppeKodeIkraftDato;
    protected Date gruppekodeRegistreringDato;
    private YderType type;

    public enum YderType {
        current("C"),
        future("F"),
        previous("P");
        
        private final String code;
        
        private YderType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    @Id
    @Output
    public String getId() {
        return cpr + "-" + type.getCode();
    }

    @Override
	@Output
    public String getCpr() {
        return cpr;
    }

    @Output
    public Date getGruppeKodeIkraftDato() {
        return gruppeKodeIkraftDato;
    }

    public void setGruppeKodeIkraftDato(Date gruppeKodeIkraftDato) {
        this.gruppeKodeIkraftDato = gruppeKodeIkraftDato;
    }

    @Output
    public Date getGruppekodeRegistreringDato() {
        return gruppekodeRegistreringDato;
    }

    public void setGruppekodeRegistreringDato(Date gruppekodeRegistreringDato) {
        this.gruppekodeRegistreringDato = gruppekodeRegistreringDato;
    }

    @Output
    public String getSikringsgruppeKode() {
        return sikringsgruppeKode;
    }

    public void setSikringsgruppeKode(String sikringsgruppeKode) {
        this.sikringsgruppeKode = sikringsgruppeKode;
    }

    @Output
    public String getYdernummer() {
        return ydernummer;
    }

    public void setYdernummer(String ydernummer) {
        this.ydernummer = ydernummer;
    }

    @Output
    public Date getYdernummerIkraftDato() {
        return ydernummerIkraftDato;
    }

    public void setYdernummerIkraftDato(Date ydernummerIkraftDato) {
        this.ydernummerIkraftDato = ydernummerIkraftDato;
    }

    @Output
    public Date getYdernummerRegistreringDato() {
        return ydernummerRegistreringDato;
    }

    public void setYdernummerRegistreringDato(Date ydernummerRegistreringDato) {
        this.ydernummerRegistreringDato = ydernummerRegistreringDato;
    }

    public void setType(YderType type) {
        this.type = type;
    }
    
    @Output
    public String getType()
    {
    	return type.getCode();
    }

    @Override
    public String toString() {
        return "SikredeYderRelation{" +
                "gruppeKodeIkraftDato=" + gruppeKodeIkraftDato +
                ", ydernummer='" + ydernummer + '\'' +
                ", ydernummerIkraftDato=" + ydernummerIkraftDato +
                ", ydernummerRegistreringDato=" + ydernummerRegistreringDato +
                ", sikringsgruppeKode='" + sikringsgruppeKode + '\'' +
                ", gruppekodeRegistreringDato=" + gruppekodeRegistreringDato +
                ", type=" + type +
                '}';
    }
}
