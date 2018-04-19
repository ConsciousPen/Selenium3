package aaa.utils.excel.bind.cache;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import aaa.utils.excel.bind.helper.BindHelper;
import aaa.utils.excel.bind.helper.ColumnFieldHelper;
import aaa.utils.excel.bind.helper.TableFieldHelper;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import aaa.utils.excel.io.entity.area.table.TableRow;
import toolkit.exceptions.IstfException;

public final class TableFieldInfo extends FieldInfo {
	private final Class<?> tableFieldType;
	private final boolean isCaseIgnoredForAllColumns;
	private final String sheetName;
	private final int headerRowIndex;
	//private final FieldsInfoCache columnFieldsCache;
	private final Map<String, Integer> primaryKeyColumnValuesAndRowIndexes;
	private final Map<Integer, Object> rowIndexesAndCreatedObjects;
	//private Map<String, TableRow> primaryKeyColumnValuesAndRowIndexes2;

	private List<Integer> primaryKeyColumnIndexesWithUnknownValues;
	private List<Field> tableColumnsFields;
	private ExcelTable excelTable;
	private String primaryKeysSeparator;
	private Field primaryKeyColumnField;
	private Integer primaryKeyColumnIndex;

	TableFieldInfo(Field tableField, FieldsInfoCache fieldsInfoCache) {
		super(tableField, fieldsInfoCache);
		this.tableFieldType = BindHelper.getTableRowType(tableField);
		this.isCaseIgnoredForAllColumns = TableFieldHelper.isCaseIgnored(tableField);
		this.sheetName = TableFieldHelper.getSheetName(getField());
		this.headerRowIndex = TableFieldHelper.getHeaderRowIndex(getField());
		//this.columnFieldsCache = new FieldsInfoCache<>(excelManager, strictMatch);
		this.primaryKeyColumnValuesAndRowIndexes = new HashMap<>();
		//this.primaryKeyColumnValuesAndRowIndexes2 = new HashMap<>();
		this.rowIndexesAndCreatedObjects = new HashMap<>();
		this.primaryKeyColumnIndexesWithUnknownValues = new ArrayList<>();
	}

	/*@Override
	protected TableFieldInfo initialize() {
		return this;
	}*/

	public Class<?> getTableFieldType() {
		return tableFieldType;
	}

	public boolean isCaseIgnoredForAllColumns() {
		return isCaseIgnoredForAllColumns;
	}

	public List<Field> getTableColumnsFields() {
		if (this.tableColumnsFields == null) {
			this.tableColumnsFields = BindHelper.getAllAccessibleFields(getTableFieldType(), false);
			//this.tableColumnsFields = filterTableColumnsFields(getExcelTable(), allAccessibleTableColumnsFields);
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
			this.primaryKeyColumnField = ColumnFieldHelper.getPrimaryKeyField(getTableFieldType(), getTableColumnsFields());
		}
		return this.primaryKeyColumnField;
	}

	public Integer getPrimaryKeyColumnIndex() {
		if (this.primaryKeyColumnIndex == null) {
			boolean ignoreCase = this.isCaseIgnoredForAllColumns || isCaseIgnored(getPrimaryKeyColumnField());
			this.primaryKeyColumnIndex = getExcelTable().getColumnIndex(getHeaderColumnName(getPrimaryKeyColumnField()), ignoreCase);
		}
		return this.primaryKeyColumnIndex;
	}

	public String getSheetName() {
		return this.sheetName;
	}

	public int getHeaderRowIndex() {
		return this.headerRowIndex;
	}

	public boolean isCaseIgnoredInAnyColumnField() {
		return getTableColumnsFields().stream().anyMatch(this::isCaseIgnored);
	}

	/*public FieldsInfoCache<ColumnFieldInfo> getColumnFieldsCache() {
		return this.columnFieldsCache;
	}*/

	public List<String> getHeaderColumnNames() {
		return getTableColumnsFields().stream().map(this::getHeaderColumnName).collect(Collectors.toList());
	}

	boolean isStrictMatch() {
		return getFieldsInfoCache().isStrictMatch();
	}

