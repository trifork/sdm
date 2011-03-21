package dk.trifork.sdm.importer.takst.model;

import java.io.*;
import java.util.ArrayList;


public class IndikationskodeFactory extends AbstractFactory {

    private static void setFieldValue(Indikationskode obj, int fieldNo, String value) {
        if ("".equals(value)) value = null;
        switch (fieldNo) {
            case 0:
                obj.setATC(value);
                break;
            case 1:
                obj.setIndikationskode(toLong(value));
                break;
            case 2:
                obj.setDrugID(toLong(value));
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
                return 8;
            case 2:
                return 15;
            default:
                return -1;
        }
    }

    private static int getLength(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 8;
            case 1:
                return 7;
            case 2:
                return 11;
            default:
                return -1;
        }
    }

    private static int getNumberOfFields() {
        return 3;
    }

    private static String getLmsName() {
        return "LMS25";
    }

    public static ArrayList<Indikationskode> read(String rootFolder) throws IOException {

        File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

        ArrayList<Indikationskode> list = new ArrayList<Indikationskode>();
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

    private static Indikationskode parse(String line) {
        Indikationskode obj = new Indikationskode();
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