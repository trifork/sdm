package dk.trifork.sdm.importer.takst.model;

import java.io.*;
import java.util.ArrayList;


public class EmballagetypeKoderFactory extends AbstractFactory {

    private static void setFieldValue(EmballagetypeKoder obj, int fieldNo, String value) {
        if ("".equals(value)) value = null;
        switch (fieldNo) {
            case 0:
                obj.setKode(value);
                break;
            case 1:
                obj.setKortTekst(value);
                break;
            case 2:
                obj.setTekst(value);
                break;
            default:
                break;
        }
    }

    private static int getOffset(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 0;
            case 1:
                return 4;
            case 2:
                return 14;
            default:
                return -1;
        }
    }

    private static int getLength(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 4;
            case 1:
                return 10;
            case 2:
                return 50;
            default:
                return -1;
        }
    }

    private static int getNumberOfFields() {
        return 4;
    }

    private static String getLmsName() {
        return "LMS14";
    }

    public static ArrayList<EmballagetypeKoder> read(String rootFolder) throws IOException {

        File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

        ArrayList<EmballagetypeKoder> list = new ArrayList<EmballagetypeKoder>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), "CP865"));
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.trim().length() > 0) {
                    list.add(parse(line));
                }
            }
            return list;
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
                logger.warn("Could not close FileReader");
            }
        }
    }

    private static EmballagetypeKoder parse(String line) {
        EmballagetypeKoder obj = new EmballagetypeKoder();
        for (int fieldNo = 0; fieldNo < getNumberOfFields(); fieldNo++) {
            if (getLength(fieldNo) > 0) {
                // System.out.print("Getting field "+fieldNo+" from"+getOffset(fieldNo)+" to "+(getOffset(fieldNo)+getLength(fieldNo)));
                String value = line.substring(getOffset(fieldNo), getOffset(fieldNo) + getLength(fieldNo)).trim();
                // System.out.println(": "+value);
                setFieldValue(obj, fieldNo, value);
            }
        }
        return obj;
    }
}