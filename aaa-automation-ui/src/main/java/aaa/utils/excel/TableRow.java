package aaa.utils.excel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Row;
import toolkit.exceptions.IstfException;

public class TableRow {
	private ExcelTable table;
	private TableHeader header;
	private Row row;
	private int rowNumber;
	private ExcelParser excelParser;

	public TableRow(ExcelTable table, int rowNumber) {
		this.table = table;
		this.header = table.getHeader();
		this.rowNumber = rowNumber;
		this.row = header.getSheet().getRow(header.getRowNumberOnSheet() + rowNumber);
		this.excelParser = new ExcelParser(header.getSheet());
	}

	public ExcelTable getTable() {
		return table;
	}

	public TableHeader getHeader() {
		return header;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public Map<String, String> getValues() {
		Map<String, String> values = new HashMap<>(getHeader().getSize());
		for (String columnName : getHeader().getColumnNames()) {
			values.put(columnName, getValue(columnName));
		}
		return values;
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowNumber=" + rowNumber +
				", values=" + getValues().entrySet() +
				'}';
	}

	public String getValue(String headerColumnName) {
		return excelParser.getValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}

	public boolean getBoolValue(String headerColumnName) {
		return excelParser.getBoolValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}

	public int getIntValue(String headerColumnName) {
		return excelParser.getIntValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}

	public LocalDateTime getDateValue(String headerColumnName) {
		return excelParser.getDateValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}

	public boolean hasValue(String headerColumnName, Object cellValue) {
		Object actualCellValue;
		if (cellValue instanceof String) {
			actualCellValue = getValue(headerColumnName);
		} else if (cellValue instanceof Integer) {
			actualCellValue = getIntValue(headerColumnName);
		} else if (cellValue instanceof Boolean) {
			actualCellValue = getBoolValue(headerColumnName);
		} else if (cellValue instanceof LocalDateTime) {
			actualCellValue = getDateValue(headerColumnName);
		} else {
			throw new IstfException("Unsupported cell value type class: " + cellValue.getClass().getSimpleName());
		}
		return Objects.equals(actualCellValue, cellValue);
	}

	private Row getRow() {
		return this.row;
	}
}
