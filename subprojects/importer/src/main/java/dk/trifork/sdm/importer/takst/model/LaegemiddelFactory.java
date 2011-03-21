package dk.trifork.sdm.importer.takst.model;

import java.io.*;
import java.util.ArrayList;


public class LaegemiddelFactory extends AbstractFactory {

    private static void setFieldValue(Laegemiddel obj, int fieldNo, String value) {
        if ("".equals(value)) value = null;
        switch (fieldNo) {
            case 0:
                obj.setDrugid(toLong(value));
                break;
            case 1:
                obj.setVaretype(value);
                break;
            case 2:
                obj.setVaredeltype(value);
                break;
            case 3:
                obj.setAlfabetSekvensplads(value);
                break;
            case 4:
                obj.setSpecNummer(toLong(value));
                break;
            case 5:
                obj.setNavn(value);
                break;
            case 6:
                obj.setLaegemiddelformTekst(value);
                break;
            case 7:
                obj.setFormKode(value);
                break;
            case 8:
                obj.setKodeForYderligereFormOplysn(value);
                break;
            case 9:
                obj.setStyrkeKlarTekst(value);
                break;
            case 10:
                obj.setStyrkeNumerisk(toLong(value));
                break;
            case 11:
                obj.setStyrkeEnhed(value);
                break;
            case 12:
                obj.setMTIndehaver(toLong(value));
                break;
            case 13:
                obj.setRepraesentantDistributoer(toLong(value));
                break;
            case 14:
                obj.setATC(value);
                break;
            case 15:
                obj.setAdministrationsvej(value);
                break;
            case 16:
                obj.setTrafikadvarsel(value);
                break;
            case 17:
                obj.setSubstitution(value);
                break;
            case 21:
                obj.setLaegemidletsSubstitutionsgruppe(value);
                break;
            case 22:
                obj.setEgnetTilDosisdispensering(value);
                break;
            case 23:
                obj.setDatoForAfregistrAfLaegemiddel(value);
                break;
            case 24:
                obj.setKarantaenedato(value);
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
                return 13;
            case 3:
                return 15;
            case 4:
                return 24;
            case 5:
                return 29;
            case 6:
                return 59;
            case 7:
                return 79;
            case 8:
                return 86;
            case 9:
                return 93;
            case 10:
                return 113;
            case 11:
                return 123;
            case 12:
                return 126;
            case 13:
                return 132;
            case 14:
                return 138;
            case 15:
                return 146;
            case 16:
                return 154;
            case 17:
                return 155;
            case 21:
                return 159;
            case 22:
                return 163;
            case 23:
                return 164;
            case 24:
                return 172;
            default:
                return -1;
        }
    }

    private static int getLength(int fieldNo) {
        switch (fieldNo) {
            case 0:
                return 11;
            case 1:
                return 2;
            case 2:
                return 2;
            case 3:
                return 9;
            case 4:
                return 5;
            case 5:
                return 30;
            case 6:
                return 20;
            case 7:
                return 7;
            case 8:
                return 7;
            case 9:
                return 20;
            case 10:
                return 10;
            case 11:
                return 3;
            case 12:
                return 6;
            case 13:
                return 6;
            case 14:
                return 8;
            case 15:
                return 8;
            case 16:
                return 1;
            case 17:
                return 1;
            case 21:
                return 4;
            case 22:
                return 1;
            case 23:
                return 8;
            case 24:
                return 8;
            default:
                return -1;
        }
    }

    private static int getNumberOfFields() {
        return 25;
    }

    public static String getLmsName() {
        return "LMS01";
    }

    public static ArrayList<Laegemiddel> read(String rootFolder) throws IOException {

        File f = new File(rootFolder + getLmsName().toLowerCase() + ".txt");

        ArrayList<Laegemiddel> list = new ArrayList<Laegemiddel>();
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

    public static Laegemiddel parse(String line) {
        Laegemiddel obj = new Laegemiddel();
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