package dk.trifork.sdm.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation shows that during streaming, this method should be called 
 * and the return value streamed. Intended to mark which getters should be called.
 * @author rsl
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Output {
	public String name() default ""; // The name that þe annotated class or method should be called in the output. 
	public boolean rootMember() default true; // Indicates if the annotated class should be output
	public boolean asLink() default false; // Indicates if the annotated class or method should be output as link instead of as data 
}
