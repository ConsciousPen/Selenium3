package aaa.utils.excel.bind.helper;

import java.lang.reflect.Field;
import java.util.List;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import aaa.utils.excel.bind.annotation.ExcelTableElement;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class TableClassHelper {
	private static final String HEADER_ROW_INDEX_METHOD_NAME = "headerRowIndex";
	private static final String IGNORE_CASE_METHOD_NAME = "ignoreCase";

	public static Field getPrimaryKeyField(Class<?> tableClass) {
		return getPrimaryKeyField(tableClass, BindHelper.getAllAccessibleFieldsFromThisAndSuperClasses(tableClass));
	}

	public static Field getPrimaryKeyField(Class<?> tableClass, List<Field> tableColumnsFields) {
		for (Field field : tableColumnsFields) {
			if (field.isAnnotationPresent(ExcelColumnElement.class) && field.getAnnotation(ExcelColumnElement.class).isPrimaryKey()) {
				return field;
			}
		}
		throw new IstfException(String.format("\"%s\" class does not have any primary key field", tableClass.getName()));
	}

	public static String getPrimaryKeyValue(Field primaryKeyField, TableRow tableRow) {
		return tableRow.getStringValue(ColumnFieldHelper.getHeaderColumnName(primaryKeyField));
	}

	public static int getHeaderRowIndex(Class<?> tableClass) {
		return tableClass.getAnnotation(ExcelTableElement.class).headerRowIndex();
	}

	public static String getSheetName(Class<?> tableClass) {
		return tableClass.getAnnotation(ExcelTableElement.class).sheetName();
	}

	public static boolean isCaseIgnored(Class<?> tableClass) {
		return tableClass.getAnnotation(ExcelTableElement.class).ignoreCase();
	}
}
