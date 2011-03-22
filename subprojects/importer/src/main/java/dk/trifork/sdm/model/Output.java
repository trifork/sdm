package dk.trifork.sdm.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This annotation shows that during streaming, this method should be called 
 * and the return value streamed. Intended to mark which getters should be called.
 * 
 * @author Rune Skou Larsen <rsl@trifork.com>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Output {
	
	/**
	 * The name that þe annotated class or method should be called in the output.
	 */
	public String name() default "";
}
