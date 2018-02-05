package aaa.utils.excel.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelTableElement {
	/**
	 * -1 by default means search header row in sheet which has all column names defined as class member names.
	 * In case of positive value, it will be used as table's header row number and all non empty cells within will be used as header column names.
	 * First row on sheet starts from 1 index.
	 */
	int headerRowNumber() default -1;

	/**
	 * Sheet name where header column name should be searched
	 */
	String sheetName();

	/**
	 * If {@link #headerRowNumber()} is negative, this argument means return lowest or highest table on sheet in case if header row search returns multiple results
	 */
	boolean isLowest() default false;
}
