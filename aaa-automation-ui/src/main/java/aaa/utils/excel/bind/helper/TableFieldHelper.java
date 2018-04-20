package aaa.utils.excel.bind.helper;

import java.lang.reflect.Field;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.io.entity.area.table.TableRow;

public class TableFieldHelper {
	private static final String HEADER_ROW_INDEX_METHOD_NAME = "headerRowIndex";
	private static final String IGNORE_CASE_METHOD_NAME = "ignoreCase";

	public static String getPrimaryKeyValue(Field primaryKeyField, TableRow tableRow) {
		return tableRow.getStringValue(ColumnFieldHelper.getHeaderColumnName(primaryKeyField));
	}

	public static int getHeaderRowIndex(Field field) {
		if (field.isAnnotationPresent(ExcelTableElement.class)) {
			return field.getAnnotation(ExcelTableElement.class).headerRowIndex();
		}
		return (int) BindHelper.getAnnotationDefaultValue(ExcelTableElement.class, HEADER_ROW_INDEX_METHOD_NAME);
	}

	public static String getSheetName(Field field) {
		return field.isAnnotationPresent(ExcelTableElement.class) ? field.getAnnotation(ExcelTableElement.class).sheetName() : field.getName();
	}

	public static boolean isCaseIgnored(Field field) {
		if (field.isAnnotationPresent(ExcelTableElement.class)) {
			return field.getAnnotation(ExcelTableElement.class).ignoreCase();
		}
		return (boolean) BindHelper.getAnnotationDefaultValue(ExcelTableElement.class, IGNORE_CASE_METHOD_NAME);
	}
}
