package aaa.utils.excel.parse.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.ExcelParser;
import toolkit.exceptions.IstfException;

public class TableHeader {
	private Map<String, Integer> headerColumns;
	private Row row;

	public TableHeader(Row row) {
		this(row, 1);
	}

	public TableHeader(Row row, int fromColumnNumber) {
		this(row, fromColumnNumber, row.getLastCellNum() - fromColumnNumber + 2);
	}

	/**
	 * Get TableHeader object
	 *
	 * @param row {@link Row} object of table's header
	 * @param fromColumnNumber first header's column number on sheet, index starts from 1
	 * @param headerSize number of cells in header, should be positive
	 */
	public TableHeader(Row row, int fromColumnNumber, int headerSize) {
		assertThat(row).as("Row should not be null").isNotNull();
		assertThat(fromColumnNumber).isPositive().as("First header's column number on sheet should be greater than 0");
		assertThat(headerSize).isPositive().as("Header size should be greater than 0");
		this.row = row;
		this.headerColumns = new HashMap<>();

		ExcelParser excelParser = new ExcelParser(row.getSheet());
		for (int columnNumber = fromColumnNumber; columnNumber <= fromColumnNumber + headerSize - 1; columnNumber++) {
			String value = excelParser.getStringValue(row, columnNumber);
			if (value != null) {
				this.headerColumns.put(value, columnNumber);
			}
		}
	}

	public Set<String> getColumnNames() {
		return headerColumns.keySet();
	}

	public int getSize() {
		return headerColumns.size();
	}

	int getRowNumberOnSheet() {
		return row.getRowNum() + 1;
	}

	Sheet getSheet() {
		return row.getSheet();
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnNames() +
				'}';
	}

	public boolean hasColumnName(String columnName) {
		return headerColumns.containsKey(columnName);
	}

	public int getColumnNumber(String columnName) {
		if (!hasColumnName(columnName)) {
			throw new IstfException(String.format("There is no column name \"%s\" in the table's header", columnName));
		}
		return headerColumns.get(columnName);
	}

	public String getColumnName(int columnNumber) {
		return headerColumns.entrySet().stream().filter(hc -> hc.getValue() == columnNumber).findFirst().map(Map.Entry::getKey)
				.orElseThrow(() -> new IstfException(String.format("There is no column number %s in the table's header", columnNumber)));
	}
}
