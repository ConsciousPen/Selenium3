package aaa.utils.excel.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import aaa.utils.excel.bind.ExcelUnmarshaller;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTableElement {
	/**
	 * Positive value will be used as table's header row index. After finding this row if {@code strictMatch} argument is true in {@link ExcelUnmarshaller} unmarshaller methods
	 * then only class field names will be used as header column names, otherwise ({@code strictMatch} is false) - all non empty cells within found excel row will be used as header column names.
	 * Negative value means search for first occurrence of header row on sheet which has all column names defined as class field names.
	 * Default value is "-1", rows indexes starts from 1.
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
