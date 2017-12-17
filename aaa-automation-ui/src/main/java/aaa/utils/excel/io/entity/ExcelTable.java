package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;

public class ExcelTable implements Iterable<TableRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelTable.class);

	private Row headerRow;
	private int rowsNumber;
	private CellType<?>[] cellTypes;
	private TableHeader header;
	private List<TableRow> rows;
	private Set<Integer> nonTableColumnNumbers;

	public ExcelTable(Row headerRow) {
		this(headerRow, ExcelCell.getBaseTypes());
	}

	public ExcelTable(Row headerRow, CellType<?>... cellTypes) {
		//TODO: get last row in table instead of last row on sheet
		this(headerRow, headerRow.getSheet().getLastRowNum() - headerRow.getRowNum(),
				headerRow.getFirstCellNum() + 1, headerRow.getLastCellNum() + 1, cellTypes);
	}

	public ExcelTable(Row headerRow, int rowsNumber, int firstColumnNumber, int lastColumnNumber, CellType<?>... cellTypes) {
		assertThat(rowsNumber).as("Number of table's rows should be greater than 0").isPositive();
		assertThat(firstColumnNumber).as("First column number should be greater than 0").isPositive();
		assertThat(lastColumnNumber).as("Last column number should be greater than first column number").isGreaterThan(firstColumnNumber);
		assertThat(cellTypes).as("Cell Types set should not be empty or null").isNotEmpty().isNotNull();

		nonTableColumnNumbers = new HashSet<>();
		nonTableColumnNumbers.addAll(IntStream.range(0, firstColumnNumber).boxed().collect(Collectors.toList()));
		nonTableColumnNumbers.addAll(IntStream.rangeClosed(lastColumnNumber, headerRow.getLastCellNum()).boxed().collect(Collectors.toList()));
		headerRow = removeNonTableCells(headerRow);
		this.headerRow = removeCellsWithDuplicatedValues(headerRow);
		this.rowsNumber = rowsNumber;
		this.cellTypes = cellTypes.clone();
	}


	public CellType<?>[] getCellTypes() {
		return this.cellTypes.clone();
	}

	public TableHeader getHeader() {
		if (header == null) {
			header = new TableHeader(getHeaderRow(), this);
		}
		return header;
	}

	public Sheet getSheet() {
		return getHeaderRow().getSheet();
	}

	public List<TableRow> getRows() {
		if (rows == null) {
			this.rows = new ArrayList<>(rowsNumber);
			for (int rowNumber = 1; rowNumber <= rowsNumber; rowNumber++) {
				Row row = getSheet().getRow(getHeaderRow().getRowNum() + rowNumber);
				row = removeNonTableCells(row);
				rows.add(new TableRow(row, this, rowNumber));
			}
		}
		return new ArrayList<>(this.rows);
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
				"header=" + getHeader() +
				", rowsNumber=" + getRowsNumber() +
				", tableRows=" + getRows() +
				'}';
	}

	public boolean hasRow(int rowNumber) {
		return rowNumber > 0 && rowNumber <= getRowsNumber();
	}

	/**
	 * Get {@link TableRow} object by table's row number. Index starts from 1 (0 belongs to header)
	 */
	public TableRow getRow(int rowNumber) {
		assertThat(hasRow(rowNumber)).as("There is no row number %s in table", rowNumber);
		return getRows().get(rowNumber - 1);
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

	Row getHeaderRow() {
		return headerRow;
	}

	private Row removeCellsWithDuplicatedValues(Row row) {
		Set<String> rowValues = new HashSet<>();
		for (Cell cell : getPoiCells(row)) {
			String cellValue = new ExcelCell(cell, ExcelCell.STRING_TYPE).getStringValue();
			if (rowValues.contains(cellValue)) {
				log.warn("Table's header has duplicated cell value {} in column number {}, cells from this column will be excluded", cellValue, cell.getColumnIndex() + 1);
				nonTableColumnNumbers.add(cell.getColumnIndex() + 1);
				row.removeCell(cell);
			} else {
				rowValues.add(cellValue);
			}
		}
		return row;
	}

	private List<Cell> getPoiCells(Row headerRow) {
		List<Cell> cells = new ArrayList<>();
		for (Cell cell : headerRow) {
			cells.add(cell);
		}
		return cells;
	}

	private Row removeNonTableCells(Row row) {
		for (Cell cell : getPoiCells(row)) {
			if (nonTableColumnNumbers.contains(cell.getColumnIndex() + 1) || cell == null) {
				row.removeCell(cell);
			}
		}
		return row;
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
