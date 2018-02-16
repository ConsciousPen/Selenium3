package aaa.utils.excel.bind;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.bind.helper.TableFieldHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class ExcelUnmarshaller {
	protected static Logger log = LoggerFactory.getLogger(ExcelUnmarshaller.class);
	private Map<Class<?>, Pair<ExcelTable, List<Field>>> tableClasses = new HashMap<>();

	//TODO-dchubkov: implement registerCellType(CellType<?>... cellTypes) method

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel) {
		return unmarshal(excelFile, excelFileModel, false);
	}

	public <T> T unmarshal(File excelFile, Class<T> excelFileModel, boolean strictMatch, int... rowsIndexes) {
		return unmarshal(new ExcelManager(excelFile), excelFileModel, strictMatch, rowsIndexes);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, int... rowsIndexes) {
		return unmarshal(excelManager, excelFileModel, false, rowsIndexes);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, boolean strictMatch, int... rowsIndexes) {
		return unmarshal(excelManager, excelFileModel, strictMatch, true, rowsIndexes);
	}

	public <T> T unmarshal(ExcelManager excelManager, Class<T> excelFileModel, boolean strictMatch, boolean closeManagerOnFinish, int... rowsIndexes) {
		//TODO: check excelFileModel is valid class (not primitive, etc...)
		log.info(String.format("Getting \"%1$s\" object model from excel file \"%2$s\" %3$s strict match binding",
				excelFileModel.getSimpleName(), excelManager.getFile().getAbsolutePath(), strictMatch ? "with" : "without"));

		T excelFileObject = BindHelper.getInstance(excelFileModel);

		for (Field tableRowField : BindHelper.getAllAccessibleFields(excelFileModel, true)) {
			boolean ignoreCaseForAllFields = TableFieldHelper.isCaseIgnored(tableRowField);
			Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(excelManager, tableRowField, ignoreCaseForAllFields, strictMatch);

			List<Object> tableRowsObjects = new ArrayList<>();
			for (TableRow row : tableAndColumnsFields.getLeft()) {
				Object rowObject = BindHelper.getInstance(BindHelper.getTableRowType(tableRowField));
				for (Field columnField : tableAndColumnsFields.getRight()) {
					boolean ignoreCase = ignoreCaseForAllFields || ColumnFieldHelper.isCaseIgnored(columnField);
					setFieldValue(columnField, rowObject, row, ignoreCase, strictMatch);
				}
				tableRowsObjects.add(rowObject);
			}
			BindHelper.setFieldValue(tableRowField, excelFileObject, tableRowsObjects);
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

	private Pair<ExcelTable, List<Field>> getTableAndColumnsFields(ExcelManager excelManager, Field tableRowField, boolean ignoreCaseForAllFields, boolean strictMatch) {
		Class<?> tableClass = BindHelper.getTableRowType(tableRowField);
		if (tableClasses.containsKey(tableClass)) {
			return tableClasses.get(tableClass);
		}

		List<Field> tableColumnsFields = BindHelper.getAllAccessibleFields(tableClass, false);
		ExcelTable table = getExcelTable(excelManager, tableRowField, tableColumnsFields, ignoreCaseForAllFields, strictMatch);

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
				if (BindHelper.isTableRowField(f)) {
					missedTypeAndFieldName.append("<").append(BindHelper.getTableRowType(f).getSimpleName()).append(">");
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
	}

	private ExcelTable getExcelTable(ExcelManager excelManager, Field tableRowField, List<Field> tableColumnsFields, boolean ignoreCaseForAllFields, boolean strictMatch) {
		ExcelTable table;
		int rowNumber = TableFieldHelper.getHeaderRowIndex(tableRowField);
		ExcelSheet sheet = excelManager.getSheet(TableFieldHelper.getSheetName(tableRowField));
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
					table.getSheetName(), BindHelper.getTableRowType(tableRowField).getName(), extraTableColumnNames);
			if (strictMatch) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(extraTableColumnNames.toArray(new String[extraTableColumnNames.size()]));
		}
		return table;
	}

	private void setFieldValue(Field tableColumnField, Object rowObject, TableRow row, boolean ignoreColumnNameCase, boolean strictMatch) {
		String columnName = ColumnFieldHelper.getHeaderColumnName(tableColumnField);
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
				Class<?> tableRowClass = BindHelper.getTableRowType(tableColumnField);
				Field primaryKeyField = ColumnFieldHelper.getPrimaryKeyField(tableRowClass);
				List<String> linkedTableRowIds = Arrays.asList(linkedRowsIds.split(ColumnFieldHelper.getPrimaryKeysSeparator(primaryKeyField)));

				boolean ignoreCaseForAllFields = TableFieldHelper.isCaseIgnored(tableColumnField);
				Pair<ExcelTable, List<Field>> tableAndColumnsFields = getTableAndColumnsFields(row.getTable().getExcelManager(), tableColumnField, ignoreCaseForAllFields, strictMatch);
				List<Object> tableRowObjects = new ArrayList<>();
				for (TableRow linkedTableRow : tableAndColumnsFields.getLeft()) {
					Object tableRowObject = BindHelper.getInstance(tableRowClass);
					if (linkedTableRowIds.contains(TableFieldHelper.getPrimaryKeyValue(primaryKeyField, linkedTableRow))) {
						for (Field linkedTableRowField : tableAndColumnsFields.getRight()) {
							boolean ignoreCase = ignoreCaseForAllFields || ColumnFieldHelper.isCaseIgnored(linkedTableRowField);
							setFieldValue(linkedTableRowField, tableRowObject, linkedTableRow, ignoreCase, strictMatch);
						}
						tableRowObjects.add(tableRowObject);
					}
				}
				BindHelper.setFieldValue(tableColumnField, rowObject, tableRowObjects);
				break;
			default:
				throw new IstfException(String.format("Field type \"%s\" is not supported for unmarshalling", tableColumnField.getType().getName()));
		}
	}
}
