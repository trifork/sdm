package dk.trifork.sdm.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to designate that a field is the id. For a point in validtime, each id must be unique. 
 * But an entity can keep its id across changes, so there can be more database records with the same id
 * representing the entity at different times.
 *
 * @author rsl
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
}
