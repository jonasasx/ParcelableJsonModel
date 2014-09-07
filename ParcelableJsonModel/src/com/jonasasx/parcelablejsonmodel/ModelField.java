package com.jonasasx.parcelablejsonmodel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface ModelField.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelField {

	/**
	 * Json key.
	 *
	 * @return the string
	 */
	public String json() default "";
}