package aaa.utils.excel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
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
		if (!hasRow(rowNumber)) {
			throw new IstfException("There is no row in table with number: " + rowNumber);
		}

		TableRow foundRow = null;
		for (TableRow row : getRows()) {
			foundRow = row;
			if (rowNumber == row.getRowNumber()) {
				break;
			}
		}
		return foundRow;
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
