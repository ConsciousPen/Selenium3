package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import aaa.utils.excel.ExcelParser;
import aaa.utils.excel.table.ExcelTable;
import aaa.utils.excel.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	public <T> T unmarshal(File excelFile, Class<T> excelFileModel) {
		return unmarshal(excelFile, excelFileModel, false);
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel, boolean strictMatch) {
		T excelFileInstance = getInstance(excelFileModel);
		ExcelParser excelParser = new ExcelParser(excelFile);

		for (Field tableField : getAllFields(excelFileModel, true)) {
			excelParser.switchSheet(getSheetName(tableField));
			List<Field> tableRowFields = getAllFields(getTableRowType(tableField));
			ExcelTable excelTable = excelParser.getTable(isLowestTable(tableField), getHeaderColumnNames(tableRowFields));

			List<Object> tableFields = new ArrayList<>();
			for (TableRow row : excelTable) {
				Object tableInstance = getInstance(getTableRowType(tableField));
				for (Field tableRowField : tableRowFields) {
					setFieldValue(tableRowField, tableInstance, row);
				}
				tableFields.add(tableInstance);
			}
			setFieldValue(tableField, excelFileInstance, tableFields);
		}
		return excelFileInstance;
	}

	private <T> T getInstance(Class<T> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IstfException(String.format("Failed to create instance of \"%s\" class.", clazz.getName()), e);
		}
	}

	private List<Field> getAllFields(Class<?> tableClass) {
		return getAllFields(tableClass, false);
	}

	private List<Field> getAllFields(Class<?> tableClass, boolean onlyTables) {
		List<Field> allTableFields = new ArrayList<>();
		for (Class<?> clazz : getThisAndAllSuperClasses(tableClass)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isAccessible(field, tableClass)) {
					if (onlyTables && !isTableField(field)) {
						continue;
					}
					allTableFields.add(field);
				}
			}
		}
		return allTableFields;
	}

	private boolean isAccessible(Field field, Class<?> clazz) {
		return Objects.equals(field.getDeclaringClass(), clazz) || Modifier.isProtected(field.getModifiers()) || Modifier.isPublic(field.getModifiers());
	}

	private boolean isTableField(Field field) {
		boolean isTableField = List.class.equals(field.getType());
		if (!isTableField && field.isAnnotationPresent(ExcelTableElement.class)) {
			throw new IstfException(String.format("\"%1$s\" annotation should be assigned to the \"%2$s\" type only!", ExcelTableElement.class.getName(), List.class.getName()));
		}
		return isTableField;
	}

	private List<Class<?>> getThisAndAllSuperClasses(Class<?> clazz) {
		List<Class<?>> allSuperClasses = new ArrayList<>();
		allSuperClasses.add(clazz);
		while (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
			clazz = clazz.getSuperclass();
			allSuperClasses.add(clazz);
		}
		return allSuperClasses;
	}

	private boolean isLowestTable(Field tableField) {
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			return tableField.getAnnotation(ExcelTableElement.class).isLowest();
		}
		return (boolean) getAnnotationDefaultValue(ExcelTableElement.class, "isLowest");
	}

	private void setFieldValue(Field tableRowField, Object tableInstance, TableRow row) {
		String columnName = getHeaderColumnName(tableRowField);
		switch (tableRowField.getType().getName()) {
			case "int":
			case "Integer":
				setFieldValue(tableRowField, tableInstance, row.getIntValue(columnName));
				break;
			case "boolean":
			case "Boolean":
				setFieldValue(tableRowField, tableInstance, row.getBoolValue(columnName));
				break;
			case "java.lang.String":
				setFieldValue(tableRowField, tableInstance, row.getStringValue(columnName));
				break;
			case "java.time.LocalDateTime":
				setFieldValue(tableRowField, tableInstance, row.getDateValue(columnName));
				break;
			case "java.util.List":
				String linkedRowsIds = row.getStringValue(getHeaderColumnName(tableRowField));
				if (linkedRowsIds.isEmpty()) {
					break;
				}
				List<Field> linkedTableRowFields = getAllFields(getTableRowType(tableRowField));
				ExcelParser excelParser = new ExcelParser(row.getTable().getSheet()).switchSheet(getSheetName(tableRowField));
				ExcelTable excelTable = excelParser.getTable(isLowestTable(tableRowField), getHeaderColumnNames(linkedTableRowFields));

				List<Object> linkedTableRows = new ArrayList<>();
				for (TableRow linkedTableRow : excelTable) {
					Class<?> linkedTableRowClass = getTableRowType(tableRowField);
					Object linkedTableRowInstance = getInstance(linkedTableRowClass);
					Field primaryKeyField = getPrimaryKeyField(linkedTableRowClass);
					List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(getPrimaryKeysSeparator(primaryKeyField)));
					if (linkedTableRowIds.contains(getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
						for (Field linkedTableRowField : linkedTableRowFields) {
							setFieldValue(linkedTableRowField, linkedTableRowInstance, linkedTableRow);
						}
						linkedTableRows.add(linkedTableRowInstance);
					}
				}
				setFieldValue(tableRowField, tableInstance, linkedTableRows);
				break;
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableRowField.getType().getName()));
		}
	}

	private void setFieldValue(Field field, Object classInstance, Object value) {
		if (!field.isAccessible()) {
			//TODO-dchubkov: find appropriate setter method and use it for set value
			field.setAccessible(true);
		}
		try {
			field.set(classInstance, value);
		} catch (IllegalAccessException e) {
			throw new IstfException(String.format("Unable set value to the field \"%1$s\" in class \"%2$s\"", field.getName(), classInstance.getClass().getName()), e);
		}
	}

	private Field getPrimaryKeyField(Class<?> tableRowClass) {
		for (Field field : tableRowClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(ExcelTableColumnElement.class) && field.getAnnotation(ExcelTableColumnElement.class).isPrimaryKey()) {
				return field;
			}
		}
		throw new IstfException(String.format("\"%s\" class does not have any primary key field", tableRowClass.getName()));
	}

	private String getPrimaryKeyValue(Field primaryKeyField, TableRow tableRow) {
		return tableRow.getStringValue(getHeaderColumnName(primaryKeyField));
	}

	private String getPrimaryKeysSeparator(Field primaryKeyField) {
		if (primaryKeyField.isAnnotationPresent(ExcelTableColumnElement.class)) {
			return primaryKeyField.getAnnotation(ExcelTableColumnElement.class).primaryKeysSeparator();
		}
		return (String) getAnnotationDefaultValue(ExcelTableColumnElement.class, "primaryKeysSeparator");
	}

	private Class<?> getTableRowType(Field tableField) {
		if (!List.class.equals(tableField.getType())) {
			throw new IstfException(String.format("Excel Table field has \"%1$s\" type but should be \"%2$s\"", tableField.getType(), List.class.getName()));
		}
		ParameterizedType parameterizedType = (ParameterizedType) tableField.getGenericType();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	private String[] getHeaderColumnNames(List<Field> tableRowFields) {
		return tableRowFields.stream().map(this::getHeaderColumnName).toArray(String[]::new);
	}

	private String getHeaderColumnName(Field tableRowField) {
		String defaultNameMarker = (String) getAnnotationDefaultValue(ExcelTableColumnElement.class, "name");
		if (tableRowField.isAnnotationPresent(ExcelTableColumnElement.class) && !tableRowField.getAnnotation(ExcelTableColumnElement.class).name().equals(defaultNameMarker)) {
			return tableRowField.getAnnotation(ExcelTableColumnElement.class).name();
		}
		return tableRowField.getName();
	}

	private Object getAnnotationDefaultValue(Class<?> annotationClass, String methodName) {
		Method method;
		try {
			method = annotationClass.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException e) {
			throw new IstfException(String.format("\"%1$s\" annotation does not have \"%2$s\" method.", annotationClass.getName(), methodName), e);
		}
		return method.getDefaultValue();
	}

	private String getSheetName(Field tableField) {
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			return tableField.getAnnotation(ExcelTableElement.class).sheetName();
		}
		return tableField.getName();
	}
}
