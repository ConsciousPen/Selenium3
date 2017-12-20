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

	public TableHeader(Row row, ExcelSheet sheet, ExcelTable table) {
		super(row, sheet, ExcelCell.STRING_TYPE);
		this.table = table;
		this.columnNames = new HashMap<>();
		for (Map.Entry<Integer, ExcelCell> cellEntry : getCellsMap().entrySet()) {
			columnNames.put(cellEntry.getKey(), cellEntry.getValue().getStringValue());
		}
	}

	public List<String> getColumnNames() {
		return new ArrayList<>(columnNames.values());
	}

	int getRowNumberOnSheet() {
		return getPoiRow().getRowNum() + 1;
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
	public boolean getBoolValue(int columnNumber) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public int getIntValue(int columnNumber) {
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
		return columnNames.entrySet().stream().filter(c -> c.getValue().equals(columnName)).findFirst().get().getKey();
	}

	public String getColumnName(int columnNumber) {
		assertThat(hasColumn(columnNumber)).as("There is no column with %s index in table's header", columnNumber).isTrue();
		return this.columnNames.get(columnNumber);
	}
}
