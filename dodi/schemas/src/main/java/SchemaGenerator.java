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
import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import com.trifork.stamdata.views.Views;

/**
 * App that generates XSD schemas for the views.
 *
 * @author Thomas Børlum (thb@trifork.com)
 */
public class SchemaGenerator {

	public static void main(String[] args) throws JAXBException, IOException {

		if (args.length != 1) {
			printUsage();
			return;
		}

		final File destinationDir = new File(args[0]);
		destinationDir.mkdirs();

		class MySchemaOutputResolver extends SchemaOutputResolver {

			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {

				String name = namespaceUri.substring(namespaceUri.lastIndexOf("/") + 1) + ".xsd";

				return new StreamResult(new File(destinationDir, name));
			}
		}

		Class<?>[] views = Views.findAllViews().toArray(new Class[0]);

		JAXBContext context = JAXBContext.newInstance(views);
		context.generateSchema(new MySchemaOutputResolver());
	}

	private static void printUsage() {
		System.out.println("Program usage:");
		System.out.println("<destination directory>");
	}
}
