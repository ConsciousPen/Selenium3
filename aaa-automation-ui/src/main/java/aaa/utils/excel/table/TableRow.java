package aaa.utils.excel.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.ss.usermodel.Row;
import toolkit.exceptions.IstfException;

public class TableRow {
	private ExcelTable table;
	private TableHeader header;
	private Row row;
	private int rowNumber;
	//private ExcelParser excelParser;
	private List<TableCell> tableCells;

	public TableRow(ExcelTable table, int rowNumber) {
		assertThat(rowNumber).isPositive().as("Table row number should be greater than 0");
		this.table = table;
		this.header = table.getHeader();
		this.rowNumber = rowNumber;
		this.row = header.getSheet().getRow(header.getRowNumberOnSheet() + rowNumber - 1);
		//this.excelParser = new ExcelParser(header.getSheet());
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

	public int getRowNumberOnSheet() {
		return getRow().getRowNum() + 1;
	}

	public List<TableCell> getCells() {
		if (tableCells == null) {
			tableCells = new ArrayList<>(getHeader().getSize());
			for (String columnName : getHeader().getColumnNames()) {
				tableCells.add(new TableCell(this, getHeader().getColumnNumber(columnName)));
			}
		}
		return Collections.unmodifiableList(this.tableCells);
	}

	public Map<String, Object> getValues() {
		Map<String, Object> values = new HashMap<>(getHeader().getSize());
		for (TableCell cell : this) {
			values.put(cell.getColumnName(), cell.getValue());
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

	public String getStringValue(String headerColumnName) {
		return getCell(headerColumnName).getStringValue();
		//return excelParser.getStringValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}

	private TableCell getCell(String headerColumnName) {
		return getCells().stream().filter(c -> c.getColumnName().equals(headerColumnName)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no cell with \"%s\" column name in the table's header", headerColumnName)));
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
			actualCellValue = getStringValue(headerColumnName);
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

	Row getRow() {
		return this.row;
	}
}
