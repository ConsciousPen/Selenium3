package aaa.utils.excel.bind;

import static toolkit.verification.CustomAssertions.assertThat;
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
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.ExcelSheet;
import aaa.utils.excel.io.entity.ExcelTable;
import aaa.utils.excel.io.entity.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	protected static Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel) {
		return unmarshal(excelFile, excelFileModel, false);
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel, boolean strictMatch) {
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelFile.getAbsolutePath(), strictMatch ? "with" : "without"));
		T excelFileInstance = getInstance(excelFileModel);
		ExcelManager excelManager = new ExcelManager(excelFile);

		for (Field tableField : getAllFields(excelFileModel, true)) {
			List<Field> tableRowFields = getAllFields(getTableRowType(tableField));
			ExcelTable table = getExcelTable(excelManager, tableField, tableRowFields, strictMatch);
			tableRowFields = getFilteredFields(table, tableField, tableRowFields, strictMatch);

			List<Object> tableFieldsInstances = new ArrayList<>();
			for (TableRow row : table) {
				Object tableInstance = getInstance(getTableRowType(tableField));
				for (Field tableRowField : tableRowFields) {
					setFieldValue(tableRowField, tableInstance, row, strictMatch);
				}
				tableFieldsInstances.add(tableInstance);
			}
			setFieldValue(tableField, excelFileInstance, tableFieldsInstances);
		}
		excelManager.close();

		log.info("Excel unmarshalling was successful.");
		return excelFileInstance;
	}

	private ExcelTable getExcelTable(ExcelManager excelManager, Field tableField, List<Field> tableRowFields, boolean strictMatch) {
		ExcelTable table;
		int rowNumber;
		if (tableField.isAnnotationPresent(ExcelTableElement.class)) {
			rowNumber = tableField.getAnnotation(ExcelTableElement.class).headerRowNumber();
		} else {
			rowNumber = (int) getAnnotationDefaultValue(ExcelTableElement.class, "headerRowNumber");
		}

		ExcelSheet sheet = excelManager.getSheet(getSheetName(tableField));
		if (rowNumber < 0) {
			List<String> headerColumnNames = getHeaderColumnNames(tableRowFields);
			table = sheet.getTable(isLowestTable(tableField), headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			table = sheet.getTable(rowNumber);
		}

		List<String> expectedFieldsColumns = getHeaderColumnNames(tableRowFields);
		List<String> missingFieldColumns = table.getColumnNames().stream().filter(cn -> !expectedFieldsColumns.contains(cn)).collect(Collectors.toList());

		if (!missingFieldColumns.isEmpty()) {
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), getTableRowType(tableField).getName(), missingFieldColumns);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(missingFieldColumns.toArray(new String[missingFieldColumns.size()]));
		}
		return table;
	}

	private List<Field> getFilteredFields(ExcelTable table, Field tableField, List<Field> tableRowFields, boolean strictMatch) {
		List<Field> fields = new ArrayList<>(tableRowFields);
		List<String> expectedFieldsColumns = getHeaderColumnNames(tableRowFields);
		List<Field> fieldsWithMissingColumns = tableRowFields.stream().filter(f -> !table.getHeader().hasColumnName(getHeaderColumnName(f))).collect(Collectors.toList());

		if (!fieldsWithMissingColumns.isEmpty()) {
			List<String> missingTableColumns = getHeaderColumnNames(fieldsWithMissingColumns);
			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), getTableRowType(tableField).getName(), missingTableColumns);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Field(s) with missed column(s) in result object will have default value(s) of appropriate type(s)", message);
		}
		fields.removeAll(fieldsWithMissingColumns);
		return fields;
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
					if (field.isAnnotationPresent(ExcelTransient.class) || onlyTables && !isTableField(field)) {
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
		assertThat(!isTableField && field.isAnnotationPresent(ExcelTableElement.class))
				.as("\"%1$s\" annotation should be assigned to the \"%2$s\" type only!", ExcelTableElement.class.getName(), List.class.getName()).isFalse();
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

	private void setFieldValue(Field tableRowField, Object tableInstance, TableRow row, boolean strictMatch) {
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
				String linkedRowsIds = row.getStringValue(columnName);
				if (linkedRowsIds.isEmpty()) {
					break;
				}
				List<Field> linkedTableRowFields = getAllFields(getTableRowType(tableRowField));
				ExcelTable table = getExcelTable(row.getSheet().getExcelManager(), tableRowField, linkedTableRowFields, strictMatch);
				linkedTableRowFields = getFilteredFields(table, tableRowField, linkedTableRowFields, strictMatch);

				List<Object> linkedTableRows = new ArrayList<>();
				Class<?> linkedTableRowClass = getTableRowType(tableRowField);
				Field primaryKeyField = getPrimaryKeyField(linkedTableRowClass);
				List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(getPrimaryKeysSeparator(primaryKeyField)));
				for (TableRow linkedTableRow : table) {
					Object linkedTableRowInstance = getInstance(linkedTableRowClass);
					if (linkedTableRowIds.contains(getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
						for (Field linkedTableRowField : linkedTableRowFields) {
							setFieldValue(linkedTableRowField, linkedTableRowInstance, linkedTableRow, strictMatch);
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
		} catch (IllegalAccessException | IllegalArgumentException e) {
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
		assertThat(List.class.equals(tableField.getType())).as("Excel Table field has \"%1$s\" type but should be \"%2$s\"", tableField.getType(), List.class.getName()).isTrue();
		ParameterizedType parameterizedType = (ParameterizedType) tableField.getGenericType();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	private List<String> getHeaderColumnNames(List<Field> tableRowFields) {
		return tableRowFields.stream().map(this::getHeaderColumnName).collect(Collectors.toList());
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
