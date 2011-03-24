import java.io.File;
import java.io.IOException;

import javax.persistence.Entity;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.trifork.stamdata.replication.replication.views.View;


public class SchemaGenerator {

	public static void main(String[] args) throws JAXBException, IOException {

		final File baseDir = new File(".");

		class MySchemaOutputResolver extends SchemaOutputResolver {

			public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
				return new StreamResult(new File(baseDir, suggestedFileName));
			}
		}

		String MODEL_PACKAGE = View.class.getPackage().getName();
		Reflections reflector = new Reflections(new ConfigurationBuilder().filterInputsBy(new FilterBuilder.Include(FilterBuilder.prefix(MODEL_PACKAGE))).setUrls(ClasspathHelper.getUrlsForPackagePrefix(MODEL_PACKAGE)).setScanners(new TypeAnnotationsScanner()));
		Class<?>[] classes = reflector.getTypesAnnotatedWith(Entity.class).toArray(new Class[0]);

		JAXBContext context = JAXBContext.newInstance(classes);
		context.generateSchema(new MySchemaOutputResolver());
	}
}
