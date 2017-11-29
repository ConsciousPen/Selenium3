package aaa.utils.excel;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Row;

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
		this.row = header.getSheet().getRow(header.getRowNum() + rowNumber);
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
		for (String columnName : getHeader().getHeaderNames()) {
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

	private Row getRow() {
		return this.row;
	}
}
