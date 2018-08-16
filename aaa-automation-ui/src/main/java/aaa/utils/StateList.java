package aaa.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation @StateList is applicable for class and for test method levels. If annotation is set on class level  - it applies to all test methods in class. Test method level annotation applies only it.
 * Test method level annotation has Higher priority than class level – this mean, if there are annotations on both levels, test method level will be picked up.
 *
 * Annotation has 2 flags: states and statesExcept. Just one of them should be used, it is not allowed to use both – it simply doesn’t make sense.
 * states – list of states applicable to test
 * statesExcept – List of states NOT applicable to test.
 * If test is applicable to all states – just do not use this annotation.
 *
 * **/
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
