package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;

public class TableHeader extends ExcelRow {
	private ExcelTable table;
	private Map<Integer, String> columnNames;

	public TableHeader(Row row, Set<Integer> columnIndexes, ExcelTable table) {
		super(row, 0, columnIndexes, table.getSheet(), table.getCellTypes());
		this.table = table;
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	public List<String> getColumnsNames() {
		return new ArrayList<>(getColumnNamesMap().values());
	}

	int getRowIndexOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	public ExcelTable getTable() {
		return table;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	@Override
	public Boolean getBoolValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public Integer getIntValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have int values");
	}

	@Override
	public LocalDateTime getDateValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have LocalDateTime values");
	}

	@Override
	public <R extends ExcelRow> R registerCellType(CellType<?>... cellTypes) {
		throw new UnsupportedOperationException("Table header cell types should not be updated");
	}

	@Override
	public <R extends ExcelRow> R erase() {
		throw new UnsupportedOperationException("Table header erasing is not supported");
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException("Table header deleting is not supported");
	}

	public boolean hasColumnName(String columnName) {
		return getColumnsNames().contains(columnName);
	}

	public int getColumnIndex(String columnName) {
		assertThat(hasColumnName(columnName)).as("There is no column name \"%s\" in the table's header", columnName).isTrue();
		return getColumnNamesMap().entrySet().stream().filter(c -> c.getValue().equals(columnName)).findFirst().get().getKey();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getColumnNamesMap().get(columnIndex);
	}

	public void excludeColumns(String... columnNames) {
		getTable().excludeColumns(columnNames);
	}

	void excludeColumn(int columnIndex) {
		this.columnNames.remove(columnIndex);
	}

	private Map<Integer, String> getColumnNamesMap() {
		if (this.columnNames == null) {
			this.columnNames = new HashMap<>();
			for (ExcelCell cell : getCells()) {
				if (!cell.isEmpty()) {
					this.columnNames.putIfAbsent(cell.getColumnIndex(), cell.getStringValue());
				}
			}
		}
		return this.columnNames;
	}
}
