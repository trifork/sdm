package com.trifork.stamdata.importer.jobs.bemyndigelse;

import static com.trifork.stamdata.Preconditions.checkNotNull;

import java.io.File;

import javax.inject.Inject;

import com.trifork.stamdata.importer.config.KeyValueStore;
import com.trifork.stamdata.importer.parsers.Parser;
import com.trifork.stamdata.importer.parsers.annotations.ParserInformation;
import com.trifork.stamdata.importer.parsers.exceptions.OutOfSequenceException;
import com.trifork.stamdata.importer.parsers.exceptions.ParserException;
import com.trifork.stamdata.persistence.RecordPersister;

@ParserInformation(id = "bemyndigelse", name = "Bemyndigelse")
public class BemyndigelseParser implements Parser {

    
    private static final String SEQUENCE_KEY = "SEQUENCE_KEY";
    private static final String RECORD_TYPE_START = "00";
    private static final String RECORD_TYPE_ENTRY = "10";
    private static final String RECORD_TYPE_END = "99";
    private final KeyValueStore keyValueStore;

    @Inject
    BemyndigelseParser(KeyValueStore keyValueStore) {
        this.keyValueStore = keyValueStore;
    }
    
    @Override
    public void process(File dataSet, RecordPersister persister) throws OutOfSequenceException, ParserException,
            Exception {
        
        File file = checkRequiredFiles(dataSet);
        
        
    }
    
    File checkRequiredFiles(File dir) {
        
       checkNotNull(dir);
       
       for (File file : dir.listFiles()) {
           
           String fileName = file.getName();
           
           if (fileName.matches("^(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])$")) {
               return file;
           }
       }
       
//       String newSequenceNum = file.getName(); // ....
       
       // TODO Check sequence of filename
       String previousSequenceNum = keyValueStore.get(SEQUENCE_KEY);
       
       if (previousSequenceNum == null) {
           // log
       } else {
           
           // Check that the file sequence does not have "holes".
       
       }
       
  //     keyValueStore.put(SEQUENCE_KEY, newSequenceNum);
       
       throw new ParserException("File not found"); // TODO: Make subclass
    }

}
