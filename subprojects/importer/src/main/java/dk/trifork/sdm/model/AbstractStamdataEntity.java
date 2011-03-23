package dk.trifork.sdm.model;

import dk.trifork.sdm.util.DateUtils;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.*;


public abstract class AbstractStamdataEntity implements StamdataEntity {

	public static final Calendar FUTURE; // TODO: Use the FUTURE in DateUtils.
	private static final Logger logger = Logger.getLogger(AbstractStamdataEntity.class);
	private static final Map<Class<? extends StamdataEntity>, Method> idMethodCache = new HashMap<Class<? extends StamdataEntity>, Method>();
	private static final Map<Method, String> outputFieldNames = new HashMap<Method, String>();

	static {
		FUTURE = Calendar.getInstance();
		FUTURE.clear();
		FUTURE.set(2999, 11, 31);
	}

	@Override
	public Object getKey() {

		Method idMethod = getIdMethod(getClass());
		try {
			return idMethod.invoke(this);
		}
		catch (Exception e) {
			logger.error("Error getting id for object of class: " + getClass());
			return null;
		}
	}

	/**
	 * 
	 * @param class1 . A type of StamdataEntity
	 * @return the getter method that contains the unique id for the given
	 *         StamdataEntity type
	 */
	public static Method getIdMethod(Class<? extends StamdataEntity> class1) {

		Method m = idMethodCache.get(class1);
		if (m != null) return m;
		Method[] allMethods = class1.getMethods();
		for (Method method : allMethods) {
			if (method.isAnnotationPresent(Id.class)) {
				idMethodCache.put(class1, method);
				return method;
			}
		}
		// TODO: This should be an precondition exception.
		logger.error("Could not find idmethod for class: " + class1 + " A getter must be annotated with @Id!");
		return null;
	}

	/**
	 * 
	 * @param method A getter method, that is used for serialization.
	 * @return The name used to designate this field when serializing
	 */
	public static String getOutputFieldName(Method method) {

		String name = outputFieldNames.get(method);
		if (name == null) {
			Output output = method.getAnnotation(Output.class);
			name = method.getName().substring(3); // Strip "get"
			if (output != null && output.name().length() > 0) {
				name = output.name();
			}
			outputFieldNames.put(method, name);
		}
		return name;
	}

	public static List<Method> getOutputMethods(Class<? extends StamdataEntity> type) {

		Method[] methods = type.getMethods();
		List<Method> outputMethods = new ArrayList<Method>();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Output.class)) outputMethods.add(method);
		}
		return outputMethods;
	}

	@Override
	public Calendar getValidTo() {

		return DateUtils.FUTURE;
	}
}
