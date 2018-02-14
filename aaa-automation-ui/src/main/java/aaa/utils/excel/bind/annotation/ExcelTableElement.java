package aaa.utils.excel.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTableElement {
	/**
	 * -1 by default means search for first occurrence of header row on sheet which has all column names defined as class field names.
	 * In case of positive value, it will be used as table's header row index and all non empty cells within will be used as header column names to be binded to class field names.
	 * Rows indexes starts from 1.
	 */
	int headerRowIndex() default -1;

	/**
	 * Sheet name where header column name should be searched
	 */
	String sheetName();

	/**
	 * If true then ignore case while matching each class field name with header column name from excel file.
	 * <b>true</b> value on class level overrides all field's {@link ExcelTableColumnElement#ignoreCase()} values.
	 * Default value is false.
	 */
	boolean ignoreCase() default false;
}
