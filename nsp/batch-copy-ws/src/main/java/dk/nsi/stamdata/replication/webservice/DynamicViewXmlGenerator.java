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
package dk.nsi.stamdata.replication.webservice;

import com.trifork.stamdata.Nullable;
import dk.nsi.stamdata.replication.dynamic.DynamicRow;
import dk.nsi.stamdata.replication.exceptions.UnsupportedColumnType;
import dk.nsi.stamdata.replication.vo.ColumnMapVO;
import dk.nsi.stamdata.replication.vo.ViewMapVO;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Map;

public class DynamicViewXmlGenerator {
    private static final DateTime END_OF_TIME = new DateTime(2999, 12, 31, 0, 0, 0, DateTimeZone.UTC);
    private static final DateTimeFormatter formatterDateTime = ISODateTimeFormat.dateTime();
    private static final DateTimeFormatter formatterDate = DateTimeFormat.forPattern("yyyy-MM-dd");

    public static final String ATOM_NAMESPACE_URI = "http://www.w3.org/2005/Atom";
    public static final String STAMDATA_NAMESPACE_URI_PREFIX = "http://nsi.dk/-/stamdata/3.0/";

    public org.w3c.dom.Document generateXml(ViewMapVO view, List<DynamicRow> rows, String register,
                                            String datatype, DateTime updated)
            throws TransformerException {

        List<ColumnMapVO> columnMaps = view.getColumnMaps();
        String stamdataNamespaceUri = STAMDATA_NAMESPACE_URI_PREFIX + register;

        Document document = DocumentHelper.createDocument();
        document.setXMLEncoding("utf-8");

        Element root = document.addElement("atom:feed", ATOM_NAMESPACE_URI);

        addElement(root, ATOM_NAMESPACE_URI, "atom:id", String.format("tag:nsi.dk,2011:%s/%s/v1", register, datatype));
        addElement(root, ATOM_NAMESPACE_URI, "atom:updated", AtomDate.toString(updated.toDate()));
        addElement(root, ATOM_NAMESPACE_URI, "atom:title", "Stamdata Registry Feed");
        Element author = addElement(root, ATOM_NAMESPACE_URI, "atom:author", null);
        addElement(author, ATOM_NAMESPACE_URI, "atom:name", "National Sundheds IT");

        for (DynamicRow row : rows) {
            String revisionId = String.format("%010d%010d", row.getModifiedDate().getTime() / 1000, row.getPid());
            Element entry = addElement(root, ATOM_NAMESPACE_URI, "atom:entry", null);
            String atomId = String.format("tag:nsi.dk,2011:%s/%s/v1/%s", register, datatype, revisionId);
            addElement(entry, ATOM_NAMESPACE_URI, "atom:id", atomId);
            addElement(entry, ATOM_NAMESPACE_URI, "atom:title", null);

            addElement(entry, ATOM_NAMESPACE_URI, "atom:updated", AtomDate.toString(row.getModifiedDate()));

            Element content = addElement(entry, ATOM_NAMESPACE_URI, "atom:content", null);
            content.addAttribute("type", "application/xml");

            Element rowElement = addElement(content, stamdataNamespaceUri, datatype, null);
            for (ColumnMapVO columnMapVO : columnMaps) {
                if (!columnMapVO.isPid() && columnMapVO.getFeedColumnName() != null) {
                    addColumnAsElement(rowElement, stamdataNamespaceUri, columnMapVO, row.getContent());
                }
            }
        }

        return convertToW3C(document);
    }

    private void addColumnAsElement(Element parent, String namespace, ColumnMapVO columnMapVO,
                                    Map<String, Object> content) {
        Object columnContent = content.get(columnMapVO.getTableColumnName());
        String convertedContent = null;
        // null is used as end of time in some importers
        // so we need to take care of that special case
        if (columnContent == null && columnMapVO.getFeedColumnName().equalsIgnoreCase("ValidTo")) {
            columnContent = new Timestamp(END_OF_TIME.getMillis());
        }
        if (columnContent != null) {
            switch (columnMapVO.getDataType()) {
                case Types.BIGINT:
                    if (columnContent.getClass() == BigInteger.class) {
                        BigInteger bigIntegerValue = (BigInteger) columnContent;
                        convertedContent = bigIntegerValue.toString();
                    } else if (columnContent.getClass() == Long.class) {
                        convertedContent = Long.toString((Long) columnContent);
                    }
                    break;
                case Types.TIMESTAMP:
                    convertedContent = TimestampToString((Timestamp) columnContent);
                    break;
                case Types.BOOLEAN:
                    Boolean b = (Boolean) columnContent;
                    if (b) {
                        convertedContent = "1";
                    } else {
                        convertedContent = "0";
                    }
                    break;
                case Types.INTEGER:
                    convertedContent = Integer.toString((Integer) columnContent);
                    break;
                case Types.DATE:
                    Date d = (Date) columnContent;
                    convertedContent = formatterDate.print(d.getTime());
                    break;
                case Types.VARCHAR:
                    convertedContent = (String) columnContent;
                    break;
                default:
                    throw new UnsupportedColumnType(columnMapVO.getDataType());
            }
        }
        addElement(parent, namespace, columnMapVO.getFeedColumnName(), convertedContent);
    }

    private String TimestampToString(Timestamp timeStamp) {
        return formatterDateTime.print(timeStamp.getTime());
    }

    private Element addElement(Element parent, String namespace, String tagName, @Nullable String value)
    {
        Element element = parent.addElement(tagName, namespace);
        if(value != null) {
            element.setText(value);
        }
        return element;
    }

    private static final TransformerFactory TRANSFORMER_FACTORY = TransformerFactory.newInstance();

    public static org.w3c.dom.Document convertToW3C(org.dom4j.Document dom4jdoc) throws TransformerException
    {
        SAXSource source = new DocumentSource(dom4jdoc);
        DOMResult result = new DOMResult();

        Transformer transformer = TRANSFORMER_FACTORY.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(source, result);
        return (org.w3c.dom.Document) result.getNode();
    }

}
