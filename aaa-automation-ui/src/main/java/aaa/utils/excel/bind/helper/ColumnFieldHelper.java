package aaa.utils.excel.bind.helper;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import aaa.utils.excel.bind.annotation.ExcelTableColumnElement;
import toolkit.exceptions.IstfException;

public class ColumnFieldHelper {
	private static final String COLUMN_NAME_METHOD_NAME = "name";
	private static final String IGNORE_CASE_METHOD_NAME = "ignoreCase";
	private static final String PRIMARY_KEY_SSEPARATOR_METHOD_NAME = "primaryKeysSeparator";

	public static String getPrimaryKeysSeparator(Field primaryKeyField) {
		if (primaryKeyField.isAnnotationPresent(ExcelTableColumnElement.class)) {
			return primaryKeyField.getAnnotation(ExcelTableColumnElement.class).primaryKeysSeparator();
		}
		return (String) BindHelper.getAnnotationDefaultValue(ExcelTableColumnElement.class, PRIMARY_KEY_SSEPARATOR_METHOD_NAME);
	}

	public static String getHeaderColumnName(Field field) {
		String defaultNameMarker = (String) BindHelper.getAnnotationDefaultValue(ExcelTableColumnElement.class, COLUMN_NAME_METHOD_NAME);
		if (field.isAnnotationPresent(ExcelTableColumnElement.class) && !field.getAnnotation(ExcelTableColumnElement.class).name().equals(defaultNameMarker)) {
			return field.getAnnotation(ExcelTableColumnElement.class).name();
		}
		return field.getName();
	}

	public static List<String> getHeaderColumnNames(List<Field> fields) {
		return fields.stream().map(ColumnFieldHelper::getHeaderColumnName).collect(Collectors.toList());
	}

	public static boolean isCaseIgnored(Field field) {
		if (field.isAnnotationPresent(ExcelTableColumnElement.class)) {
			return field.getAnnotation(ExcelTableColumnElement.class).ignoreCase();
		}
		return (boolean) BindHelper.getAnnotationDefaultValue(ExcelTableColumnElement.class, IGNORE_CASE_METHOD_NAME);
	}

	public static DateTimeFormatter[] getFormatters(Field field) {
		List<DateTimeFormatter> dateTimeFormatters = new ArrayList<>();
		if (field.isAnnotationPresent(ExcelTableColumnElement.class)) {
			for (String datePattern : field.getAnnotation(ExcelTableColumnElement.class).dateFormatPatterns()) {
				try {
					dateTimeFormatters.add(DateTimeFormatter.ofPattern(datePattern));
				} catch (IllegalArgumentException e) {
					throw new IstfException(String.format("Unable to get valid DateTimeFormatter for field \"%1$s\" with date pattern \"%2$s\"", field.getName(), datePattern), e);
				}
			}
		}
		return dateTimeFormatters.toArray(new DateTimeFormatter[dateTimeFormatters.size()]);
	}
}
