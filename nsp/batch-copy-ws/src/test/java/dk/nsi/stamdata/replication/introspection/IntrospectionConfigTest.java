package dk.nsi.stamdata.replication.introspection;

import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import static junit.framework.Assert.assertEquals;

public class IntrospectionConfigTest {

    private final static String TESTFILE_NAME = "classpath:test_table_map.json";

    @Test
    public void testLoadingFileFromClassPath() {
        IntrospectionConfig introspectionConfig = new IntrospectionConfig(TESTFILE_NAME);
    }

    @Test
    public void testLoadingFile() throws IOException {
        File file = copyToTempFile();
        String path = "file:" + file.getAbsolutePath();
        IntrospectionConfig introspectionConfig = new IntrospectionConfig(path);
        file.delete();

        Set<IntrospectionConfig.RegisterConfig> registers = introspectionConfig.getRegisters();
        assertEquals(2, registers.size());

        Iterator<IntrospectionConfig.RegisterConfig> iterator = registers.iterator();
        int cnt = 0;
        while (iterator.hasNext()) {
            IntrospectionConfig.RegisterConfig config = iterator.next();
            if (config.name.equals("sikrede")) {
                assertEquals("Sikrede", config.dataTypeTableMapping.get("sikrede"));
            } else {
                assertEquals("VitaminGrunddata", config.dataTypeTableMapping.get("grunddata"));
                assertEquals("VitaminFirmadata", config.dataTypeTableMapping.get("firmadata"));
                assertEquals("VitaminUdgaaedeNavne", config.dataTypeTableMapping.get("udgaaedenavne"));
                assertEquals("VitaminIndholdsstoffer", config.dataTypeTableMapping.get("indholdsstoffer"));
            }
        }
    }

    private File copyToTempFile() throws IOException {
        File tempFile = File.createTempFile("test_table_map_file", ".json");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile));
        InputStream stream = IntrospectionConfigTest.class.getClassLoader()
                .getResourceAsStream(TESTFILE_NAME.substring("classpath:".length()));
        byte data[] = new byte[stream.available()];
        stream.read(data);
        char cdata[] = new char[data.length];
        for (int i=0; i<data.length; i++) {
            cdata[i] = (char)data[i];
        }
        bw.write(cdata);
        stream.close();
        bw.close();
        return tempFile;
    }

}
