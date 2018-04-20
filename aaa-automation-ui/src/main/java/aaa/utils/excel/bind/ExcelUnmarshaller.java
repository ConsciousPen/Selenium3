package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.cache.TableClassesCache;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	private static final Object UNMARSHAL_LOCK = new Object();
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final TableClassesCache cache;

	private Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);

	public ExcelUnmarshaller(File excelFile) {
		this(excelFile, true);
	}

	public ExcelUnmarshaller(File excelFile, boolean strictMatch) {
		this(new ExcelManager(excelFile), strictMatch);
	}

	// TODO-dchubkov: sremove after implementation of getting objects by prvided excel rows (or kind of filter table)
	public ExcelUnmarshaller(ExcelManager excelManager) {
		this(excelManager, true);
	}

	public ExcelUnmarshaller(ExcelManager excelManager, boolean strictMatch) {
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.cache = new TableClassesCache(excelManager, strictMatch);
	}

	public File getExcelFile() {
		return this.excelManager.getFile();
	}

	public boolean isStrictMatch() {
		return strictMatch;
	}

	public <T> T unmarshal(Class<T> excelFileModel) {
		return unmarshal(excelFileModel, true);
	}

	public <T> T unmarshal(Class<T> excelFileModel, boolean closeManagerOnFinish) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));

		T excelFileObject = BindHelper.getInstance(excelFileModel);
		//cache = new FieldsInfoCache(excelManager, strictMatch);

		synchronized (UNMARSHAL_LOCK) { // Used to solve performance issues when parsing thousands of excel rows simultaneously by multiple threads
			for (Field field : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
				///boolean ignoreCaseForAllFields = TableFieldHelper.isCaseIgnored(field);
				///Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(excelManager, field, ignoreCaseForAllFields, strictMatch);

				List<Object> tablesObjects = new ArrayList<>(cache.of(field).getExcelTable().getRowsNumber());
				for (TableRow row : cache.of(field).getExcelTable()) {
					Object rowObject = BindHelper.getInstance(cache.of(field).getTableFieldType());
					for (Field columnField : cache.of(field).getTableColumnsFields()) {
						boolean ignoreCase = cache.of(field).isCaseIgnoredForAllColumns() || cache.of(field).isCaseIgnored(columnField);
						setFieldValue(columnField, rowObject, row, ignoreCase, strictMatch);
					}
					tablesObjects.add(rowObject);
				}
				BindHelper.setFieldValue(field, excelFileObject, tablesObjects);
			}

			if (closeManagerOnFinish) {
				excelManager.close();
			}

			log.info("Excel unmarshalling was successful.");
		}
		return excelFileObject;
	}

	public void marshal(Object excelFileObject, File excelFile) {
		//TODO-dchubkov: To be implemented...
		throw new NotImplementedException("Excel marshalling is not implemented yet");
	}

	public void flushCache() {
		this.cache.flushAll();
	}

	/*private static Pair<ExcelTable, List<Field>> getTableAndColumnsFields(ExcelManager excelManager, Field field, boolean ignoreCaseForAllFields, boolean strictMatch) {
		Class<?> tableClass = BindHelper.getTableClass(field);
		if (tableClasses.containsKey(tableClass)) {
			return tableClasses.get(tableClass);
		}

		List<Field> tableColumnsFields = BindHelper.getAllAccessibleFields(tableClass, false);
		ExcelTable table = getExcelTable(excelManager, field, tableColumnsFields, ignoreCaseForAllFields, strictMatch);

		List<Field> missedTableColumnsFields = new ArrayList<>(tableColumnsFields);
		for (Field columnField : tableColumnsFields) {
			boolean ignoreCase = ignoreCaseForAllFields || ColumnFieldHelper.isCaseIgnored(columnField);
			if (table.hasColumn(ColumnFieldHelper.getHeaderColumnName(columnField), ignoreCase)) {
				missedTableColumnsFields.remove(columnField);
			}
		}

		if (!missedTableColumnsFields.isEmpty()) {
			List<String> missedFieldColumnNames = new ArrayList<>(missedTableColumnsFields.size());
			for (Field f : missedTableColumnsFields) {
				StringBuilder missedTypeAndFieldName = new StringBuilder(f.getType().getSimpleName());
				if (BindHelper.isfield(f)) {
					missedTypeAndFieldName.append("<").append(BindHelper.getTableClass(f).getSimpleName()).append(">");
				}
				missedFieldColumnNames.add(missedTypeAndFieldName.append(" ").append(ColumnFieldHelper.getHeaderColumnName(f)).toString());
			}
			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), tableClass.getName(), missedFieldColumnNames);

			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed." + message);
			}
			log.warn("{} Field(s) with missed column(s) in result object will have default value(s) of appropriate type(s)", message);
		}
		tableColumnsFields.removeAll(missedTableColumnsFields);
		Pair<ExcelTable, List<Field>> tableAndFields = Pair.of(table, tableColumnsFields);
		tableClasses.put(tableClass, tableAndFields);
		return tableAndFields;
	}*/

	/*private static ExcelTable getExcelTable(ExcelManager excelManager, Field field, List<Field> tableColumnsFields, boolean ignoreCaseForAllFields, boolean strictMatch) {
		ExcelTable table;
		int rowNumber = TableFieldHelper.getHeaderRowIndex(field);
		ExcelSheet sheet = excelManager.getSheet(TableFieldHelper.getSheetName(field));
		boolean ignoreCase = ignoreCaseForAllFields || tableColumnsFields.stream().anyMatch(ColumnFieldHelper::isCaseIgnored);
		List<String> headerColumnNames = ColumnFieldHelper.getHeaderColumnNames(tableColumnsFields);
		if (rowNumber < 0) {
			table = sheet.getTable(ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			if (strictMatch) {
				table = sheet.getTable(rowNumber, null, ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
			} else {
				table = sheet.getTable(rowNumber);
			}
		}

		List<String> extraTableColumnNames = table.getColumnsNames();
		for (Field columnField : tableColumnsFields) {
			String fieldColumnName = ColumnFieldHelper.getHeaderColumnName(columnField);
			Predicate<String> fieldColumnExistsInTable = ignoreCaseForAllFields || ColumnFieldHelper.isCaseIgnored(columnField) ? fieldColumnName::equalsIgnoreCase : fieldColumnName::equals;
			extraTableColumnNames.removeIf(fieldColumnExistsInTable);
			if (extraTableColumnNames.isEmpty()) {
				break;
			}
		}

		if (!extraTableColumnNames.isEmpty()) {
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheetName(), BindHelper.getTableClass(field).getName(), extraTableColumnNames);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(extraTableColumnNames.toArray(new String[extraTableColumnNames.size()]));
		}
		return table;
	}*/

	private void setFieldValue(Field tableColumnField, Object rowObject, TableRow row, boolean ignoreColumnNameCase, boolean strictMatch) {
		//String columnName = ColumnFieldHelper.getHeaderColumnName(tableColumnField);
		//String columnName = cache.of(field).getHeaderColumnName(tableColumnField);
		String columnName = cache.ofColumnField(tableColumnField).getHeaderColumnName();
		TableCell cell = row.getCell(columnName, ignoreColumnNameCase);
		switch (tableColumnField.getType().getName()) {
			case "int":
			case "java.lang.Integer":
				BindHelper.setFieldValue(tableColumnField, rowObject, cell.isEmpty() ? null : cell.getIntValue());
				break;
			case "boolean":
			case "java.lang.Boolean":
				BindHelper.setFieldValue(tableColumnField, rowObject, cell.isEmpty() ? null : cell.getBoolValue());
				break;
			case "java.lang.String":
				BindHelper.setFieldValue(tableColumnField, rowObject, cell.getStringValue());
				break;
			case "java.time.LocalDateTime":
				BindHelper.setFieldValue(tableColumnField, rowObject, cell.isEmpty() ? null : cell.getDateValue(ColumnFieldHelper.getFormatters(tableColumnField)));
				break;
			case "double":
			case "java.lang.Double":
				BindHelper.setFieldValue(tableColumnField, rowObject, cell.isEmpty() ? null : cell.getDoubleValue());
				break;
			case "java.util.List":
				String linkedRowsIds = cell.getStringValue();
				if (linkedRowsIds.isEmpty()) {
					break;
				}
				//Class<?> tableClass = BindHelper.getTableClass(tableColumnField);
				Class<?> tableClass = cache.ofColumnField(tableColumnField).getTableFieldType();
				//Field primaryKeyField = ColumnFieldHelper.getPrimaryKeyField(tableClass);
				//Field primaryKeyField = cache.of(field).getPrimaryKeyField();
				//List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(ColumnFieldHelper.getPrimaryKeysSeparator(primaryKeyField)));
				//List<String> linkedTableRowIds = Arrays.stream(linkedRowsIds.split(ColumnFieldHelper.getPrimaryKeysSeparator(primaryKeyField))).collect(Collectors.toList());
				////List<String> linkedTableRowIds = Arrays.stream(linkedRowsIds.split(cache.of(field).getPrimaryKeysSeparator())).collect(Collectors.toList());
				String[] linkedTableRowIds = linkedRowsIds.split(cache.of(tableColumnField).getPrimaryKeysSeparator());

				///Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(row.getTable().getExcelManager(), tableColumnField, ignoreColumnNameCase, strictMatch);
				//boolean ignorePrimaryKeyColumnNameCase = ignoreColumnNameCase || ColumnFieldHelper.isCaseIgnored(primaryKeyField);
				//boolean ignorePrimaryKeyColumnNameCase = ignoreColumnNameCase || cache.of(field).isCaseIgnored(primaryKeyField);
				//int primaryKeyColumnIndex = tableAndColumnsFields.getLeft().getColumnIndex(ColumnFieldHelper.getHeaderColumnName(primaryKeyField), ignorePrimaryKeyColumnNameCase);
				//int primaryKeyColumnIndex = cache.of(field).getExcelTable().getColumnIndex(ColumnFieldHelper.getHeaderColumnName(primaryKeyField), ignorePrimaryKeyColumnNameCase);
				int primaryKeyColumnIndex = cache.of(tableColumnField).getPrimaryKeyColumnIndex();
				/*List<TableRow> linkedTableRows = tableAndColumnsFields.getLeft().getRows().stream().filter(r -> linkedTableRowIds.contains(r.getStringValue(primaryKeyColumnIndex)))
						.collect(Collectors.toList());*/
				/*List<TableRow> linkedTableRows = new ArrayList<>(linkedTableRowIds.size());
				for (TableRow linkedTableRow : tableAndColumnsFields.getLeft().getRows()) {
					if (linkedTableRowIds.isEmpty()) {
						break;
					}
					String cellValue = linkedTableRow.getStringValue(primaryKeyColumnIndex);
					if (linkedTableRowIds.contains(cellValue)) {
						linkedTableRows.add(linkedTableRow);
						linkedTableRowIds.remove(cellValue);
					}
				}*/
				/*List<String> primaryKeyColumnValues = tableAndColumnsFields.getLeft().getColumn(primaryKeyColumnIndex).getStringValues();
				for (String id : linkedTableRowIds) {
					int linkedTableRowIndex = primaryKeyColumnValues.indexOf(id) + 1;
					linkedTableRows.add(tableAndColumnsFields.getLeft().getRow(linkedTableRowIndex));
				}*/

				//cache.of(tableColumnField).getRows(linkedTableRowIds);

				List<TableRow> linkedTableRows = cache.of(tableColumnField).getRows(linkedTableRowIds);

				//TODO-dchubkov: cache same tableObjects
				List<Object> tableObjects = new ArrayList<>(linkedTableRows.size());
				for (TableRow linkedTableRow : linkedTableRows) {
					//Object tableObject = BindHelper.getInstance(tableClass);
					Object tableObject;
					if (cache.of(tableColumnField).hasObject(linkedTableRow.getIndex())) {
						tableObject = cache.of(tableColumnField).getObject(linkedTableRow.getIndex());
					} else {
						tableObject = BindHelper.getInstance(tableClass);
						for (Field linkedTableField : cache.of(tableColumnField).getTableColumnsFields()) {
							///boolean ignoreCase = ignoreColumnNameCase || ColumnFieldHelper.isCaseIgnored(linkedfield);
							boolean ignoreCase = cache.of(tableColumnField).isCaseIgnoredForAllColumns() || cache.of(tableColumnField).isCaseIgnored(linkedTableField);
							//setFieldValue(linkedfield, tableObject, linkedTableRow, ignoreCase, strictMatch);
							setFieldValue(linkedTableField, tableObject, linkedTableRow, ignoreCase, strictMatch);
						}
						cache.of(tableColumnField).setObject(linkedTableRow.getIndex(), tableObject);
					}

					tableObjects.add(tableObject);
				}

				BindHelper.setFieldValue(tableColumnField, rowObject, tableObjects);
				break;
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableColumnField.getType().getName()));
		}
	}
}
