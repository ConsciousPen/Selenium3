package aaa.utils.excel.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.LocalDateTime;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumnElement {
	String DEFAULT_FIELD_VALUE_MARK = "##default";
	/**
	 * Define whether field is related to table's primary key column. Values from primary key column is used as table rows filter for collecting List of objects with {@link ExcelTableElement} annotation
	 */
	boolean isPrimaryKey() default false;

	/**
	 * Header column name to be searched in excel table (ignoring case if {@link #ignoreCase} argument is set to true). By default class field name is used as header column name in table.
	 * If field has {@link java.util.List} type then values from all columns containing field name will be collected to the list of appropriate type.
	 * Ignored if {@link #containsName} argument is set.
	 */
	String name() default DEFAULT_FIELD_VALUE_MARK;

	/**
	 * Column name substring pattern to be searched in header column names (ignoring case if {@link #ignoreCase} argument is set to true).
	 * If field has {@link java.util.List} type then values from all columns containing column name pattern will be collected to the list of appropriate type.
	 */
	String containsName() default DEFAULT_FIELD_VALUE_MARK;

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
}
