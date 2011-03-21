package dk.trifork.sdm.importer.takst.model;

import java.io.*;
import java.util.ArrayList;


public class PakningskombinationerUdenPriserFactory extends AbstractFactory {

    private static void setFieldValue(PakningskombinationerUdenPriser obj, int fieldNo, String value) {
        if ("".equals(value)) value = null;
        switch (fieldNo) {
            case 0:
                obj.setVarenummerOrdineret(toLong(value));
                break;
            case 1:
                obj.setVarenummerSubstitueret(toLong(value));
                break;
            case 2:
                obj.setVarenummerAlternativt(toLong(value));
                break;
            case 3:
                obj.setAntalPakninger(toLong(value));
                break;
            case 5:
                obj.setInformationspligtMarkering(value);
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
                return 6;
            case 2:
                return 12;
            case 3:
                return 18;
            case 5:
                return 29;
            default:
                return -1;
        }
    }

    private static int getLength(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 6;
            case 1:
                return 6;
            case 2:
                return 6;
            case 3:
                return 2;
            case 5:
                return 1;
            default:
                return -1;
        }
    }

    private static int getNumberOfFields() {
        return 6;
    }

    public static String getLmsName() {
        return "LMS33";
    }

    public static ArrayList<PakningskombinationerUdenPriser> read(String rootFolder) throws IOException {

        File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

        ArrayList<PakningskombinationerUdenPriser> list = new ArrayList<PakningskombinationerUdenPriser>();
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

    private static PakningskombinationerUdenPriser parse(String line) {
        PakningskombinationerUdenPriser obj = new PakningskombinationerUdenPriser();
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