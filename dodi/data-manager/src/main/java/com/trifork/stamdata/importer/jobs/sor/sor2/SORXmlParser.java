/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Contributor(s): Contributors are attributed in the source code
 * where applicable.
 *
 * The Original Code is "Stamdata".
 *
 * The Initial Developer of the Original Code is Trifork Public A/S.
 *
 * Portions created for the Original Code are Copyright 2011,
 * Lægemiddelstyrelsen. All Rights Reserved.
 *
 * Portions created for the FMKi Project are Copyright 2011,
 * National Board of e-Health (NSI). All Rights Reserved.
 */

package com.trifork.stamdata.importer.jobs.sor.sor2;

import com.google.inject.Inject;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import org.apache.log4j.MDC;
import org.xml.sax.SAXException;

import javax.inject.Provider;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.IOException;

import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;

@ParserInformation(id = "sor", name = "Sor")
public class SORXmlParser implements Parser {
//    private static final Logger logger = Logger.getLogger(SORXmlParser.class);

    private final SAXParser saxParser;
    private final Provider<SORFullEventHandler> saxEventHandlers;

    @Inject
    RecordPersister sorPersister;
    
    @Inject
    public SORXmlParser(SAXParser saxParser, Provider<SORFullEventHandler> saxEventHandlers) {
    	this.saxParser = saxParser;
        this.saxEventHandlers = saxEventHandlers;
    }

    @Override
    public void process(File dataSet, RecordPersister persister) throws OutOfSequenceException, ParserException, Exception {
        SLALogItem slaLogItem = getSLALogger().createLogItem(getClass().getSimpleName(), "dataSet");

        File fileToParse = null;
        File[] input = dataSet.listFiles();
        //checkArgument(input.length == 1, "Only one file is expected at this point.");
        for (File fullXml : input) {
        	if ("SOR_FULL2.xml".equals(fullXml.getName())) {
        		fileToParse = fullXml;
        	}
        }

        try {
            parseFile(fileToParse);

            slaLogItem.setCallResultOk();
            slaLogItem.store();

        } catch (Exception e) {
            slaLogItem.setCallResultError(getClass().getSimpleName() + " Failed - Cause: " + e.getMessage());
            slaLogItem.store();

            throw e;
        }
    }

    private void parseFile(File file) throws SAXException, ParserConfigurationException, IOException {

        MDC.put("filename", file.getName());

        if (file.getName().toLowerCase().endsWith("xml")) {
            SORFullEventHandler eventHandler = saxEventHandlers.get();
            saxParser.parse(file, eventHandler);
        } else {
            //logger.warn("Can only parse files with extension 'xml'! The file is ignored. file=" + file.getAbsolutePath());
        }
    }
}
