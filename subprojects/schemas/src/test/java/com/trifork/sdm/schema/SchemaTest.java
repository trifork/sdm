package com.trifork.sdm.schema;


import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;


public class SchemaTest
{
	// private static String tmpDir;


	@BeforeClass
	public static void setup() throws IOException
	{
		/*
		File tmpDir = File.createTempFile("temp", Long.toString(System.nanoTime()));

		if (!(tmpDir.delete()))
		{
			throw new IOException("Could not delete temp file: " + tmpDir.getAbsolutePath());
		}

		if (!(tmpDir.mkdir()))
		{
			throw new IOException("Could not create temp directory: " + tmpDir.getAbsolutePath());
		}

		SchemaTest.tmpDir = tmpDir.getAbsolutePath();
		*/
	}


	@Test
	public void testGenerateValidSchemas() throws Exception
	{
		/*
		SchemaGenerator schemaGenerator = new SchemaGenerator(tmpDir, TestA.class.getPackage().getName());

		String path = schemaGenerator.target + "/testa_v1.xsd";

		schemaGenerator.execute();

		// Check that the generated schema is a valid XSD schema.

		SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
		File schemaLocation = new File(path);

		factory.newSchema(schemaLocation);
		*/
	}


	@Test//(expected = InvalidClassException.class)
	public void should_fail_element_name_are_equal_except_for_case() throws Exception
	{
		/*
		SchemaGenerator schemaGenerator = new SchemaGenerator(tmpDir, TestD.class.getPackage().getName());

		schemaGenerator.execute();
		*/
	}


	@Test
	public void testInvalidPropertyNamesGetPrefix()
	{
		/*
		 * SchemaGenerator schemaGenerator = new SchemaGenerator(tmpDir, TestC.class.getPackage()
		 * .getName());
		 * 
		 * try { schemaGenerator.execute(); fail(); } catch (InvalidClassException e) { // This is
		 * expected. } catch (Exception e) { fail(); e.printStackTrace(); }
		 */
	}


	@Test
	public void testElementTypeGeneration() throws Exception
	{

		/*
		 * SchemaGenerator schemaGenerator = new SchemaGenerator(tmpDir, TestA.class.getPackage()
		 * .getName());
		 * 
		 * schemaGenerator.execute();
		 * 
		 * String path = schemaGenerator.target + "/testa_v1.xsd";
		 * 
		 * DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 * 
		 * Document document = builder.parse(new FileInputStream(path));
		 * 
		 * XPath xpath = XPathFactory.newInstance().newXPath();
		 * 
		 * Node schemaNode = (Node) xpath.evaluate("/schema", document, XPathConstants.NODE);
		 * assertNotNull(schemaNode);
		 * 
		 * Node rootElement = (Node) xpath.evaluate("/schema/element", document,
		 * XPathConstants.NODE); assertNotNull(rootElement);
		 * 
		 * String rootElementName = (String) xpath.evaluate("/schema/element/@name", document,
		 * XPathConstants.STRING); assertEquals(TestA.class.getSimpleName().toLowerCase(),
		 * rootElementName);
		 * 
		 * Node typeElement = (Node) xpath.evaluate("/schema/element/complexType", document,
		 * XPathConstants.NODE); assertNotNull(typeElement);
		 * 
		 * Node sequenceElement = (Node) xpath.evaluate("/schema/element/complexType/all", document,
		 * XPathConstants.NODE); assertNotNull(sequenceElement);
		 * 
		 * int i = 1;
		 * 
		 * checkElementNameAndType(document, i++, "a", "sd:censoredString");
		 * checkElementNameAndType(document, i++, "b", "sd:censoredBoolean");
		 * checkElementNameAndType(document, i++, "c", "sd:censoredDate");
		 * checkElementNameAndType(document, i++, "d", "sd:censoredInteger");
		 * checkElementNameAndType(document, i++, "e", "sd:censoredInteger");
		 * 
		 * // Make sure that getF() and getG() are not included. // and that the validTo validFrom
		 * etc. are.
		 * 
		 * checkElementNameAndType(document, i++, "validFrom", "xs:dateTime");
		 * checkElementNameAndType(document, i++, "validTo", "xs:dateTime");
		 * 
		 * // TODO: These two might get included in the future. //checkElementNameAndType(document,
		 * i++, "createdDate", "xs:dateTime"); //checkElementNameAndType(document, i++,
		 * "modifiedDate", "xs:dateTime");
		 */
	}


	@SuppressWarnings("unused")
	private void checkElementNameAndType(Document document, int i, String expectedName, String expectedType) throws Exception
	{
		/*
		 * String query = "/schema/element/complexType/all/element[%d]/@%s";
		 * 
		 * XPath xpath = XPathFactory.newInstance().newXPath();
		 * 
		 * String name = (String) xpath.evaluate(String.format(query, i, "name"), document,
		 * XPathConstants.STRING); assertEquals(expectedName.toLowerCase(), name.toLowerCase());
		 * 
		 * String type = (String) xpath.evaluate(String.format(query, i, "type"), document,
		 * XPathConstants.STRING); assertEquals(expectedType.toLowerCase(), type.toLowerCase());
		 */
	}


	@Test
	public void testVersioning() throws Exception
	{
		/*
		 * SchemaGenerator schemaGenerator = new SchemaGenerator(tmpDir, TestB.class.getPackage()
		 * .getName());
		 * 
		 * schemaGenerator.execute();
		 * 
		 * XPath xpath = XPathFactory.newInstance().newXPath(); DocumentBuilder builder =
		 * DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 * 
		 * // Make sure that schema B2 is not created.
		 * 
		 * String pathB2 = schemaGenerator.target + "/testb_v2.xsd"; assertFalse(new
		 * File(pathB2).exists());
		 * 
		 * 
		 * // Make sure that v1 only includes properties for v1.
		 * 
		 * String pathB1 = schemaGenerator.target + "/testb_v1.xsd"; Document documentB1 =
		 * builder.parse(new FileInputStream(pathB1));
		 * 
		 * String query = "//element[@name='%s'][1]";
		 * 
		 * assertNotNull(xpath.evaluate(String.format(query, "a"), documentB1,
		 * XPathConstants.NODE)); assertNotNull(xpath.evaluate(String.format(query, "b"),
		 * documentB1, XPathConstants.NODE)); assertEquals(null, xpath.evaluate(String.format(query,
		 * "c"), documentB1, XPathConstants.NODE)); assertEquals(null,
		 * xpath.evaluate(String.format(query, "d"), documentB1, XPathConstants.NODE));
		 * 
		 * 
		 * // Make sure that v3 only includes properties for v3.
		 * 
		 * String pathB3 = schemaGenerator.target + "/testb_v3.xsd"; Document documentB3 =
		 * builder.parse(new FileInputStream(pathB3));
		 * 
		 * 
		 * assertEquals(null, xpath.evaluate(String.format(query, "a"), documentB3,
		 * XPathConstants.NODE)); assertEquals(null, xpath.evaluate(String.format(query, "b"),
		 * documentB3, XPathConstants.NODE)); assertNotNull(xpath.evaluate(String.format(query,
		 * "c"), documentB3, XPathConstants.NODE)); assertEquals(null,
		 * xpath.evaluate(String.format(query, "d"), documentB3, XPathConstants.NODE));
		 */
	}
}
