package aaa.utils.excel.io.entity.area.table;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.iterator.CellIterator;
import aaa.utils.excel.io.entity.queue.ExcelRow;

public class TableRow extends ExcelRow implements Iterable<TableCell> {
	protected Map<Integer, TableCell> tableCells;
	private int tableRowIndex;

	public TableRow(Row row, int tableRowIndex, int rowIndex, ExcelTable table) {
		super(row, rowIndex, table);
		this.tableRowIndex = tableRowIndex;
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public TableHeader getHeader() {
		return getTable().getHeader();
	}

	public Map<String, Object> getTableValues() {
		Map<String, Object> values = new LinkedHashMap<>(getSize());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getValue());
		}
		return values;
	}

	public Map<String, String> getTableStringValues() {
		Map<String, String> values = new LinkedHashMap<>(getSize());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getStringValue());
		}
		return values;
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new LinkedHashMap<>(getTable().getColumnsMap().size());
			for (int i = 0; i < getTable().getColumnsIndexes().size(); i++) {
				int tableCellIndex = getTable().getColumnsIndexes().get(i);
				int sheetCellIndex = getTable().getColumnsIndexesOnSheet().get(i);
				Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(sheetCellIndex - 1) : null;
				TableCell tableCell = new TableCell(poiCell, this, tableCellIndex, sheetCellIndex);
				this.tableCells.put(tableCellIndex, tableCell);
			}
		}
		return this.tableCells;
	}

	int getIndexOnSheet() {
		return this.index;
	}

	@Override
	public int getIndex() {
		return this.tableRowIndex;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<TableCell> iterator() {
		return (Iterator<TableCell>) new CellIterator(this);
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowIndex=" + getIndex() +
				", values=" + getTableValues() +
				'}';
	}

	public boolean hasColumn(String headerColumnName) {
		return getHeader().hasColumn(headerColumnName);
	}

	public int getIndex(String headerColumnName) {
		return getHeader().getColumnIndex(headerColumnName);
	}

	public int getIndexOnSheet(String headerColumnName) {
		return getHeader().getColumnIndexOnSheet(headerColumnName);
	}

	public String getColumnName(int columnIndex) {
		return getHeader().getColumnName(columnIndex);
	}

	@SuppressWarnings("unchecked")
	public List<TableCell> getCellsContains(String headerColumnNamePattern) {
		return (List<TableCell>) getCells().stream().filter(c -> ((TableCell) c).getHeaderColumnName().contains(headerColumnNamePattern)).collect(Collectors.toList());
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return (TableCell) getCells().stream().filter(c -> ((TableCell) c).getHeaderColumnName().equals(headerColumnName)).findFirst().get();
	}

	public List<TableCell> getCells(String... headerColumnNames) {
		return Arrays.stream(headerColumnNames).map(this::getCell).collect(Collectors.toList());
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

	public boolean hasValue(String headerColumnName, Object expectedValue, DateTimeFormatter... formatters) {
		return hasValue(getIndex(headerColumnName), expectedValue, formatters);
	}

	public boolean isEmpty(String columnName) {
		return isEmpty(getIndex(columnName));
	}

	public int getSum(String... headerColumnNames) {
		List<Integer> indexes = getCellsIndexes();
		return getSum(indexes.toArray(new Integer[indexes.size()]));
	}

	public int getSumContains(String headerColumnNamePattern) {
		return getSum(getCellsContains(headerColumnNamePattern).stream().mapToInt(TableCell::getColumnIndex).boxed().toArray(Integer[]::new));
	}
}
