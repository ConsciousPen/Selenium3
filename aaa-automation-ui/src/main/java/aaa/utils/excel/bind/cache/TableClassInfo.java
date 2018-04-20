package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.bind.helper.TableClassHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class TableClassInfo {
	protected static Logger log = LoggerFactory.getLogger(TableClassInfo.class);

	private final Class<?> tableClass;
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final boolean isCaseIgnoredForAllColumns;
	//private final FieldsInfoCache columnFieldsCache;
	private final Map<String, Integer> primaryKeyColumnValuesAndRowIndexes;
	private final Map<Integer, Object> rowsIndexesAndCreatedObjects;
	//private Map<String, TableRow> primaryKeyColumnValuesAndRowIndexes2;

	private List<TableFieldInfo> tableFieldsInfos;
	private List<Integer> primaryKeyColumnIndexesWithUnknownValues;
	private List<Field> tableColumnsFields;
	private ExcelTable excelTable;
	private String primaryKeysSeparator;
	private Field primaryKeyColumnField;
	private Integer primaryKeyColumnIndex;

	TableClassInfo(Class<?> tableClass, ExcelManager excelManager, boolean strictMatch) {
		this.tableClass = tableClass;
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.isCaseIgnoredForAllColumns = TableClassHelper.isCaseIgnored(tableClass);
		//this.columnFieldsCache = new FieldsInfoCache<>(excelManager, strictMatch);
		this.primaryKeyColumnValuesAndRowIndexes = new HashMap<>();
		//this.primaryKeyColumnValuesAndRowIndexes2 = new HashMap<>();
		this.rowsIndexesAndCreatedObjects = new HashMap<>();
		this.primaryKeyColumnIndexesWithUnknownValues = new ArrayList<>();
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

	public ExcelManager getExcelManager() {
		return excelManager;
	}

	boolean isStrictMatch() {
		return strictMatch;
	}

	public boolean isCaseIgnoredForAllColumns() {
		return isCaseIgnoredForAllColumns;
	}

	public List<Field> getTableColumnsFields() {
		if (this.tableColumnsFields == null) {
			this.tableColumnsFields = BindHelper.getAllAccessibleFields(getTableClass(), false);
		}
		return Collections.unmodifiableList(this.tableColumnsFields);
	}

	public ExcelTable getExcelTable() {
		if (this.excelTable == null) {
			this.excelTable = findTable();
			this.tableColumnsFields = filterTableColumnsFields(getExcelTable(), getTableColumnsFields());
		}
		return this.excelTable;
	}

	public String getPrimaryKeysSeparator() {
		if (this.primaryKeysSeparator == null) {
			this.primaryKeysSeparator = ColumnFieldHelper.getPrimaryKeysSeparator(getPrimaryKeyColumnField());
		}
		return this.primaryKeysSeparator;
	}

	public Field getPrimaryKeyColumnField() {
		if (this.primaryKeyColumnField == null) {
			this.primaryKeyColumnField = TableClassHelper.getPrimaryKeyField(getTableClass(), getTableColumnsFields());
		}
		return this.primaryKeyColumnField;
	}

	public Integer getPrimaryKeyColumnIndex() {
		if (this.primaryKeyColumnIndex == null) {
			boolean ignoreCase = isCaseIgnoredForAllColumns() || isCaseIgnored(getPrimaryKeyColumnField());
			this.primaryKeyColumnIndex = getExcelTable().getColumnIndex(getHeaderColumnName(getPrimaryKeyColumnField()), ignoreCase);
		}
		return this.primaryKeyColumnIndex;
	}

	public boolean isCaseIgnoredInAnyColumnField() {
		return getTableColumnsFields().stream().anyMatch(this::isCaseIgnored);
	}

	public List<String> getHeaderColumnNames() {
		return getTableColumnsFields().stream().map(this::getHeaderColumnName).collect(Collectors.toList());
	}

	public List<TableRow> getRows(String... valuesInPrimaryKeyColumnField) {
		List<TableRow> foundRows = new ArrayList<>(valuesInPrimaryKeyColumnField.length);
		for (String expectedValue : valuesInPrimaryKeyColumnField) {
			if (!this.primaryKeyColumnValuesAndRowIndexes.containsKey(expectedValue)) {
				List<Integer> rowIndexes = new ArrayList<>(getRowIndexesWithUnknownPrimaryKeyValues());
				for (Integer index : rowIndexes) {
					TableRow row = getExcelTable().getRow(index);
					String cellValue = row.getStringValue(getPrimaryKeyColumnIndex());
					this.primaryKeyColumnValuesAndRowIndexes.put(cellValue, index);
					getRowIndexesWithUnknownPrimaryKeyValues().remove(index);
					if (Objects.equals(cellValue, expectedValue)) {
						foundRows.add(row);
						break;
					}
				}
			} else {
				foundRows.add(getExcelTable().getRow(this.primaryKeyColumnValuesAndRowIndexes.get(expectedValue)));
			}
		}
		return foundRows;
	}

	/*public List<TableRow> getRows(String... valuesInPrimaryKeyColumnField) {
		List<TableRow> foundRows = new ArrayList<>(valuesInPrimaryKeyColumnField.length);
		if (this.primaryKeyColumnValuesAndRowIndexes2.isEmpty()) {
			for (TableRow row : getExcelTable()) {
				String cellValue = row.getStringValue(getPrimaryKeyColumnIndex());
				if (cellValue != null) {
					this.primaryKeyColumnValuesAndRowIndexes2.put(cellValue, row);
				}
			}
		}

		for (String expectedValue : valuesInPrimaryKeyColumnField) {
			foundRows.add(this.primaryKeyColumnValuesAndRowIndexes2.get(expectedValue));
		}
		return foundRows;
	}*/

	public TableFieldInfo getFieldInfo(Field tableField) {
		for (TableFieldInfo tableFieldInfo : getTableFieldsInfos()) {
			if (tableFieldInfo.getTableField().equals(tableField)) {
				return tableFieldInfo;
			}
		}
		throw new IstfException(String.format("Class \"%s\" does not have \"%s\" field", getClass().getName(), tableField));
	}

	public boolean isCaseIgnored(Field tableField) {
		return getFieldInfo(tableField).isCaseIgnored();
	}

	public String getHeaderColumnName(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnName();
	}

	public boolean isTableField(Field tableField) {
		return getFieldInfo(tableField).isTableField();
	}

	public Class<?> getFieldsTableClass(Field tableField) {
		return getFieldInfo(tableField).getTableClass();
	}

	public boolean hasObject(int rowIndex) {
		return this.rowsIndexesAndCreatedObjects.containsKey(rowIndex);
		//return false;
	}

	public Object getObject(int rowIndex) {
		return this.rowsIndexesAndCreatedObjects.get(rowIndex);
	}

	public void setObject(int rowIndex, Object object) {
		this.rowsIndexesAndCreatedObjects.put(rowIndex, object);
	}

	private List<TableFieldInfo> getTableFieldsInfos() {
		if (this.tableFieldsInfos == null) {
			List<Field> tableColumnsFields = getTableColumnsFields();
			this.tableFieldsInfos = new ArrayList<>(tableColumnsFields.size());
			for (Field tableField : tableColumnsFields) {
				this.tableFieldsInfos.add(new TableFieldInfo(tableField));
			}
		}
		return this.tableFieldsInfos;
	}

	private ExcelTable findTable() {
		int headerRowIndex = TableClassHelper.getHeaderRowIndex(tableClass);
		ExcelTable table;
		List<Field> tableColumnsFields = getTableColumnsFields();

		ExcelSheet sheet = getExcelManager().getSheet(TableClassHelper.getSheetName(getTableClass()));
		boolean ignoreCase = isCaseIgnoredForAllColumns() || isCaseIgnoredInAnyColumnField();
		List<String> headerColumnNames = getHeaderColumnNames();
		if (headerRowIndex < 0) {
			table = sheet.getTable(ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			if (isStrictMatch()) {
				table = sheet.getTable(headerRowIndex, null, ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
			} else {
				table = sheet.getTable(headerRowIndex);
			}
		}

		List<String> extraTableColumnNames = table.getColumnsNames();
		for (Field columnField : tableColumnsFields) {
			String fieldColumnName = getHeaderColumnName(columnField);
			Predicate<String> fieldColumnExistsInTable = isCaseIgnoredForAllColumns() || isCaseIgnored(columnField) ? fieldColumnName::equalsIgnoreCase : fieldColumnName::equals;
			extraTableColumnNames.removeIf(fieldColumnExistsInTable);
			if (extraTableColumnNames.isEmpty()) {
				break;
			}
		}
		if (!extraTableColumnNames.isEmpty()) {
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheetName(), getTableClass().getName(), extraTableColumnNames);
			if (isStrictMatch()) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(extraTableColumnNames.toArray(new String[extraTableColumnNames.size()]));
		}

		return table;
	}

	private List<Field> filterTableColumnsFields(ExcelTable table, List<Field> tableFields) {
		List<Field> allTableColumnsFields = new ArrayList<>(tableFields);
		List<Field> missedTableColumnsFields = new ArrayList<>(allTableColumnsFields);
		for (Field columnField : allTableColumnsFields) {
			boolean ignoreCase = isCaseIgnoredForAllColumns() || isCaseIgnored(columnField);
			if (table.hasColumn(getHeaderColumnName(columnField), ignoreCase)) {
				missedTableColumnsFields.remove(columnField);
			}
		}

		if (!missedTableColumnsFields.isEmpty()) {
			List<String> missedFieldColumnNames = new ArrayList<>(missedTableColumnsFields.size());
			for (Field f : missedTableColumnsFields) {
				StringBuilder missedTypeAndFieldName = new StringBuilder(f.getType().getSimpleName());
				if (isTableField(f)) {
					missedTypeAndFieldName.append("<").append(getFieldsTableClass(f).getSimpleName()).append(">");
				}
				missedFieldColumnNames.add(missedTypeAndFieldName.append(" ").append(getHeaderColumnName(f)).toString());
			}
			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), getTableClass().getName(), missedFieldColumnNames);

			if (isStrictMatch()) {
				throw new IstfException("Excel unmarshalling with strict match has been failed." + message);
			}
			log.warn("{} Field(s) with missed column(s) in result object will have default value(s) of appropriate type(s)", message);
		}

		allTableColumnsFields.removeAll(missedTableColumnsFields);
		return allTableColumnsFields;
	}

	private List<Integer> getRowIndexesWithUnknownPrimaryKeyValues() {
		if (this.primaryKeyColumnIndexesWithUnknownValues.isEmpty()) {
			this.primaryKeyColumnIndexesWithUnknownValues = getExcelTable().getRowsIndexes();
		}
		return this.primaryKeyColumnIndexesWithUnknownValues;
	}
}
