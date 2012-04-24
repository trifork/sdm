package com.trifork.stamdata.importer.jobs.sor.sor2;

import com.google.inject.Inject;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;
import dk.sdsd.nsp.slalog.api.SLALogItem;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;

import static com.trifork.stamdata.Preconditions.checkArgument;
import static com.trifork.stamdata.importer.tools.SLALoggerHolder.getSLALogger;

@ParserInformation(id = "", name = "")
public class SORXmlParser implements Parser {
    private static final Logger logger = Logger.getLogger(SORXmlParser.class);


    private SORFullEventHandler handler = new SORFullEventHandler();
    private SAXParserFactory factory = SAXParserFactory.newInstance();


    @Inject
    RecordPersister sorPersister;

    @Override
    public void process(File dataSet, RecordPersister persister) throws OutOfSequenceException, ParserException, Exception {
        SLALogItem slaLogItem = getSLALogger().createLogItem(getClass().getSimpleName(), "dataSet");

        File[] input = dataSet.listFiles();
        checkArgument(input.length == 1, "Only one file is expected at this point.");
        File file = input[0];


        try {
            parseFile(file);

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

        SAXParser parser = factory.newSAXParser();

        if (file.getName().toLowerCase().endsWith("xml")) {
            parser.parse(file, handler);
        } else {
            logger.warn("Can only parse files with extension 'xml'! The file is ignored. file=" + file.getAbsolutePath());
        }
    }
}
