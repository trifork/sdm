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
package dk.nsi.stamdata.replication.tools;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.trifork.stamdata.Namespace;
import com.trifork.stamdata.Nullable;
import dk.nsi.stamdata.replication.dao.DynamicViewDAO;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class DynamicSchemaGenerator {

    public static void main(String[] args) throws SQLException, IOException {
        if (args.length != 2) {
            usage();
            return;
        }
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String dbConnectionString = args[0];
        File outputDir = new File(args[1]);
        System.out.println("Generating schemas from " + dbConnectionString + " to directory " + outputDir);
        Injector injector = Guice.createInjector(new GeneratorModule(dbConnectionString));
        DynamicViewDAO dynamicViewDAO = injector.getInstance(DynamicViewDAO.class);
        List<String> views = dynamicViewDAO.listAllViews();
        for (String view : views) {
            generateSchemaForView(dynamicViewDAO, outputDir, view);
        }
    }

    private static void generateSchemaForView(DynamicViewDAO dao, File outputDir, String view) throws IOException, SQLException {

        String[] split = StringUtils.split(view, "/");
        String register = split[0];
        String datatype = split[1];
        String versionString = split[2];
        Integer version = Integer.parseInt(versionString.substring(1));

        FileWriter writer = createWriterFor(outputDir, register, datatype, version);
        ViewMapVO viewMap = dao.getViewMapForView(register, datatype, version);
        generate(viewMap, writer);

        writer.flush();
        writer.close();
    }

    private static void generate(ViewMapVO viewMap, FileWriter writer) throws IOException {
        System.out.println(viewMap.toString());
        Document doc = DocumentFactory.getInstance().createDocument();
        String namespace = Namespace.STAMDATA_3_0 + "/" + viewMap.getRegister();
        String entityName = viewMap.getDatatype();

        Element all = generate(doc, namespace, entityName);
        List<ColumnMapVO> columnMaps = viewMap.getColumnMaps();
        for (ColumnMapVO column : columnMaps) {
            String columnFeedName = column.getFeedColumnName();
            if (columnFeedName != null) {
                addElement(all, columnFeedName, convert2XsdType(column.getDataType()), column.getMaxLength());
            }
        }

        XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
        xmlWriter.write(doc);
    }

    private static String convert2XsdType(int dataType) {
        switch (dataType) {
            case Types.BIGINT:
            case Types.INTEGER:
                return "xs:integer";
            case Types.VARCHAR:
                return "xs:string";
            case Types.BOOLEAN:
                return "xs:boolean";
            case Types.DOUBLE:
                return "xs:double";
            case Types.FLOAT:
                return "xs:float";
            case Types.DECIMAL:
                return "xs:decimal";
            case Types.DATE:
                return "xs:date";
            case Types.TIMESTAMP:
                return "xs:dateTime";
            default:
                throw new RuntimeException("Datatype not implemented in DynamicSchemaGenerator: " + dataType);
        }
    }

    private static Element generate(Document doc, String namespace, String entityName)
    {
        Element root = doc.addElement("xs:schema");

        root.addNamespace("xs", "http://www.w3.org/2001/XMLSchema");
        root.addNamespace("tns", namespace);
        root.addAttribute("targetNamespace", namespace);

        Element element = root.addElement("xs:element");
        element.addAttribute("name", entityName);

        Element complexType = element.addElement("xs:complexType");

        // We use "all" and not "sequence" we cause we cannot tell from the
        // class the order of the elements.
        //
        return complexType.addElement("xs:all");
    }

    private static FileWriter createWriterFor(File outputDir, String register, String datatype, Integer version) throws IOException
    {
        File registerDir = new File(outputDir, register);
        registerDir.mkdirs();

        File schemaFile = new File(registerDir, datatype + "_v" + version + ".xsd");
        return new FileWriter(schemaFile);
    }

    private static void addElement(Element parent, String name, String type, @Nullable Integer length)
    {
        Element field = parent.addElement("xs:element");
        field.addAttribute("name", name);

        if (length != null && type.equals("xs:string"))
        {
            field.addElement("xs:simpleType").addElement("xs:restriction").addElement("xs:maxLength").addAttribute("value", length.toString());
        }
        else
        {
            field.addAttribute("type", type);
        }
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("jdbc:///database outputdirectory/");
    }

    private static class GeneratorModule extends AbstractModule {

        private final String jdbcUrl;

        public GeneratorModule(String jdbcUrl) {
            this.jdbcUrl = jdbcUrl;
        }

        @Override
        protected void configure() {
        }

        @Provides
        protected Connection provideConnection() {
            try {
                return DriverManager.getConnection(jdbcUrl);
            } catch (SQLException e) {
                throw  new RuntimeException("Cannot get connection", e);
            }
        }
    }

}
