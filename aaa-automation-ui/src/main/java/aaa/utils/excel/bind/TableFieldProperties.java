package aaa.utils.excel.bind;

import java.lang.reflect.Field;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.io.entity.area.table.TableRow;

public class TableFieldProperties {
	private static final String HEADER_ROW_INDEX_METHOD_NAME = "headerRowIndex";
	private static final String IGNORE_CASE_METHOD_NAME = "ignoreCase";

	public static String getPrimaryKeyValue(Field primaryKeyField, TableRow tableRow) {
		return tableRow.getStringValue(TableColumnFieldProperties.getHeaderColumnName(primaryKeyField));
	}

	public static int getHeaderRowIndex(Field tableField) {
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			return tableField.getAnnotation(ExcelTableElement.class).headerRowIndex();
		}
		return (int) BindHelper.getAnnotationDefaultValue(ExcelTableElement.class, HEADER_ROW_INDEX_METHOD_NAME);
	}

	public static String getSheetName(Field tableField) {
		return tableField.isAnnotationPresent(ExcelTableElement.class) ? tableField.getAnnotation(ExcelTableElement.class).sheetName() : tableField.getName();
	}

	public static boolean isCaseIgnored(Field tableField) {
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			return tableField.getAnnotation(ExcelTableElement.class).ignoreCase();
		}
		return (boolean) BindHelper.getAnnotationDefaultValue(ExcelTableElement.class, IGNORE_CASE_METHOD_NAME);
	}
}
