package aaa.utils.excel;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import aaa.utils.openl.model.ExcelTableColumnElement;
import aaa.utils.openl.model.ExcelTableElement;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	public <T> ExcelUnmarshaller(Class<T> modelClass) {
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileType) {
		ExcelParser excelParser = new ExcelParser(excelFile);
		T excelFileInstance = (T) getInstance(excelFileType); //TODO-dchubkov: handle warning

		List<Object> tableFields = new ArrayList<>();
		for (Field tableField : getAllTableFields(excelFileType)) {
			excelParser.switchSheet(getSheetName(tableField));
			List<Field> tableRowFields = getAllTableRowFields(tableField);
			ExcelTable excelTable = excelParser.getTable(getHeaderColumnNames(tableRowFields), tableField.getAnnotation(ExcelTableElement.class).isLowest());

			Object tableInstance = getInstance(getTableRowType(tableField));
			for (TableRow row : excelTable) {
				for (Field tableRowField : tableRowFields) {
					setFieldValue(tableRowField, tableInstance, row, excelParser);
				}
			}

			tableFields.add(tableInstance);
			tableField.setAccessible(true);
			try {
				tableField.set(excelFileInstance, tableFields);
			} catch (IllegalAccessException e) {
				e.printStackTrace(); //TODO-dchubkov: throw own exception
			}
		}
		return excelFileInstance;
	}

	private Object getInstance(Class<?> clazz) {
		Object instance = null;
		try {
			instance = clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace(); //TODO-dchubkov: throw own exception
		}
		return instance;
	}

	private Class<?> getTableRowType(Field tableField) {
		ParameterizedType parameterizedType = (ParameterizedType) tableField.getGenericType();
		//TODO-dchubkov: assert generic type is List
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	private void setFieldValue(Field tableRowField, Object tableInstance, TableRow row, ExcelParser excelParser) {
		tableRowField.setAccessible(true);
		String columnName = getHeaderColumnName(tableRowField);
		try {
			switch (tableRowField.getType().getName()) {
				case "int":
				case "Integer":
					tableRowField.set(tableInstance, row.getIntValue(columnName));
					break;
				case "boolean":
				case "Boolean":
					tableRowField.set(tableInstance, row.getBoolValue(columnName));
					break;
				case "java.lang.String":
					tableRowField.set(tableInstance, row.getValue(columnName));
					break;
				case "java.time.LocalDateTime":
					tableRowField.set(tableInstance, row.getDateValue(columnName));
					break;
				case "java.util.List":
					List<Field> linkedTableRowFields = getAllTableRowFields(tableRowField);
					excelParser.switchSheet(getSheetName(tableRowField));
					ExcelTable excelTable = excelParser.getTable(getHeaderColumnNames(linkedTableRowFields), tableRowField.getAnnotation(ExcelTableElement.class).isLowest());

					List<Object> linkedTableRows = new ArrayList<>();
					for (TableRow linkedTableRow : excelTable) {
						Class<?> linkedTableRowClass = getTableRowType(tableRowField);
						Object linkedTableRowInstance = getInstance(linkedTableRowClass);
						List<String> linkedTableRowIds = Arrays.asList(row.getValue(getHeaderColumnName(tableRowField)).split(","));

						if (linkedTableRowIds.contains(getPrimaryKeyValue(linkedTableRowClass, linkedTableRow))) {
							for (Field linkedTableRowField : linkedTableRowFields) {
								setFieldValue(linkedTableRowField, linkedTableRowInstance, linkedTableRow, excelParser);
							}
							linkedTableRows.add(linkedTableRowInstance);
						}
					}

					tableRowField.set(tableInstance, linkedTableRows);
					break;
				default:
					throw new IstfException("Unsupported field type: " + tableRowField.getType().getName()); //TODO-dchubkov: make better message
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace(); //TODO-dchubkov: make better message
		}
	}

	private String getPrimaryKeyValue(Class<?> tableRowClass, TableRow tableRow) {
		String primaryKeyValue = "";
		for (Field tableRowField : tableRowClass.getDeclaredFields()) {
			if (tableRowField.isAnnotationPresent(ExcelTableColumnElement.class) && tableRowField.getAnnotation(ExcelTableColumnElement.class).isPrimaryKey()) {
				primaryKeyValue = tableRow.getValue(getHeaderColumnName(tableRowField));
				break;
			}
		}
		return primaryKeyValue;
	}

	private List<Field> getAllTableRowFields(Field tableField) {
		//TODO-dchubkov: get protected tables from super classes?
		//Class<?> tableRowType = (Class<?>) parameterizedType.getActualTypeArguments()[0];

		List<Class<?>> allClasses = new ArrayList<>();
		List<Field> allTableRowFields = new ArrayList<>();
		Class<?> tableRowClass = getTableRowType(tableField);
		allTableRowFields.addAll(Arrays.asList(tableRowClass.getDeclaredFields()));
		while (tableRowClass.getSuperclass() != null && !tableRowClass.getSuperclass().equals(Object.class)) {
			tableRowClass = tableRowClass.getSuperclass();
			allClasses.add(tableRowClass);
		}

		for (Class<?> clazz : allClasses) {
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isProtected(field.getModifiers()) || Modifier.isPublic(field.getModifiers())) {
					allTableRowFields.add(field);
				}
			}
		}

		return allTableRowFields;
	}

	private Set<String> getHeaderColumnNames(List<Field> tableRowFields) {
		return tableRowFields.stream().map(this::getHeaderColumnName).collect(Collectors.toSet());
	}

	private String getHeaderColumnName(Field tableRowField) {
		String defaultNameMarker = "##default";
		if (tableRowField.isAnnotationPresent(ExcelTableColumnElement.class) && !tableRowField.getAnnotation(ExcelTableColumnElement.class).name().equals(defaultNameMarker)) {
			return tableRowField.getAnnotation(ExcelTableColumnElement.class).name();
		}
		return tableRowField.getName();
	}

	private List<Field> getAllTableFields(Class<?> excelFileType) {
		List<Field> allTableFields = new ArrayList<>();
		List<Class<?>> allClasses = new ArrayList<>();
		allClasses.add(excelFileType);

		//Class<T> fileType = excelFileType;
		while (excelFileType.getSuperclass() != null && !excelFileType.getSuperclass().equals(Object.class)) {
			excelFileType = excelFileType.getSuperclass();
			allClasses.add(excelFileType);
		}

		for (Class<?> clazz : allClasses) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(ExcelTableElement.class) && Modifier.isProtected(field.getModifiers()) || Modifier.isPublic(field.getModifiers())) {
					if (!field.getType().equals(List.class)) {
						throw new IstfException("ExcelTableElement annotation should be assigned as List type only"); //TODO-dchubkov: make better message
					}
					allTableFields.add(field);
				}
			}
		}

		return allTableFields;
		//return allClasses.stream().filter(c -> c.equals(List.class)).flatMap(t -> Arrays.stream(t.getDeclaredFields())).filter(t -> t.isAnnotationPresent(ExcelTableElement.class)).collect(Collectors.toList());
	}

	private String getSheetName(Field tableField) {
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			return tableField.getAnnotation(ExcelTableElement.class).sheetName();
		}
		return tableField.getName();
	}
}
