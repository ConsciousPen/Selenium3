package aaa.utils.excel.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Sheet;
import toolkit.exceptions.IstfException;

public class ExcelTable implements Iterable<TableRow> {

	private TableHeader header;
	private List<TableRow> tableRows;
	private int rowsNumber;

	public ExcelTable(TableHeader header) {
		this(header, header.getSheet().getLastRowNum() - header.getRowNumberOnSheet() + 1);
	}

	/**
	 * Get ExcelTable object with {@link TableHeader} and {@code rowsNumber} number of rows
	 *
	 * @param header {@link TableHeader} object of this table
	 * @param rowsNumber number of table's rows excluding header row, should be positive number
	 */
	public ExcelTable(TableHeader header, int rowsNumber) {
		assertThat(rowsNumber).as("Number of table's rows should be greater than 0").isPositive();
		this.header = header;
		this.rowsNumber = rowsNumber;
	}

	public TableHeader getHeader() {
		return header;
	}

	public Sheet getSheet() {
		return header.getSheet();
	}

	public List<TableRow> getRows() {
		if (tableRows == null) {
			tableRows = new ArrayList<>(rowsNumber);
			for (int rowNumber = 1; rowNumber <= rowsNumber; rowNumber++) {
				tableRows.add(new TableRow(this, rowNumber));
			}
		}
		return Collections.unmodifiableList(tableRows);
	}

	/**
	 * Get table's rows number without header row
	 */
	public int getRowsNumber() {
		return rowsNumber;
	}

	@Override
	@Nonnull
	public Iterator<TableRow> iterator() {
		return new TableRowIterator(rowsNumber);
	}

	@Override
	public String toString() {
		return "ExcelTable{" +
				"header=" + header +
				", rowsNumber=" + getRowsNumber() +
				", tableRows=" + tableRows +
				'}';
	}

	public boolean hasRow(int rowNumber) {
		return getRows().stream().anyMatch(tRow -> tRow.getRowNumber() == rowNumber);
	}

	/**
	 * Get {@link TableRow} object by table's row number. Index starts from 1 (0 belongs to header)
	 */
	public TableRow getRow(int rowNumber) {
		return getRows().stream().filter(r -> r.getRowNumber() == rowNumber).findFirst()
				.orElseThrow(() -> new IstfException("There is no row number " + rowNumber + " in table"));
	}

	public List<TableRow> getRows(String headerColumnName, Object cellValue) {
		List<TableRow> foundRows = getRows().stream().filter(r -> r.hasValue(headerColumnName, cellValue)).collect(Collectors.toList());
		if (foundRows.isEmpty()) {
			throw new IstfException(String.format("There are no rows in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName));
		}
		return foundRows;
	}

	public TableRow getRow(String headerColumnName, Object cellValue) {
		return getRows(headerColumnName, cellValue).get(0);
	}

	public List<TableRow> getRows(Map<String, Object> query) {
		List<TableRow> foundRows = getRows();
		for (Map.Entry<String, Object> columnNameAndCellValue : query.entrySet()) {
			foundRows.removeIf(r -> !r.hasValue(columnNameAndCellValue.getKey(), columnNameAndCellValue.getValue()));
		}
		if (foundRows.isEmpty()) {
			throw new IstfException("There are no rows in table with column names and cell values query: " + query.entrySet());
		}
		return foundRows;
	}

	public TableRow getRow(Map<String, Object> query) {
		return getRows(query).get(0);
	}

	class TableRowIterator implements Iterator<TableRow> {
		private int currentIndex;
		private int endIndex;

		TableRowIterator(int endIndex) {
			this(0, endIndex);
		}

		TableRowIterator(int startIndex, int endIndex) {
			this.currentIndex = startIndex;
			this.endIndex = endIndex;
		}

		@Override
		public boolean hasNext() {
			return currentIndex < endIndex && getRows().get(currentIndex) != null;
		}

		@Override
		public TableRow next() {
			if (!hasNext()) {
				throw new NoSuchElementException("ExcelTable does not have next TableRow");
			}
			TableRow returnRow = getRows().get(currentIndex);
			currentIndex++;
			return returnRow;
		}
	}
}
