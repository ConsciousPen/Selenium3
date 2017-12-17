package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

public class TableHeader extends ExcelRow {
	private ExcelTable table;
	private Map<Integer, String> columnNames;


	public TableHeader(Row row, ExcelTable table) {
		super(row, ExcelCell.STRING_TYPE);
		this.table = table;
		this.columnNames = new HashMap<>();
		for (Map.Entry<Integer, ExcelCell> cellEntry : getCellsMap().entrySet()) {
			columnNames.put(cellEntry.getKey(), cellEntry.getValue().getStringValue());
		}
	}

	@Override
	public int getRowNumber() {
		return 0;
	}

	public List<String> getColumnNames() {
		return new ArrayList<>(columnNames.values());
	}

	int getRowNumberOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnNames() +
				'}';
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
}
