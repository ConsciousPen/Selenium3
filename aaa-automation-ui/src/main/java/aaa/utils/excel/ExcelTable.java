package aaa.utils.excel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Sheet;
import toolkit.exceptions.IstfException;

public class ExcelTable implements Iterable<TableRow> {

	private TableHeader header;
	private List<TableRow> tableRows;
	private int rowsNumber;

	public ExcelTable(TableHeader header) {
		this(header, header.getSheet().getLastRowNum() - header.getRowNum());
	}

	public ExcelTable(TableHeader header, int rowsNumber) {
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
		return tableRows;
	}

	/**
	 * Without header row
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
	 * Starts from 1st index (0 belongs to header)
	 */
	public TableRow getRow(int rowNumber) {
		return getRows().stream().filter(r -> r.getRowNumber() == rowNumber).findFirst()
				.orElseThrow(() -> new IstfException("There is no row in table with number: " + rowNumber));
	}

	public TableRow getRow(String headerColumnName, String cellValue) {
		return getRows().stream().filter(r -> Objects.equals(r.getValue(headerColumnName), cellValue)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no row in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName)));
	}

	public TableRow getRow(String headerColumnName, int cellValue) {
		return getRows().stream().filter(r -> Objects.equals(r.getIntValue(headerColumnName), cellValue)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no row in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName)));
	}

	public TableRow getRow(String headerColumnName, boolean cellValue) {
		return getRows().stream().filter(r -> Objects.equals(r.getBoolValue(headerColumnName), cellValue)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no row in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName)));
	}

	public TableRow getRow(String headerColumnName, LocalDateTime cellValue) {
		return getRows().stream().filter(r -> Objects.equals(r.getDateValue(headerColumnName), cellValue)).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no row in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName)));
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
