package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.bind.helper.TableClassHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class TableClassInfo<T> {
	protected static Logger log = LoggerFactory.getLogger(TableClassInfo.class);

	private final Class<T> tableClass;
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final boolean isCaseIgnoredForAllColumns;
	//private final FieldsInfoCache columnFieldsCache;
	private final Map<Integer, TableRow> primaryKeyColumnValuesAndRows;
	private final Map<Integer, T> rowsIndexesAndCreatedObjects;
	//private Map<String, TableRow> primaryKeyColumnValuesAndRowIndexes2;

	private List<TableFieldInfo> tableFieldsInfos;
	private List<Integer> primaryKeyColumnsIndexesWithUnknownValues;
	private List<Field> tableColumnsFields;
	private ExcelTable excelTable;
	private String primaryKeysSeparator;
	private Field primaryKeyColumnField;
	private Integer primaryKeyColumnIndex;
	private CellType<?> primaryKeyCellType;

	TableClassInfo(Class<T> tableClass, ExcelManager excelManager, boolean strictMatch) {
		this.tableClass = tableClass;
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.isCaseIgnoredForAllColumns = TableClassHelper.isCaseIgnored(tableClass);
		//this.columnFieldsCache = new FieldsInfoCache<>(excelManager, strictMatch);
		this.primaryKeyColumnValuesAndRows = new HashMap<>();
		//this.primaryKeyColumnValuesAndRowIndexes2 = new HashMap<>();
		this.rowsIndexesAndCreatedObjects = new HashMap<>();
		this.primaryKeyColumnsIndexesWithUnknownValues = new ArrayList<>();
	}

	public Class<T> getTableClass() {
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
			//boolean ignoreCase = isCaseIgnoredForAllColumns() || isCaseIgnored(getPrimaryKeyColumnField());
			//this.primaryKeyColumnIndex = getExcelTable().getColumnIndex(getHeaderColumnName(getPrimaryKeyColumnField()), ignoreCase);
			this.primaryKeyColumnIndex = getHeaderColumnIndex(getPrimaryKeyColumnField());
		}
		return this.primaryKeyColumnIndex;
	}

	public boolean isCaseIgnoredInAnyColumnField() {
		return getTableColumnsFields().stream().anyMatch(this::isCaseIgnored);
	}

	public List<String> getHeaderColumnNames() {
		return getTableColumnsFields().stream().map(this::getHeaderColumnName).collect(Collectors.toList());
	}

	public TableRow getRow(Integer primaryKeyExpectedValue) {
		if (!this.primaryKeyColumnValuesAndRows.containsKey(primaryKeyExpectedValue)) {
			List<Integer> rowsIndexes = new ArrayList<>(getRowsIndexesWithUnknownPrimaryKeyValues());
			for (Integer index : rowsIndexes) {
				TableRow row = getExcelTable().getRow(index);
				Integer cellValue = row.getCell(getPrimaryKeyColumnIndex()).getIntValue();
				this.primaryKeyColumnValuesAndRows.put(cellValue, row);
				getRowsIndexesWithUnknownPrimaryKeyValues().remove(index);
				if (Objects.equals(cellValue, primaryKeyExpectedValue)) {
					return row;
				}
			}
		}

		return this.primaryKeyColumnValuesAndRows.get(primaryKeyExpectedValue);
	}

	public List<TableRow> getRows(List<Integer> primaryKeyExpectedValues) {
		if (CollectionUtils.isEmpty(primaryKeyExpectedValues)) {
			return getExcelTable().getRows();
		}

		List<TableRow> foundRows = new ArrayList<>(primaryKeyExpectedValues.size());
		for (Integer expectedValue : primaryKeyExpectedValues) {
			foundRows.add(getRow(expectedValue));
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
		return isCaseIgnoredForAllColumns() || getFieldInfo(tableField).isCaseIgnored();
	}

	public String getHeaderColumnName(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnName();
	}

	public int getHeaderColumnIndex(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnIndex(getExcelTable().getHeader(), isCaseIgnoredForAllColumns());
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

	public T getObject(Integer rowIndex) {
		return this.rowsIndexesAndCreatedObjects.get(rowIndex);
	}

	public void setObject(int rowIndex, T object) {
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
		List<String> headerColumnNames = getHeaderColumnNames();
		if (headerRowIndex < 0) {
			table = sheet.getTable(isCaseIgnoredInAnyColumnField(), headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			if (isStrictMatch()) {
				table = sheet.getTable(headerRowIndex, null, isCaseIgnoredInAnyColumnField(), headerColumnNames.toArray(new String[headerColumnNames.size()]));
			} else {
				table = sheet.getTable(headerRowIndex);
			}
		}

		List<String> extraTableColumnNames = table.getColumnsNames();
		for (Field columnField : tableColumnsFields) {
			String fieldColumnName = getHeaderColumnName(columnField);
			Predicate<String> fieldColumnExistsInTable = isCaseIgnored(columnField) ? fieldColumnName::equalsIgnoreCase : fieldColumnName::equals;
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
			if (table.hasColumn(getHeaderColumnName(columnField), isCaseIgnored(columnField))) {
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

	private List<Integer> getRowsIndexesWithUnknownPrimaryKeyValues() {
		if (this.primaryKeyColumnsIndexesWithUnknownValues.isEmpty()) {
			this.primaryKeyColumnsIndexesWithUnknownValues = getExcelTable().getRowsIndexes();
		}
		return this.primaryKeyColumnsIndexesWithUnknownValues;
	}
}
