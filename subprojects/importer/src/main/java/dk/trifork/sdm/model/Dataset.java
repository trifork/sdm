package dk.trifork.sdm.model;

import static dk.trifork.sdm.model.AbstractStamdataEntity.getIdMethod;
import static dk.trifork.sdm.model.AbstractStamdataEntity.getOutputFieldName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Dataset<T extends StamdataEntity> {

	private Map<Object, List<T>> entities = new HashMap<Object, List<T>>();
	private Class<T> type;

	public Dataset(List<T> entities, Class<T> type) {

		this.type = type;

		for (T entity : entities) {
			List<T> ents = new ArrayList<T>();
			ents.add(entity);
			this.entities.put(entity.getKey(), ents);
		}
	}

	public int size() {

		return getEntities().size();
	}

	public Dataset(Class<T> type) {

		this.type = type;
	}

	public Collection<T> getEntities() {

		Collection<T> allEnts = new ArrayList<T>();
		for (List<T> ents : entities.values()) {
			allEnts.addAll(ents);
		}
		return allEnts;
	}

	public T getEntityById(Object id) {

		List<T> ents = entities.get(id);
		if (ents == null) return null;
		if (ents.size() == 1) return ents.get(0);
		throw new RuntimeException("Multiple entities exist with entityid " + id);
	}

	public List<T> getEntitiesById(Object id) {

		return entities.get(id);
	}

	public Class<T> getType() {

		return type;
	}

	/**
	 * @return the name that this entity type (class) should be displayed with
	 *         when output
	 */
	public String getEntityTypeDisplayName() {

		Output output = type.getAnnotation(Output.class);
		if (output != null && !"".equals(output.name())) return output.name();
		return type.getSimpleName();
	}

	public static String getEntityTypeDisplayName(Class<? extends StamdataEntity> type) {

		Output output = type.getAnnotation(Output.class);
		if (output != null && !"".equals(output.name())) return output.name();
		return type.getSimpleName();
	}

	public void removeEntities(List<T> entities) {

		for (T entity : entities) {
			this.entities.remove(entity.getKey());
		}
	}

	public void addEntity(T entity) {

		Object id = entity.getKey();
		List<T> ents = entities.get(id);
		if (ents == null) {
			ents = new ArrayList<T>();
			entities.put(id, ents);
		}
		ents.add(entity);
	}

	public static String getIdOutputName(Class<? extends StamdataEntity> clazz) {

		return getOutputFieldName(getIdMethod(clazz));
	}
}
