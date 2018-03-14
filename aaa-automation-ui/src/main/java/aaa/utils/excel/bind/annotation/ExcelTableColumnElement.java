package aaa.utils.excel.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTableColumnElement {
	/**
	 * Define whether field is related to table's primary key column. Values from primary key column is used as table rows filter for collecting List of objects with {@link ExcelTableElement} annotation
	 */
	boolean isPrimaryKey() default false;

	/**
	 * Header column name to be searched in excel table. By default class field name is used as header column name in table.
	 */
	String name() default "##default";

	/**
	 * If true then ignore case while matching field name with header column name from excel file.
	 * Default value is false.
	 */
	boolean ignoreCase() default false;

	/**
	 * Primary key separator for multiple key values. "," is default separator
	 */
	String primaryKeysSeparator() default ",";

	/**
	 * Date format patterns array for {@link LocalDateTime} field types used for parsing cells with date values
	 */
	String[] dateFormatPatterns() default {};

	//to be continued...
	//String listByContains() default "##*";
}
