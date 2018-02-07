package aaa.utils.excel.bind;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.NotImplementedException;
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

	//TODO-dchubkov: implement registerCellType(CellType<?>... cellTypes) method

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel) {
		return unmarshal(excelFile, excelFileModel, false);
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel, boolean strictMatch) {
		return unmarshal(new ExcelManager(excelFile), excelFileModel, strictMatch);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel) {
		return unmarshal(excelManager, excelFileModel, false);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, boolean strictMatch) {
		return unmarshal(excelManager, excelFileModel, strictMatch, true);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, boolean strictMatch, boolean closeManagerOnFinish) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));

		T excelFileObject = getInstance(excelFileModel);

		for (Field tableRowField : getAllAccessibleFields(excelFileModel, true)) {
			Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(excelManager, tableRowField, strictMatch);
			List<Object> tableRowsObjects = new ArrayList<>();
			for (TableRow row : tableAndColumnsFields.getLeft()) {
				Object rowObject = getInstance(getTableRowType(tableRowField));
				for (Field columnField : tableAndColumnsFields.getRight()) {
					setFieldValue(columnField, rowObject, row, strictMatch);
				}
				tableRowsObjects.add(rowObject);
			}
			setFieldValue(tableRowField, excelFileObject, tableRowsObjects);
		}

		if (closeManagerOnFinish) {
			excelManager.close();
		}
		tableClasses.clear();

		log.info("Excel unmarshalling was successful.");
		return excelFileObject;
	}

	public void marshal(Object excelFileObject, File excelFile) {
		//TODO-dchubkov: To be implemented...
		throw new NotImplementedException("Excel marshalling is not implemented yet");
	}

	private <T> T getInstance(Class<T> clazz) {
		try {
			return clazz.getConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IstfException(String.format("Failed to create instance of \"%s\" class.", clazz.getName()), e);
		}
	}

	private List<Field> getAllAccessibleFields(Class<?> tableClass, boolean onlyTables) {
		List<Field> fields = new ArrayList<>();
		for (Field field : getAllAccessibleFieldsFromThisAndSuperClasses(tableClass)) {
			if (!field.isAnnotationPresent(ExcelTransient.class)) {
				if (onlyTables && !isTableRowField(field)) {
					continue;
				}
				fields.add(field);
			}
		}
		return fields;
	}

	private List<Field> getAllAccessibleFieldsFromThisAndSuperClasses(Class<?> tableClass) {
		List<Field> accessibleFields = new ArrayList<>();
		for (Class<?> clazz : getThisAndAllSuperClasses(tableClass)) {
			for (Field field : clazz.getDeclaredFields()) {
				boolean isLocalField = Objects.equals(field.getDeclaringClass(), clazz);
				boolean isPublic = Modifier.isPublic(field.getModifiers());
				boolean isProtected = Modifier.isProtected(field.getModifiers());
				boolean isPackagePrivateAndAccessible =
						!Modifier.isPrivate(field.getModifiers()) && !isPublic && !isProtected && field.getDeclaringClass().getPackage().getName().equals(clazz.getPackage().getName());
				boolean isNotHiddenByChildClassField = accessibleFields.stream().noneMatch(f -> Objects.equals(field.getName(), f.getName()));

				if ((isLocalField || isPublic || isProtected || isPackagePrivateAndAccessible) && isNotHiddenByChildClassField) {
					accessibleFields.add(field);
				}
			}
		}
		return accessibleFields;
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

	private Pair<ExcelTable, List<Field>> getTableAndColumnsFields(ExcelManager excelManager, Field tableRowField, boolean strictMatch) {
		Class<?> tableClass = getTableRowType(tableRowField);
		if (tableClasses.containsKey(tableClass)) {
			return tableClasses.get(tableClass);
		}

		List<Field> tableColumnsFields = getAllAccessibleFields(tableClass, false);
		ExcelTable table = getExcelTable(excelManager, tableRowField, tableColumnsFields, strictMatch);

		List<String> expectedFieldColumns = getHeaderColumnNames(tableColumnsFields);
		List<Field> missingTableColumnsFields = tableColumnsFields.stream().filter(f -> !table.getHeader().hasColumn(getHeaderColumnName(f))).collect(Collectors.toList());

		if (!missingTableColumnsFields.isEmpty()) {
			List<String> missingFieldNames = new ArrayList<>(missingTableColumnsFields.size());
			for (Field f : missingTableColumnsFields) {
				StringBuilder missedTypeAndFieldName = new StringBuilder(f.getType().getSimpleName());
				if (isTableRowField(f)) {
					missedTypeAndFieldName.append("<").append(getTableRowType(f).getSimpleName()).append(">");
				}
				missingFieldNames.add(missedTypeAndFieldName.append(" ").append(getHeaderColumnName(f)).toString());
			}
			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), tableClass.getName(), missingFieldNames);

			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed." + message);
			}
			log.warn("{} Field(s) with missed column(s) in result object will have default value(s) of appropriate type(s)", message);
		}
		tableColumnsFields.removeAll(missingTableColumnsFields);
		Pair<ExcelTable, List<Field>> tableAndFields = Pair.of(table, tableColumnsFields);
		tableClasses.put(tableClass, tableAndFields);
		return tableAndFields;
	}

	private ExcelTable getExcelTable(ExcelManager excelManager, Field tableRowField, List<Field> tableColumnsFields, boolean strictMatch) {
		ExcelTable table;
		int rowNumber;
		if (tableRowField.isAnnotationPresent(ExcelTableElement.class)) {
			rowNumber = tableRowField.getAnnotation(ExcelTableElement.class).headerRowNumber();
		} else {
			rowNumber = (int) getAnnotationDefaultValue(ExcelTableElement.class, "headerRowNumber");
		}

		ExcelSheet sheet = excelManager.getSheet(getSheetName(tableRowField));
		if (rowNumber < 0) {
			List<String> headerColumnNames = getHeaderColumnNames(tableColumnsFields);
			table = sheet.getTable(isLowestTable(tableRowField), headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			table = sheet.getTable(rowNumber);
		}

		List<String> expectedFieldsColumns = getHeaderColumnNames(tableColumnsFields);
		List<String> missingFieldColumns = table.getColumnsNames().stream().filter(cn -> !expectedFieldsColumns.contains(cn)).collect(Collectors.toList());

		if (!missingFieldColumns.isEmpty()) {
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), getTableRowType(tableRowField).getName(), missingFieldColumns);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(missingFieldColumns.toArray(new String[missingFieldColumns.size()]));
		}
		return table;
	}

	private boolean isTableRowField(Field field) {
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

	private void setFieldValue(Field tableColumnField, Object rowObject, TableRow row, boolean strictMatch) {
		String columnName = getHeaderColumnName(tableColumnField);
		switch (tableColumnField.getType().getName()) {
			case "int":
			case "java.lang.Integer":
				setFieldValue(tableColumnField, rowObject, row.isEmpty(columnName) ? null : row.getIntValue(columnName));
				break;
			case "boolean":
			case "java.lang.Boolean":
				setFieldValue(tableColumnField, rowObject, row.isEmpty(columnName) ? null : row.getBoolValue(columnName));
				break;
			case "java.lang.String":
				setFieldValue(tableColumnField, rowObject, row.getStringValue(columnName));
				break;
			case "java.time.LocalDateTime":
				setFieldValue(tableColumnField, rowObject, row.isEmpty(columnName) ? null : row.getDateValue(columnName, getFormatters(tableColumnField)));
				break;
			case "double":
			case "java.lang.Double":
				setFieldValue(tableColumnField, rowObject, row.isEmpty(columnName) ? null : row.getDoubleValue(columnName));
				break;
			case "java.util.List":
				String linkedRowsIds = row.getStringValue(columnName);
				if (linkedRowsIds.isEmpty()) {
					break;
				}
				Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(row.getTable().getExcelManager(), tableColumnField, strictMatch);
				List<Object> tableRowObjects = new ArrayList<>();
				Class<?> tableRowClass = getTableRowType(tableColumnField);
				Field primaryKeyField = getPrimaryKeyField(tableRowClass);
				List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(getPrimaryKeysSeparator(primaryKeyField)));
				for (TableRow linkedTableRow : tableAndColumnsFields.getLeft()) {
					Object tableRowObject = getInstance(tableRowClass);
					if (linkedTableRowIds.contains(getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
						for (Field linkedTableRowField : tableAndColumnsFields.getRight()) {
							setFieldValue(linkedTableRowField, tableRowObject, linkedTableRow, strictMatch);
						}
						tableRowObjects.add(tableRowObject);
					}
				}
				setFieldValue(tableColumnField, rowObject, tableRowObjects);
				break;
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableColumnField.getType().getName()));
		}
	}

	private DateTimeFormatter[] getFormatters(Field tableColumnField) {
		List<DateTimeFormatter> dateTimeFormatters = new ArrayList<>();
		if (tableColumnField.isAnnotationPresent(ExcelTableColumnElement.class)) {
			for (String datePattern : tableColumnField.getAnnotation(ExcelTableColumnElement.class).dateFormatPatterns()) {
				try {
					dateTimeFormatters.add(DateTimeFormatter.ofPattern(datePattern));
				} catch (IllegalArgumentException e) {
					throw new IstfException(String.format("Unable to get valid DateTimeFormatter for field \"%1$s\" with date pattern \"%2$s\"", tableColumnField.getName(), datePattern), e);
				}
			}
		}
		return dateTimeFormatters.toArray(new DateTimeFormatter[dateTimeFormatters.size()]);
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
		for (Field field : getAllAccessibleFieldsFromThisAndSuperClasses(tableRowClass)) {
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

	private Class<?> getTableRowType(Field tableRowField) {
		assertThat(List.class.equals(tableRowField.getType())).as("Excel Table field has \"%1$s\" type but should be \"%2$s\"", tableRowField.getType(), List.class.getName()).isTrue();
		ParameterizedType parameterizedType = (ParameterizedType) tableRowField.getGenericType();
		return (Class<?>) parameterizedType.getActualTypeArguments()[0];
	}

	private List<String> getHeaderColumnNames(List<Field> tableColumnsFields) {
		return tableColumnsFields.stream().map(this::getHeaderColumnName).collect(Collectors.toList());
	}

	private String getHeaderColumnName(Field tableColumnField) {
		String defaultNameMarker = (String) getAnnotationDefaultValue(ExcelTableColumnElement.class, "name");
		if (tableColumnField.isAnnotationPresent(ExcelTableColumnElement.class) && !tableColumnField.getAnnotation(ExcelTableColumnElement.class).name().equals(defaultNameMarker)) {
			return tableColumnField.getAnnotation(ExcelTableColumnElement.class).name();
		}
		return tableColumnField.getName();
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
