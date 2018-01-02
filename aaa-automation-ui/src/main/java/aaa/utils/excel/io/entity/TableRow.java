package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;

public class TableRow extends ExcelRow {
	private ExcelTable table;
	private int rowNumber;
	private Map<Integer, TableCell> tableCells;

	public TableRow(Row row, ExcelTable table, int rowNumber) {
		super(row, table.getSheet(), table.getCellTypes());
		this.table = table;
		this.rowNumber = rowNumber;
	}

	public ExcelTable getTable() {
		return table;
	}

	public Map<String, Object> getTableValues() {
		Map<String, Object> values = new HashMap<>(getSize());
		for (TableCell cell : getCells()) {
			values.put(cell.getHeaderColumnName(), cell.getValue());
		}
		return values;
	}

	public Map<String, String> getTableStringValues() {
		Map<String, String> values = new HashMap<>(getSize());
		for (TableCell cell : getCells()) {
			values.put(cell.getHeaderColumnName(), cell.getStringValue());
		}
		return values;
	}

	int getRowNumberOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	@Override
	public int getRowNumber() {
		return this.rowNumber;
	}

	@Override
	public List<TableCell> getCells() {
		return new ArrayList<>(getCellsMap().values());
	}

	@Override
	public void erase() {
		getTable().eraseRow(this);
	}

	@Override
	public void delete() {
		getTable().deleteRow(this);
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowNumber=" + getRowNumber() +
				", values=" + getTableValues().entrySet() +
				'}';
	}

	@Override
	public List<Integer> getColumnNumbers() {
		return getTable().getHeader().getColumnNumbers();
	}

	public List<String> getColumnNames() {
		return getTable().getHeader().getColumnNames();
	}

	public boolean hasColumnName(String headerColumnName) {
		return getTable().getHeader().hasColumnName(headerColumnName);
	}

	public Integer getColumnNumber(String headerColumnName) {
		return getTable().getHeader().getColumnNumber(headerColumnName);
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumnName(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCells().stream().filter(c -> c.getHeaderColumnName().equals(headerColumnName)).findFirst().get();
	}

	public List<TableCell> getCellsContains(String headerColumnNamePattern) {
		return getCells().stream().filter(c -> c.getHeaderColumnName().contains(headerColumnNamePattern)).collect(Collectors.toList());
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

	public boolean hasCell(String headerColumnName) {
		return getTable().getHeader().hasColumnName(headerColumnName);
	}

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return getCell(headerColumnName).hasValue(expectedValue);
	}

	void excludeColumn(int columnNumber) {
		getCellsMap().remove(columnNumber);
	}

	private Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new HashMap<>();
			for (int columnNumber : getColumnNumbers()) {
				this.tableCells.put(columnNumber, new TableCell(getPoiRow().getCell(columnNumber - 1), this, columnNumber));
			}
		}
		return this.tableCells;
	}
}
