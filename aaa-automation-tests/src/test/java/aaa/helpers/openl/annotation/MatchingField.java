package aaa.helpers.openl.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import aaa.helpers.openl.model.OpenLPolicy;
import aaa.utils.excel.bind.annotation.ExcelTableElement;

/**
 * Should be used only for fields of classes with {@link ExcelTableElement} annotation as a marker for algorithm implemented in<p>
 * {@link OpenLPolicy#sortInnerListsRecursively(OpenLPolicy)} method which sorts inner lists of OpenL policy objects according to the values of fields with this annotation.<p>
 * See examples in {@link OpenLPolicy#sortInnerListsRecursively(OpenLPolicy)} method.
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MatchingField {
}
