package aaa.utils.excel.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTableColumnElement {
	boolean isPrimaryKey() default false;

	String name() default "##default";

	String primaryKeysSeparator() default ",";
}
