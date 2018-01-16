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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	protected static Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);
	private Map<Class<?>, Pair<ExcelTable, List<Field>>> tableClasses = new HashMap<>();

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel) {
		return unmarshal(excelFile, excelFileModel, false);
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel, boolean strictMatch) {
		return unmarshal(new ExcelManager(excelFile), excelFileModel, strictMatch, true);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel) {
		return unmarshal(excelManager, excelFileModel, false, true);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, boolean strictMatch, boolean closeManagerOnFinish) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));

		T excelFileInstance = getInstance(excelFileModel);

		for (Field tableField : getAllFields(excelFileModel, true)) {
			Pair<ExcelTable, List<Field>> tableRowAndFields = getTableAndAllFields(excelManager, tableField, strictMatch);
			List<Object> tableFieldsInstances = new ArrayList<>();
			for (TableRow row : tableRowAndFields.getLeft()) {
				Object tableInstance = getInstance(getTableRowType(tableField));
				for (Field tableRowField : tableRowAndFields.getRight()) {
					setFieldValue(tableRowField, tableInstance, row, strictMatch);
				}
				tableFieldsInstances.add(tableInstance);
			}
			setFieldValue(tableField, excelFileInstance, tableFieldsInstances);
		}

		if (closeManagerOnFinish) {
			excelManager.close();
		}
		tableClasses.clear();

		log.info("Excel unmarshalling was successful.");
		return excelFileInstance;
	}

	private <T> T getInstance(Class<T> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IstfException(String.format("Failed to create instance of \"%s\" class.", clazz.getName()), e);
		}
	}

	private List<Field> getAllFields(Class<?> tableClass, boolean onlyTables) {
		List<Field> fields = new ArrayList<>();
		for (Class<?> clazz : getThisAndAllSuperClasses(tableClass)) {
			for (Field field : clazz.getDeclaredFields()) {
				if (isAccessible(field, tableClass)) {
					if (field.isAnnotationPresent(ExcelTransient.class) || onlyTables && !isTableField(field)) {
						continue;
					}
					fields.add(field);
				}
			}
		}
		return fields;
	}

	private List<Class<?>> getThisAndAllSuperClasses(Class<?> clazz) {
		List<Class<?>> allSuperClasses = new ArrayList<>();
		allSuperClasses.add(clazz);
		while (clazz.getClasses() != null && !clazz.getSuperclass().equals(Object.class)) {
			clazz = clazz.getSuperclass();
			allSuperClasses.add(clazz);
		}
		return allSuperClasses;
	}

	private Pair<ExcelTable, List<Field>> getTableAndAllFields(ExcelManager excelManager, Field tableField, boolean strictMatch) {
		Class<?> tableClass = getTableRowType(tableField);
		if (tableClasses.containsKey(tableClass)) {
			return tableClasses.get(tableClass);
		}

		List<Field> tableFields = getAllFields(tableClass, false);
		ExcelTable table = getExcelTable(excelManager, tableField, tableFields, strictMatch);

		List<String> expectedFieldsColumns = getHeaderColumnNames(tableFields);
		List<Field> fieldsWithMissingColumns = tableFields.stream().filter(f -> !table.getHeader().hasColumn(getHeaderColumnName(f))).collect(Collectors.toList());

		if (!fieldsWithMissingColumns.isEmpty()) {
			Map<String, String> missingTableColumns = new HashMap<>();
			fieldsWithMissingColumns.forEach(f -> missingTableColumns.put(f.getType().getSimpleName(), getHeaderColumnName(f)));

			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), tableClass.getName(), missingTableColumns.entrySet());
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed." + message);
			}
			log.warn("{} Field(s) with missed column(s) in result object will have default value(s) of appropriate type(s)", message);
		}
		tableFields.removeAll(fieldsWithMissingColumns);
		Pair<ExcelTable, List<Field>> tableAndFields = Pair.of(table, tableFields);
		tableClasses.put(tableClass, tableAndFields);
		return tableAndFields;
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
		List<String> missingFieldColumns = table.getColumnsNames().stream().filter(cn -> !expectedFieldsColumns.contains(cn)).collect(Collectors.toList());

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

	private boolean isAccessible(Field field, Class<?> clazz) {
		return Objects.equals(field.getDeclaringClass(), clazz) || Modifier.isProtected(field.getModifiers()) || Modifier.isPublic(field.getModifiers());
	}

	private boolean isTableField(Field field) {
		boolean isTableField = List.class.equals(field.getType());
		assertThat(!isTableField && field.isAnnotationPresent(ExcelTableElement.class))
				.as("\"%1$s\" annotation should be assigned to the \"%2$s\" type only!", ExcelTableElement.class.getName(), List.class.getName()).isFalse();
		return isTableField;
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
			case "java.lang.Integer":
				setFieldValue(tableRowField, tableInstance, row.isEmpty(columnName) ? null : row.getIntValue(columnName));
				break;
			case "boolean":
			case "java.lang.Boolean":
				setFieldValue(tableRowField, tableInstance, row.isEmpty(columnName) ? null : row.getBoolValue(columnName));
				break;
			case "java.lang.String":
				setFieldValue(tableRowField, tableInstance, row.getStringValue(columnName));
				break;
			case "java.time.LocalDateTime":
				setFieldValue(tableRowField, tableInstance, row.isEmpty(columnName) ? null : row.getDateValue(columnName));
				break;
			case "java.util.List":
				String linkedRowsIds = row.getStringValue(columnName);
				if (linkedRowsIds.isEmpty()) {
					break;
				}
				Pair<ExcelTable, List<Field>> tableRowAndlinkedTableRowFields = getTableAndAllFields(row.getTable().getExcelManager(), tableRowField, strictMatch);
				List<Object> linkedTableRows = new ArrayList<>();
				Class<?> linkedTableRowClass = getTableRowType(tableRowField);
				Field primaryKeyField = getPrimaryKeyField(linkedTableRowClass);
				List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(getPrimaryKeysSeparator(primaryKeyField)));
				for (TableRow linkedTableRow : tableRowAndlinkedTableRowFields.getLeft()) {
					Object linkedTableRowInstance = getInstance(linkedTableRowClass);
					if (linkedTableRowIds.contains(getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
						for (Field linkedTableRowField : tableRowAndlinkedTableRowFields.getRight()) {
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
			//TODO check set null value to primitive type
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
