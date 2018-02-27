package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class TableRow extends ExcelRow<TableCell> {
	public TableRow(Row row, int rowIndexInTable, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelTable table) {
		this(row, rowIndexInTable, rowIndexOnSheet, columnsIndexesOnSheet, table, table.getCellTypes());
	}

	public TableRow(Row row, int rowIndexInTable, int rowIndexOnSheet, Set<Integer> columnsIndexesOnSheet, ExcelTable table, Set<CellType<?>> cellTypes) {
		super(row, rowIndexInTable, rowIndexOnSheet, columnsIndexesOnSheet, table, cellTypes);
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public Map<String, Object> getTableValues() {
		Map<String, Object> values = new LinkedHashMap<>(getCellsNumber());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getValue());
		}
		return values;
	}

	public Map<String, String> getTableStringValues() {
		Map<String, String> values = new LinkedHashMap<>(getCellsNumber());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getStringValue());
		}
		return values;
	}

	@Override
	protected Map<Integer, TableCell> gatherQueueIndexesAndCellsMap(Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, TableCell> columnsIndexesAndCellsMap = new LinkedHashMap<>(columnsIndexesOnSheet.size());
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndexOnSheet - 1) : null;
			TableCell tableCell = new TableCell(poiCell, columnIndexInTable, columnIndexOnSheet, this, cellTypes);
			columnsIndexesAndCellsMap.put(columnIndexInTable, tableCell);
			columnIndexInTable++;
		}
		return columnsIndexesAndCellsMap;
	}

	@Override
	public int getIndexOnSheet() {
		return super.getIndexOnSheet();
	}

	@Override
	public List<Integer> getCellsIndexesOnSheet() {
		return super.getCellsIndexesOnSheet();
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"sheetName=" + getSheetName() +
				", rowIndex=" + getIndex() +
				", columnsNumber=" + getCellsNumber() +
				", cellTypes=" + getCellTypes() +
				", values=" + getTableStringValues() +
				'}';
	}

	public boolean hasColumn(String headerColumnName) {
		return hasColumn(headerColumnName, false);
	}

	public boolean hasColumn(String headerColumnName, boolean ignoreCase) {
		return getTable().getHeader().hasColumn(headerColumnName, ignoreCase);
	}

	public int getIndex(String headerColumnName) {
		return getIndex(headerColumnName, false);
	}

	public int getIndex(String headerColumnName, boolean ignoreCase) {
		return getTable().getHeader().getColumnIndex(headerColumnName);
	}

	public int getIndexOnSheet(String headerColumnName) {
		return getIndexOnSheet(headerColumnName, false);
	}

	public int getIndexOnSheet(String headerColumnName, boolean ignoreCase) {
		return getTable().getHeader().getColumnIndexOnSheet(headerColumnName);
	}

	public String getColumnName(int columnIndex) {
		return getTable().getHeader().getColumnName(columnIndex);
	}

	public List<TableCell> getCellsContains(String headerColumnNamePattern) {
		return getCells().stream().filter(c -> c.getHeaderColumnName().contains(headerColumnNamePattern)).collect(Collectors.toList());
	}

	public TableCell getCell(String headerColumnName) {
		return getCell(headerColumnName, false);
	}

	public TableCell getCell(String headerColumnName, boolean ignoreCase) {
		assertThat(hasColumn(headerColumnName, ignoreCase)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getTable().getColumn(headerColumnName, ignoreCase).getCell(getIndex());
	}

	public Object getValue(String headerColumnName) {
		return getValue(getIndex(headerColumnName));
	}

	public String getStringValue(String headerColumnName) {
		return getStringValue(getIndex(headerColumnName));
	}

	public Boolean getBoolValue(String headerColumnName) {
		return getBoolValue(getIndex(headerColumnName));
	}

	public Integer getIntValue(String headerColumnName) {
		return getIntValue(getIndex(headerColumnName));
	}

	public Double getDoubleValue(String headerColumnName) {
		return getDoubleValue(getIndex(headerColumnName));
	}

	public LocalDateTime getDateValue(String headerColumnName, DateTimeFormatter... formatters) {
		return getDateValue(getIndex(headerColumnName), formatters);
	}

	public TableRow setValue(String headerColumnName, Object value) {
		return (TableRow) setValue(getIndex(headerColumnName), value);
	}

	public <T> TableRow setValue(String headerColumnName, T value, CellType<T> valueType) {
		return (TableRow) setValue(getIndex(headerColumnName), value, valueType);
	}

	public boolean hasValue(String headerColumnName, Object expectedValue, DateTimeFormatter... formatters) {
		return hasValue(headerColumnName, false, expectedValue, formatters);
	}

	public boolean hasValue(String headerColumnName, boolean ignoreHeaderColumnNameCase, Object expectedValue, DateTimeFormatter... formatters) {
		return hasValue(getIndex(headerColumnName, ignoreHeaderColumnNameCase), expectedValue, formatters);
	}

	public boolean isEmpty(String headerColumnName) {
		return isEmpty(headerColumnName, false);
	}

	public boolean isEmpty(String headerColumnName, boolean ignoreCase) {
		return isEmpty(getIndex(headerColumnName, ignoreCase));
	}

	public int getSum(String... headerColumnNames) {
		List<Integer> indexes = getCellsIndexes();
		return getSum(indexes.toArray(new Integer[indexes.size()]));
	}

	public int getSumContains(String headerColumnNamePattern) {
		return getSum(getCellsContains(headerColumnNamePattern).stream().map(TableCell::getColumnIndex).toArray(Integer[]::new));
	}
}
