package aaa.utils.excel.io.entity;

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
		super(row, table.getCellTypes());
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

	@Override
	public int getRowNumber() {
		return this.rowNumber;
	}

	int getRowNumberOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	@Override
	public List<TableCell> getCells() {
		if (tableCells == null) {
			tableCells = new HashMap<>();
			for (Map.Entry<Integer, ExcelCell> cellEntry : getCellsMap().entrySet()) {
				ExcelCell c = cellEntry.getValue();
				tableCells.put(cellEntry.getKey(), new TableCell(c.getPoiCell(), this, c.getCellTypes()));
			}
		}
		return new ArrayList<>(this.tableCells.values());
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowNumber=" + getRowNumber() +
				", values=" + getTableValues().entrySet() +
				'}';
	}

	public boolean hasColumnName(String headerColumnName) {
		return getTable().getHeader().hasColumnName(headerColumnName);
	}

	public Integer getColumnNumber(String headerColumnName) {
		return getTable().getHeader().getColumnNumber(headerColumnName);
	}

	public TableCell getCell(String headerColumnName) {
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

	public boolean getBoolValue(String headerColumnName) {
		return getCell(headerColumnName).getBoolValue();
	}

	public int getIntValue(String headerColumnName) {
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
}
