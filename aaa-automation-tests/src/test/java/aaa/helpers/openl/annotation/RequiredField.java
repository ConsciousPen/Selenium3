package aaa.helpers.openl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

/**
 * Should be used only for fields of classes with {@link ExcelTableElement} annotation as a marker for algorithm implemented in<p>
 * {@link OpenLPolicy#isProper()} method which verifies that values of annotatted fields are not null (and list is not empty if it's collection)
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredField {
}
