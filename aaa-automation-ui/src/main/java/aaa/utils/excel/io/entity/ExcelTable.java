package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public class ExcelTable implements Iterable<TableRow> {

	private TableHeader header;
	private List<TableRow> rows;
	private int rowsNumber;
	private Set<CellType<?>> cellTypes;

	public ExcelTable(TableHeader header) {
		this(header, header.getSheet().getLastRowNum() - header.getRowNumberOnSheet() + 1, header.getExcelRow().getCellTypes());
	}

	/**
	 * Get ExcelTable object with {@link TableHeader} and {@code rowsNumber} number of rows
	 *
	 * @param header {@link TableHeader} object of this table
	 * @param rowsNumber number of table's rows excluding header row, should be positive number
	 */
	public ExcelTable(TableHeader header, int rowsNumber, Set<CellType<?>> cellTypes) {
		assertThat(header).as("TableHeader should not be null").isNotNull();
		assertThat(rowsNumber).as("Number of table's rows should be greater than 0").isPositive();
		assertThat(cellTypes).as("Cell Types set should not be empty or null").isNotEmpty().isNotNull();
		this.header = header;
		this.rowsNumber = rowsNumber;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(cellTypes);
	}

	public TableHeader getHeader() {
		return header;
	}

	public Sheet getSheet() {
		return header.getSheet();
	}

	public List<TableRow> getRows() {
		if (rows == null) {
			this.rows = new ArrayList<>(rowsNumber);
			for (int rowNumber = 1; rowNumber <= rowsNumber; rowNumber++) {
				Row row = getHeader().getSheet().getRow(getHeader().getRowNumberOnSheet() + rowNumber - 1);
				rows.add(new TableRow(row, this, rowNumber));
			}
		}
		return Collections.unmodifiableList(this.rows);
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
		return new TableRowIterator(getRowsNumber());
	}

	@Override
	public String toString() {
		return "ExcelTable{" +
				"header=" + header +
				", rowsNumber=" + getRowsNumber() +
				", tableRows=" + getRows() +
				'}';
	}

	public boolean hasRow(int rowNumber) {
		return getRows().stream().anyMatch(r -> r.getRowNumber() == rowNumber);
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
		assertThat(foundRows).as("There are no rows in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName).isNotEmpty();
		return foundRows;
	}

	public TableRow getRow(String headerColumnName, Object cellValue) {
		return getRows(headerColumnName, cellValue).get(0);
	}

	public List<TableRow> getRows(Map<String, Object> query) {
		List<TableRow> foundRows = new ArrayList<>(getRows());
		for (Map.Entry<String, Object> columnNameAndCellValue : query.entrySet()) {
			foundRows.removeIf(r -> !r.hasValue(columnNameAndCellValue.getKey(), columnNameAndCellValue.getValue()));
		}
		assertThat(foundRows).as("There are no rows in table with column names and cell values query: " + query.entrySet()).isNotEmpty();
		return foundRows;
	}

	public TableRow getRow(Map<String, Object> query) {
		return getRows(query).get(0);
	}

	class TableRowIterator implements Iterator<TableRow> {
		private int currentIndex;
		private int endIndex;

		TableRowIterator(int rowsNumber) {
			this.currentIndex = 1;
			this.endIndex = rowsNumber;
		}

		@Override
		public boolean hasNext() {
			return currentIndex <= endIndex;
		}

		@Override
		public TableRow next() {
			if (!hasNext()) {
				throw new NoSuchElementException("ExcelTable does not have next TableRow");
			}
			TableRow returnRow = getRow(currentIndex);
			currentIndex++;
			return returnRow;
		}
	}
}
