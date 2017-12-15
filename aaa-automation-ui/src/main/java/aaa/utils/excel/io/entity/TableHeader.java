package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Sheet;

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
		assertThat(fromColumnNumber).as("First header's column number on sheet should be greater than 0").isPositive();
		assertThat(headerSize).as("Header size should be greater than 0").isPositive();
		assertThat(excelRow.getCellTypes().contains(ExcelCell.STRING_TYPE)).as("Header's row cell types should contain at least %s cell type", ExcelCell.STRING_TYPE).isTrue();

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
		return new HashSet<>(headerColumns.keySet());
	}

	public Set<Integer> getColumnIndexes() {
		return new HashSet<>(headerColumns.values());
	}

	public int getSize() {
		return headerColumns.size();
	}

	public ExcelRow getExcelRow() {
		return excelRow;
	}

	int getRowNumberOnSheet() {
		return getExcelRow().getRowNumber();
	}

	Sheet getSheet() {
		return excelRow.getPoiRow().getSheet();
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

	public boolean hasColumnIndex(int columnIndex) {
		return getColumnIndexes().contains(columnIndex);
	}

	public int getColumnIndex(String columnName) {
		assertThat(hasColumnName(columnName)).as("There is no column name \"%s\" in the table's header", columnName).isTrue();
		return headerColumns.get(columnName);
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasColumnIndex(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getColumnNames().stream().filter(cn -> getColumnIndex(cn) == columnIndex).findFirst().get();
	}
}
