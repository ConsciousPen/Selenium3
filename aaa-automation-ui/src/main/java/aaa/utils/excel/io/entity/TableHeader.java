package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Sheet;
import toolkit.exceptions.IstfException;

public class TableHeader {
	private Map<String, Integer> headerColumns;
	private ExcelRow excelRow;

	public TableHeader(ExcelRow excelRow) {
		this(excelRow, 1);
	}

	public TableHeader(ExcelRow excelRow, int fromColumnNumber) {
		this(excelRow, fromColumnNumber, excelRow.getLastCellNum() - fromColumnNumber + 1);
	}

	/**
	 * Get TableHeader object
	 *
	 * @param excelRow {@link ExcelRow} object of table's header
	 * @param fromColumnNumber first header's column number on sheet, index starts from 1
	 * @param headerSize number of cells in header, should be positive
	 */
	public TableHeader(ExcelRow excelRow, int fromColumnNumber, int headerSize) {
		assertThat(excelRow).as("Row should not be null").isNotNull();
		assertThat(fromColumnNumber).isPositive().as("First header's column number on sheet should be greater than 0");
		assertThat(headerSize).isPositive().as("Header size should be greater than 0");
		this.excelRow = excelRow;
		this.headerColumns = new HashMap<>();

		for (int columnNumber = fromColumnNumber; columnNumber <= fromColumnNumber + headerSize - 1; columnNumber++) {
			String value = excelRow.getStringValue(columnNumber);
			if (value != null) {
				this.headerColumns.put(value, columnNumber);
			}
		}
	}

	public Set<String> getColumnNames() {
		return headerColumns.keySet();
	}

	public Set<Integer> getColumnIndexes() {
		return (Set<Integer>) headerColumns.values();
	}

	public int getSize() {
		return headerColumns.size();
	}

	int getRowNumberOnSheet() {
		return excelRow.getRow().getRowNum() + 1;
	}

	Sheet getSheet() {
		return excelRow.getRow().getSheet();
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

	public int getColumnIndex(String columnName) {
		assertThat(hasColumnName(columnName)).as("There is no column name \"%s\" in the table's header", columnName);
		return headerColumns.get(columnName);
	}

	public String getColumnName(int columnIndex) {
		return headerColumns.entrySet().stream().filter(hc -> hc.getValue() == columnIndex).findFirst().map(Map.Entry::getKey)
				.orElseThrow(() -> new IstfException(String.format("There is no column index %s in the table's header", columnIndex)));
	}
}
