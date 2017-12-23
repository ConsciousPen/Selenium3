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

	void excludeColumns(String... columnNames) {
		for (String column : columnNames) {
			this.columnNames.remove(getColumnNumber(column));
		}
	}

	private Map<Integer, String> getColumnNamesMap() {
		if (this.columnNames == null) {
			this.columnNames = new HashMap<>();
			for (ExcelCell cell : getCells()) {
				this.columnNames.putIfAbsent(cell.getColumnNumber(), cell.getStringValue());
			}
		}
		return this.columnNames;
	}
}
