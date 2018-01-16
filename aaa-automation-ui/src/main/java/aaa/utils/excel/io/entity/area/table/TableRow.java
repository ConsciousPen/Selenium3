package aaa.utils.excel.io.entity.area.table;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
		return getCell(headerColumnName).getValue();
	}

	public String getStringValue(String headerColumnName) {
		return getCell(headerColumnName).getStringValue();
	}

	public Boolean getBoolValue(String headerColumnName) {
		return getCell(headerColumnName).getBoolValue();
	}

	public Integer getIntValue(String headerColumnName) {
		return getCell(headerColumnName).getIntValue();
	}

	public LocalDateTime getDateValue(String headerColumnName) {
		return getCell(headerColumnName).getDateValue();
	}

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return Objects.equals(getCell(headerColumnName).getValue(), expectedValue);
	}

	public int getSum(String... headerColumnNames) {
		return getSum(getCells(headerColumnNames).stream().mapToInt(TableCell::getColumnIndex).boxed().toArray(Integer[]::new));
	}

	public int getSumContains(String headerColumnNamePattern) {
		return getSum(getCellsContains(headerColumnNamePattern).stream().mapToInt(TableCell::getColumnIndex).boxed().toArray(Integer[]::new));
	}

	public boolean isEmpty(String columnName) {
		return getCell(columnName).isEmpty();
	}
}
