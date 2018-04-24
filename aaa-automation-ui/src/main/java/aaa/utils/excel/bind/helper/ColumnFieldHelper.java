package aaa.utils.excel.bind.helper;

import java.lang.reflect.Field;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import aaa.utils.excel.bind.annotation.ExcelColumnElement;
import toolkit.exceptions.IstfException;

public class ColumnFieldHelper {
	private static final String COLUMN_NAME_METHOD_NAME = "name";
	private static final String CONTAINS_COLUMN_NAME_METHOD_NAME = "containsName";
	private static final String IGNORE_CASE_METHOD_NAME = "ignoreCase";
	private static final String PRIMARY_KEY_SSEPARATOR_METHOD_NAME = "primaryKeysSeparator";

	public static String getPrimaryKeysSeparator(Field primaryKeyField) {
		if (primaryKeyField.isAnnotationPresent(ExcelColumnElement.class)) {
			return primaryKeyField.getAnnotation(ExcelColumnElement.class).primaryKeysSeparator();
		}
		return (String) BindHelper.getAnnotationDefaultValue(ExcelColumnElement.class, PRIMARY_KEY_SSEPARATOR_METHOD_NAME);
	}

	public static String getHeaderColumnName(Field field) {
		if (field.isAnnotationPresent(ExcelColumnElement.class) && !field.getAnnotation(ExcelColumnElement.class).name().equals(ExcelColumnElement.DEFAULT_FIELD_VALUE_MARK)) {
			return field.getAnnotation(ExcelColumnElement.class).name();
		}
		return field.getName();
	}

	public static String getHeaderColumnNamePattern(Field field) {
		//TODO-dchubkov: ignore if name argument is set
		if (field.isAnnotationPresent(ExcelColumnElement.class) && !field.getAnnotation(ExcelColumnElement.class).containsName().equals(ExcelColumnElement.DEFAULT_FIELD_VALUE_MARK)) {
			return field.getAnnotation(ExcelColumnElement.class).containsName();
		}
		return getHeaderColumnName(field);
	}

	public static List<String> getHeaderColumnNames(List<Field> fields) {
		return fields.stream().map(ColumnFieldHelper::getHeaderColumnName).collect(Collectors.toList());
	}

	public static boolean isCaseIgnored(Field field) {
		if (field.isAnnotationPresent(ExcelColumnElement.class)) {
			return field.getAnnotation(ExcelColumnElement.class).ignoreCase();
		}
		return (boolean) BindHelper.getAnnotationDefaultValue(ExcelColumnElement.class, IGNORE_CASE_METHOD_NAME);
	}

	public static boolean hasHeaderColumnNamePattern(Field field) {
		return field.isAnnotationPresent(ExcelColumnElement.class)
				&& !Objects.equals(field.getAnnotation(ExcelColumnElement.class).containsName(), ExcelColumnElement.DEFAULT_FIELD_VALUE_MARK);
	}

	public static DateTimeFormatter[] getFormatters(Field field) {
		List<DateTimeFormatter> dateTimeFormatters = new ArrayList<>();
		if (field.isAnnotationPresent(ExcelColumnElement.class)) {
			for (String datePattern : field.getAnnotation(ExcelColumnElement.class).dateFormatPatterns()) {
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
