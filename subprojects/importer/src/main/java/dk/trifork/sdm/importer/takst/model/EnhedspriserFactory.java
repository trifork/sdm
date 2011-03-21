package dk.trifork.sdm.importer.takst.model;

import java.io.*;
import java.util.ArrayList;


public class EnhedspriserFactory extends AbstractFactory {

    private static void setFieldValue(Enhedspriser obj, int fieldNo, String value) {
        if ("".equals(value)) value = null;
        switch (fieldNo) {
            case 0:
                obj.setDrugID(toLong(value));
                break;
            case 1:
                obj.setVarenummer(toLong(value));
                break;
            case 2:
                obj.setPrisPrEnhed(toLong(value));
                break;
            case 3:
                obj.setPrisPrDDD(toLong(value));
                break;
            case 4:
                obj.setBilligstePakning(value);
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
                return 11;
            case 2:
                return 17;
            case 3:
                return 26;
            case 4:
                return 35;
            default:
                return -1;
        }
    }

    private static int getLength(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 11;
            case 1:
                return 6;
            case 2:
                return 9;
            case 3:
                return 9;
            case 4:
                return 1;
            default:
                return -1;
        }
    }

    private static int getNumberOfFields() {
        return 5;
    }

    public static String getLmsName() {
        return "LMS31";
    }

    public static ArrayList<Enhedspriser> read(String rootFolder) throws IOException {

        File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

        ArrayList<Enhedspriser> list = new ArrayList<Enhedspriser>();
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

    private static Enhedspriser parse(String line) {
        Enhedspriser obj = new Enhedspriser();
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