	public List<TableRow> getRows(String... valuesInPrimaryKeyColumnField) {
		List<TableRow> foundRows = new ArrayList<>(valuesInPrimaryKeyColumnField.length);
		for (String expectedValue : valuesInPrimaryKeyColumnField) {
			if (!this.primaryKeyColumnValuesAndRowIndexes.containsKey(expectedValue)) {
				//ExcelTable table = getFieldsInfoCache().ofTableField(getPrimaryKeyColumnField()).getExcelTable();
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

	public boolean isCaseIgnored(Field tableColumnField) {
		return getFieldsInfoCache().ofColumnField(tableColumnField).isCaseIgnored();
	}

	public String getHeaderColumnName(Field tableColumnField) {
		return getFieldsInfoCache().ofColumnField(tableColumnField).getHeaderColumnName();
	}

	public boolean isTableField(Field tableColumnField) {
		return getFieldsInfoCache().ofColumnField(tableColumnField).isTableField();
	}

	public Class<?> getTableFieldType(Field tableColumnField) {
		return getFieldsInfoCache().ofColumnField(tableColumnField).getTableFieldType();
	}

	public boolean hasObject(int rowIndex) {
		return this.rowIndexesAndCreatedObjects.containsKey(rowIndex);
		//return false;
	}

	public Object getObject(int rowIndex) {
		return this.rowIndexesAndCreatedObjects.get(rowIndex);
	}

	public void setObject(int rowIndex, Object object) {
		this.rowIndexesAndCreatedObjects.put(rowIndex, object);
	}

	private ExcelTable findTable() {
		ExcelTable table;
		List<Field> tableColumnsFields = getTableColumnsFields();

		ExcelSheet sheet = getFieldsInfoCache().getExcelManager().getSheet(getSheetName());
		boolean ignoreCase = isCaseIgnoredForAllColumns() || isCaseIgnoredInAnyColumnField();
		List<String> headerColumnNames = getHeaderColumnNames();
		if (getHeaderRowIndex() < 0) {
			table = sheet.getTable(ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
		} else {
			if (isStrictMatch()) {
				table = sheet.getTable(getHeaderRowIndex(), null, ignoreCase, headerColumnNames.toArray(new String[headerColumnNames.size()]));
			} else {
				table = sheet.getTable(getHeaderRowIndex());
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
					table.getSheetName(), getTableFieldType().getName(), extraTableColumnNames);
			if (isStrictMatch()) {
				throw new IstfException("Excel unmarshalling with strict match has been failed. " + message);
			}
			log.warn("{} Result object will not have missed field(s)", message);
			table.excludeColumns(extraTableColumnNames.toArray(new String[extraTableColumnNames.size()]));
		}

		return table;
	}

	private List<Field> filterTableColumnsFields(ExcelTable table, List<Field> tableColumnsFields) {
		List<Field> allTableColumnsFields = new ArrayList<>(tableColumnsFields);
		List<Field> missedTableColumnsFields = new ArrayList<>(allTableColumnsFields);
		for (Field columnField : allTableColumnsFields) {
			boolean ignoreCase = this.isCaseIgnoredForAllColumns || isCaseIgnored(columnField);
			if (table.hasColumn(getHeaderColumnName(columnField), ignoreCase)) {
				missedTableColumnsFields.remove(columnField);
			}
		}

		if (!missedTableColumnsFields.isEmpty()) {
			List<String> missedFieldColumnNames = new ArrayList<>(missedTableColumnsFields.size());
			for (Field f : missedTableColumnsFields) {
				StringBuilder missedTypeAndFieldName = new StringBuilder(f.getType().getSimpleName());
				if (isTableField(f)) {
					missedTypeAndFieldName.append("<").append(getTableFieldType(f).getSimpleName()).append(">");
				}
				missedFieldColumnNames.add(missedTypeAndFieldName.append(" ").append(getHeaderColumnName(f)).toString());
			}
			String message = String.format("Missed header column(s) detected in excel table on sheet \"%1$s\" for field(s) from class \"%2$s\": %3$s.",
					table.getSheet().getSheetName(), getTableFieldType().getName(), missedFieldColumnNames);

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
