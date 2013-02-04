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

import static com.trifork.stamdata.Preconditions.checkArgument;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.bind.annotation.XmlTransient;

import com.trifork.stamdata.specs.TilskudsblanketRecordSpecs;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.trifork.stamdata.Namespace;
import com.trifork.stamdata.Nullable;
import com.trifork.stamdata.persistence.RecordSpecification;
import com.trifork.stamdata.specs.SikredeRecordSpecs;
import com.trifork.stamdata.specs.YderregisterRecordSpecs;

import dk.nsi.stamdata.views.View;
import dk.nsi.stamdata.views.ViewModule;

public class SchemaGenerator
{
    private static final Key<Map<String, Class<? extends View>>> view = Key.get(new TypeLiteral<Map<String, Class<? extends View>>>() {});

    public static void main(String[] args) throws IOException
    {
        File outputDir = new File(args[0]);

        Collection<Class<? extends View>> views = Guice.createInjector(new ViewModule()).getInstance(view).values();

        for (Class<? extends View> view : views)
        {
            String packageName = view.getPackage().getName();
            String subDirName = packageName.substring(packageName.lastIndexOf(".") + 1);
            String viewName = view.getSimpleName().toLowerCase();

            Writer writer = createWriterFor(outputDir, subDirName, viewName);

            generate(view, writer);

            writer.flush();
            writer.close();
        }

        // HACK: Make generic for all record specs.

        generateRecordXsd(outputDir, "sikrede", "sikrede", SikredeRecordSpecs.ENTRY_RECORD_SPEC);
        generateRecordXsd(outputDir, "yderregisteret", "yder", YderregisterRecordSpecs.YDER_RECORD_TYPE);
        generateRecordXsd(outputDir, "yderregisteret", "person", YderregisterRecordSpecs.PERSON_RECORD_TYPE);

        generateRecordXsd(outputDir, "tilskudsblanket", "blanket", TilskudsblanketRecordSpecs.BLANKET_RECORD_SPEC);
        generateRecordXsd(outputDir, "tilskudsblanket", "blanketenkelt", TilskudsblanketRecordSpecs.BLANKET_ENKELTTILSKUD_RECORD_SPEC);
        generateRecordXsd(outputDir, "tilskudsblanket", "blanketforhoejet", TilskudsblanketRecordSpecs.BLANKET_FORHOJETTILSKUD_RECORD_SPEC);
        generateRecordXsd(outputDir, "tilskudsblanket", "blanketkroniker", TilskudsblanketRecordSpecs.BLANKET_KRONIKERTILSKUD_RECORD_SPEC);
        generateRecordXsd(outputDir, "tilskudsblanket", "forhoejettakst", TilskudsblanketRecordSpecs.FORHOEJETTAKST_RECORD_SPEC);
        generateRecordXsd(outputDir, "tilskudsblanket", "blanketterminal", TilskudsblanketRecordSpecs.BLANKET_TERMINALTILSKUD_RECORD_SPEC);
    }
    
    private static void generateRecordXsd(File outputDir, String register, String entityName, RecordSpecification spec) throws IOException
    {
        Writer writer = createWriterFor(outputDir, register, entityName);

        generate(spec, writer, register);

        writer.flush();
        writer.close();
    }
    
    private static FileWriter createWriterFor(File outputDir, String register, String entityName) throws IOException
    {
        File registerDir = new File(outputDir, register);
        registerDir.mkdirs();

        File schemaFile = new File(registerDir, entityName + ".xsd");
        return new FileWriter(schemaFile);
    }
    
    public static void generate(Class<? extends View> entity, Writer writer) throws IOException
    {
        Document doc = DocumentFactory.getInstance().createDocument();

        String targetNamespace = entity.getPackage().getAnnotation(XmlSchema.class).namespace();
        String entityName = entity.getSimpleName().toLowerCase();
        
        Element all = generate(doc, targetNamespace, entityName);

        for (Field method : entity.getDeclaredFields())
        {
            if (method.isAnnotationPresent(XmlTransient.class)) continue;

            String name = method.getName();
            String type = convert2SchemaType(method);
            addElement(all, name, type, null);
        }

        XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
        xmlWriter.write(doc);
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
    
    private static String convert2SchemaType(Field field)
    {
        if (String.class.isAssignableFrom(field.getType()))
        {
            return "xs:string";
        }
        else if (Long.class.isAssignableFrom(field.getType()) || BigInteger.class.isAssignableFrom(field.getType()) || long.class.isAssignableFrom(field.getType()) || int.class.isAssignableFrom(field.getType()))
        {
            return "xs:integer";
        }
        else if (Date.class.isAssignableFrom(field.getType()))
        {
            return "xs:dateTime";
        }
        else if (Float.class.isAssignableFrom(field.getType()))
        {
            return "xs:float";
        }
        else if (Double.class.isAssignableFrom(field.getType()))
        {
            return "xs:double";
        }
        else if (Boolean.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType()))
        {
            return "xs:boolean";
        }
        else
        {
            checkArgument(false, "Return type '%s' on field %s is not supported.", field.getType(), field.toString());
            return null;
        }
    }
    
    public static void generate(RecordSpecification specification, Writer writer, String register) throws IOException
    {
        Document doc = DocumentFactory.getInstance().createDocument();

        // FIXME: Register added to record spec.

        String namespace = Namespace.STAMDATA_3_0 + "/" + register;
        String entityName = specification.getTable().toLowerCase();

        Element all = generate(doc, namespace, entityName);
        
        for (RecordSpecification.FieldSpecification field : specification.getFieldSpecs())
        {
            addElement(all, field.name, convert2XsdType(field.type), field.length);
        }
        all.addElement("xs:element").addAttribute("name", "ModifiedDate").addAttribute("type", "xs:dateTime");
        all.addElement("xs:element").addAttribute("name", "ValidFrom").addAttribute("type", "xs:dateTime");
        all.addElement("xs:element").addAttribute("name", "ValidTo").addAttribute("type", "xs:dateTime");

        XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
        xmlWriter.write(doc);
    }
    
    private static String convert2XsdType(RecordSpecification.RecordFieldType fieldType)
    {
        if (fieldType == RecordSpecification.RecordFieldType.ALPHANUMERICAL)
        {
            return "xs:string";
        }
        else if (fieldType == RecordSpecification.RecordFieldType.NUMERICAL)
        {
            return "xs:integer";
        }
        else
        {
            throw new AssertionError();
        }
    }
}
