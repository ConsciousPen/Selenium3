package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

		for (Field tableRowField : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
			Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(excelManager, tableRowField, strictMatch);
			List<Object> tableRowsObjects = new ArrayList<>();
			for (TableRow row : tableAndColumnsFields.getLeft()) {
				Object rowObject = getInstance(BindHelper.getTableRowType(tableRowField));
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

	private Pair<ExcelTable, List<Field>> getTableAndColumnsFields(ExcelManager excelManager, Field tableRowField, boolean strictMatch) {
		Class<?> tableClass = BindHelper.getTableRowType(tableRowField);
		if (tableClasses.containsKey(tableClass)) {
			return tableClasses.get(tableClass);
		}

		List<Field> tableColumnsFields = BindHelper.getAllAccessibleFields(tableClass, false);
		ExcelTable table = getExcelTable(excelManager, tableRowField, tableColumnsFields, strictMatch);

		List<Field> missingTableColumnsFields = tableColumnsFields.stream()
				.filter(f -> !table.getHeader().hasColumn(TableColumnFieldProperties.getHeaderColumnName(f))).collect(Collectors.toList());
		if (!missingTableColumnsFields.isEmpty()) {
			List<String> missingFieldNames = new ArrayList<>(missingTableColumnsFields.size());
			for (Field f : missingTableColumnsFields) {
				StringBuilder missedTypeAndFieldName = new StringBuilder(f.getType().getSimpleName());
				if (BindHelper.isTableRowField(f)) {
					missedTypeAndFieldName.append("<").append(BindHelper.getTableRowType(f).getSimpleName()).append(">");
				}
				missingFieldNames.add(missedTypeAndFieldName.append(" ").append(TableColumnFieldProperties.getHeaderColumnName(f)).toString());
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
		int rowNumber = TableFieldProperties.getHeaderRowIndex(tableRowField);
		ExcelSheet sheet = excelManager.getSheet(TableFieldProperties.getSheetName(tableRowField));
		Map<String, TableColumnFieldProperties> headerColumnNamesAndFieldProperties = getHeaderColumnNamesAndFieldProperties(tableColumnsFields);
		boolean ignoreCase = TableFieldProperties.isCaseIgnored(tableRowField) || headerColumnNamesAndFieldProperties.values().stream().anyMatch(TableColumnFieldProperties::isIgnoreCase);
		List<String> headerColumnNames = TableColumnFieldProperties.getHeaderColumnNames(tableColumnsFields);
		if (rowNumber < 0) {
			//TODO-dchubkov: simplify
			table = sheet.getTable(ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			if (strictMatch) {
				table = sheet.getTable(rowNumber, ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
			} else {
				table = sheet.getTable(rowNumber);
			}
		}

		List<String> expectedFieldsColumns = TableColumnFieldProperties.getHeaderColumnNames(tableColumnsFields);
		List<String> missingFieldColumns = table.getColumnsNames().stream().filter(cn -> !expectedFieldsColumns.contains(cn)).collect(Collectors.toList());

		if (!missingFieldColumns.isEmpty()) {
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), BindHelper.getTableRowType(tableRowField).getName(), missingFieldColumns);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(missingFieldColumns.toArray(new String[missingFieldColumns.size()]));
		}
		return table;
	}

	private void setFieldValue(Field tableColumnField, Object rowObject, TableRow row, boolean strictMatch) {
		String columnName = TableColumnFieldProperties.getHeaderColumnName(tableColumnField);
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
				setFieldValue(tableColumnField, rowObject, row.isEmpty(columnName) ? null : row.getDateValue(columnName, TableColumnFieldProperties.getFormatters(tableColumnField)));
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
				Class<?> tableRowClass = BindHelper.getTableRowType(tableColumnField);
				Field primaryKeyField = TableColumnFieldProperties.getPrimaryKeyField(tableRowClass);
				List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(TableColumnFieldProperties.getPrimaryKeysSeparator(primaryKeyField)));
				for (TableRow linkedTableRow : tableAndColumnsFields.getLeft()) {
					Object tableRowObject = getInstance(tableRowClass);
					if (linkedTableRowIds.contains(TableFieldProperties.getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
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

	private Map<String, TableColumnFieldProperties> getHeaderColumnNamesAndFieldProperties(List<Field> tableColumnsFields) {
		Map<String, TableColumnFieldProperties> headerColumnNamesAndFieldProperties = new HashMap<>();
		for (Field tableColumnField : tableColumnsFields) {
			TableColumnFieldProperties columnFieldProperties = new TableColumnFieldProperties(tableColumnField);
			headerColumnNamesAndFieldProperties.put(columnFieldProperties.getName(), columnFieldProperties);
		}
		return headerColumnNamesAndFieldProperties;
	}
}
