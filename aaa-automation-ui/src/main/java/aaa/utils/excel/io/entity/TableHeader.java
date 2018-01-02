package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;

public class TableHeader extends ExcelRow {
	private ExcelTable table;
	private Map<Integer, String> columnNames;

	public TableHeader(Row row, ExcelTable table) {
		super(row, table.getSheet());
		this.table = table;
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	@Override
	public List<Integer> getColumnNumbers() {
		return new ArrayList<>(getColumnNamesMap().keySet());
	}

	public List<String> getColumnNames() {
		return new ArrayList<>(getColumnNamesMap().values());
	}

	int getRowNumberOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	public ExcelTable getTable() {
		return table;
	}

	@Override
	public int getRowNumber() {
		return 0;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnNames() +
				'}';
	}

	@Override
	public Boolean getBoolValue(int columnNumber) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public Integer getIntValue(int columnNumber) {
		throw new UnsupportedOperationException("Table header cells don't have int values");
	}

	@Override
	public LocalDateTime getDateValue(int columnNumber) {
		throw new UnsupportedOperationException("Table header cells don't have LocalDateTime values");
	}

	@Override
	public <R extends ExcelRow> R registerCellType(CellType<?>... cellTypes) {
		throw new UnsupportedOperationException("Table header cell types should not be updated");
	}

	@Override
	public void erase() {
		throw new UnsupportedOperationException("Table header erasing is not supported");
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException("Table header deleting is not supported");
	}

	public boolean hasColumnName(String columnName) {
		return getColumnNames().contains(columnName);
	}

	public int getColumnNumber(String columnName) {
		assertThat(hasColumnName(columnName)).as("There is no column name \"%s\" in the table's header", columnName).isTrue();
		return getColumnNamesMap().entrySet().stream().filter(c -> c.getValue().equals(columnName)).findFirst().get().getKey();
	}

	public String getColumnName(int columnNumber) {
		assertThat(hasColumn(columnNumber)).as("There is no column with %s index in table's header", columnNumber).isTrue();
		return getColumnNamesMap().get(columnNumber);
	}

	public void excludeColumns(String... columnNames) {
		getTable().excludeColumns(columnNames);
	}

	void excludeColumn(int columnNumber) {
		this.columnNames.remove(columnNumber);
	}

	private Map<Integer, String> getColumnNamesMap() {
		if (this.columnNames == null) {
			this.columnNames = new HashMap<>();
			for (ExcelCell cell : getCells()) {
				if (!cell.isEmpty()) {
					this.columnNames.putIfAbsent(cell.getColumnNumber(), cell.getStringValue());
				}
			}
		}
		return this.columnNames;
	}
}
