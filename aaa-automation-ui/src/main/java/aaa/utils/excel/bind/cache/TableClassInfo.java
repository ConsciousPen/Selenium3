package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.bind.helper.TableClassHelper;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableCell;
import aaa.utils.excel.io.entity.area.table.TableColumn;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public class TableClassInfo {
	protected static Logger log = LoggerFactory.getLogger(TableClassInfo.class);

	private final Class<?> tableClass;
	private final ExcelManager excelManager;
	private final boolean strictMatch;
	private final boolean isCaseIgnoredForAllColumns;
	private final Map<Integer, Object> rowsIndexesAndCreatedObjects;
	//private final FieldsInfoCache columnFieldsCache;
	private Map<Integer, TableRow> primaryKeyColumnValuesAndRows;
	//private Map<String, TableRow> primaryKeyColumnValuesAndRowIndexes2;
	private List<TableFieldInfo> tableFieldsInfos;
	private List<Integer> primaryKeyColumnsIndexesWithUnknownValues;
	private List<Field> tableColumnsFields;
	private ExcelTable excelTable;
	private String primaryKeysSeparator;
	private Field primaryKeyColumnField;
	private Integer primaryKeyColumnIndex;
	private CellType<?> primaryKeyCellType;

	TableClassInfo(Class<?> tableClass, ExcelManager excelManager, boolean strictMatch) {
		this.tableClass = tableClass;
		this.excelManager = excelManager;
		this.strictMatch = strictMatch;
		this.isCaseIgnoredForAllColumns = TableClassHelper.isCaseIgnored(tableClass);
		//this.columnFieldsCache = new FieldsInfoCache<>(excelManager, strictMatch);
		//this.primaryKeyColumnValuesAndRows = new HashMap<>();
		//this.primaryKeyColumnValuesAndRowIndexes2 = new HashMap<>();
		this.rowsIndexesAndCreatedObjects = new HashMap<>();
		this.primaryKeyColumnsIndexesWithUnknownValues = new ArrayList<>();
	}

	public Class<?> getTableClass() {
		return tableClass;
	}

	public ExcelManager getExcelManager() {
		return excelManager;
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

	boolean isStrictMatch() {
		return strictMatch;
	}

	/*public TableRow getRow(Integer primaryKeyExpectedValue) {
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
	}*/

	public TableRow getRow(Integer primaryKeyExpectedValue) {
		if (this.primaryKeyColumnValuesAndRows == null) {
			this.primaryKeyColumnValuesAndRows = new HashMap<>(getExcelTable().getRows().size());
			/*for (TableRow row : getExcelTable().getRows()) {
				this.primaryKeyColumnValuesAndRows.put(row.getCell(getPrimaryKeyColumnIndex()).getIntValue(), row);
			}*/
			TableColumn column = getExcelTable().getColumn(getPrimaryKeyColumnIndex());
			for (TableCell cell : column.getCells()) {
				this.primaryKeyColumnValuesAndRows.put(cell.getIntValue(), cell.getRow());
			}
		}

		if (!this.primaryKeyColumnValuesAndRows.containsKey(primaryKeyExpectedValue)) {
			throw new IstfException(String.format("There is no \"%1$s\" value in primary key column #%2$s in table %3$s", primaryKeyExpectedValue, getPrimaryKeyColumnIndex(), getExcelTable()));
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

	public List<Integer> getHeaderColumnsIndexes(Field tableField) {
		return getFieldInfo(tableField).getHeaderColumnsIndexes(getExcelTable().getHeader(), isCaseIgnoredForAllColumns());
	}

	public boolean isTableField(Field tableField) {
		return getFieldInfo(tableField).isTableField();
	}

	public boolean isMultyColumnsField(Field tableField) {
		return getFieldInfo(tableField).isMultyColumnsField();
	}

	public Class<?> getFieldsTableClass(Field tableField) {
		return getFieldInfo(tableField).getTableClass();
	}

	public boolean hasObject(int rowIndex) {
		return this.rowsIndexesAndCreatedObjects.containsKey(rowIndex);
		//return false;
	}

	public Object getObject(Integer rowIndex) {
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

		List<Integer> extraTableColumnsIndexes = table.getColumnsIndexes();
		for (Field columnField : tableColumnsFields) {
			extraTableColumnsIndexes.removeAll(getFieldInfo(columnField).getHeaderColumnsIndexes(table.getHeader(), isCaseIgnoredForAllColumns()));
		}

		if (!extraTableColumnsIndexes.isEmpty()) {
			List<String> extraTableColumnNames = table.getHeader().getCells().stream().filter(c -> extraTableColumnsIndexes.contains(c.getColumnIndex())).map(ExcelCell::getStringValue).collect(Collectors.toList());
			String message = String.format("Extra header column(s) detected in excel table on sheet \"%1$s\" without binded field(s) from class \"%2$s\": %3$s.",
					table.getSheetName(), getTableClass().getName(), extraTableColumnNames);
			if (isStrictMatch()) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(extraTableColumnsIndexes.toArray(new Integer[extraTableColumnsIndexes.size()]));
		}

		return table;
	}

	private List<Field> filterTableColumnsFields(ExcelTable table, List<Field> tableFields) {
		List<Field> allTableColumnsFields = new ArrayList<>(tableFields);
		List<Field> missedTableColumnsFields = new ArrayList<>();
		for (Field columnField : allTableColumnsFields) {
			/*if (table.hasColumn(getHeaderColumnName(columnField), isCaseIgnored(columnField))) {
				missedTableColumnsFields.remove(columnField);
			}*/
			if (getFieldInfo(columnField).getHeaderColumnsIndexes(table.getHeader(), isCaseIgnoredForAllColumns()).isEmpty()) {
				missedTableColumnsFields.add(columnField);
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
