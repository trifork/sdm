import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import records.Records;

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

		Set<Class<?>> unmodifiableClasses = Views.findAllViews();
		Set<Class<?>> classes = new HashSet<Class<?>>(unmodifiableClasses);
		classes.add(Records.class);
		Class<?>[] views = classes.toArray(new Class[0]);

		JAXBContext context = JAXBContext.newInstance(views);
		context.generateSchema(new MySchemaOutputResolver());
	}

	private static void printUsage() {
		System.out.println("Program usage:");
		System.out.println("<destination directory>");
	}
}
