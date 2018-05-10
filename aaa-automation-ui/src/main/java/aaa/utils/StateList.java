package aaa.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface StateList {

	/**
	 * Specifies states list applicable to the test script.
	 * @return array of states.
	 */
	String[] states() default {};

	/**
	 * Specifies states list NOT applicable to the test script.
	 * @return array of states.
	 */
	String[] statesExcept() default {};

}
