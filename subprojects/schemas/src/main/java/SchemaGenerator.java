import java.io.File;
import java.io.IOException;

import javax.persistence.Entity;
import javax.xml.bind.*;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.*;

import com.trifork.stamdata.replication.replication.models.Record;


public class SchemaGenerator {

	public static void main(String[] args) throws JAXBException, IOException {

		final File baseDir = new File(".");

		class MySchemaOutputResolver extends SchemaOutputResolver {

			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				return new StreamResult(new File(baseDir, suggestedFileName));
			}
		}

		String MODEL_PACKAGE = Record.class.getPackage().getName();
		Reflections reflector = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(MODEL_PACKAGE))).setUrls(ClasspathHelper.getUrlsForPackagePrefix(MODEL_PACKAGE)).setScanners(new TypeAnnotationsScanner()));
		Class<?>[] classes = reflector.getTypesAnnotatedWith(Entity.class).toArray(new Class[0]);

		JAXBContext context = JAXBContext.newInstance(classes);
		context.generateSchema(new MySchemaOutputResolver());
	}
}
