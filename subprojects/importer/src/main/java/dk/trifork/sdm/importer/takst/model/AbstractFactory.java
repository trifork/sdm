package dk.trifork.sdm.importer.takst.model;

import org.apache.log4j.Logger;

public abstract class AbstractFactory {
    static Logger logger = Logger.getLogger(AbstractFactory.class);

    static Double toDouble(String s) {
        if (s == null || s.trim().length() == 0) return null;
        return new Double(s);
    }

    static Long toLong(String s) {
        if (s == null || s.trim().length() == 0) return null;
        return new Long(s);
    }


}
