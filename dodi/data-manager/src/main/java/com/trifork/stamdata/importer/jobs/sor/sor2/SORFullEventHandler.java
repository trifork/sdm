package com.trifork.stamdata.importer.jobs.sor.sor2;

import com.trifork.stamdata.importer.jobs.sor.sor2.xmlmodel.InstitutionOwner;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

public class SORFullEventHandler extends DefaultHandler {

    private String characterContent;

    private final String INSTITUTION_OWNER_ENTITY = "InstitutionOwnerEntity";
    private final String INSTITUTION_OWNER = "InstitutionOwner";

    InstitutionOwner currentInstitutionOwner;
    private ArrayList<InstitutionOwner> institutionOwners;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (qName.equals("SnapshotDate")) {

        }

        if (INSTITUTION_OWNER_ENTITY.equals(qName)) {
            institutionOwners = new ArrayList<InstitutionOwner>();
        }

        if (INSTITUTION_OWNER.equals(qName)) {
            currentInstitutionOwner = new InstitutionOwner();
            institutionOwners.add(currentInstitutionOwner);
        }

        super.startElement(uri, localName, qName, atts);
    }



    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("SnapshotDate")) {

        }

        if (INSTITUTION_OWNER.equals(qName)) {
            // FIXME: currentInstitutionOwner
            // institutionOwners.add(currentInstitutionOwner);
        }



        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        characterContent = new String(chars);
        super.characters(chars, start, length);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {

        super.endDocument();
    }

}
