package com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel;


public class InstitutionOwner {

    private long sorIdentifier;

    private String entityName;
    private long ownerType;

    private EanLocationCode eanLocationCode;

    private PostalAddressInformation postalAddressInformation;

    private VirtualAddressInformation virtualAddressInformation;

    private SorStatus sorStatus;

    public InstitutionOwner() {
    }
}
