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

	private Set<Integer> columnNumbers;
	private Row headerRow;
	private Integer rowsNumber;
	private CellType<?>[] cellTypes;
	private TableHeader header;
	private List<TableRow> rows;

	public ExcelTable(Row headerRow) {
		this(headerRow, ExcelCell.getBaseTypes());
	}

	public ExcelTable(Row headerRow, CellType<?>... cellTypes) {
		//TODO: get last row in table instead of last row on sheet
		this(headerRow, null, null, null, cellTypes);
	}

	public ExcelTable(Row headerRow, Integer rowsNumber, Integer firstColumnNumber, Integer lastColumnNumber, CellType<?>... cellTypes) {
		setColumnNumbers(headerRow, firstColumnNumber, lastColumnNumber);
		this.headerRow = removeNonTableCells(headerRow);
		this.rowsNumber = rowsNumber != null ? rowsNumber : calculateTableRowsNumber(headerRow);
		this.cellTypes = cellTypes.length != 0 ? cellTypes.clone() : ExcelCell.getBaseTypes();
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
		if (this.rows == null) {
			this.rows = new ArrayList<>(this.rowsNumber);
			for (int rowNumber = 1; rowNumber <= this.rowsNumber; rowNumber++) {
				Row row = getSheet().getRow(getHeaderRow().getRowNum() + rowNumber);
				row = removeNonTableCells(row);
				this.rows.add(new TableRow(row, this, rowNumber));
			}
		}
		return new ArrayList<>(this.rows);
	}

	public List<Map<String, Object>> getValues() {
		List<Map<String, Object>> values = new ArrayList<>();
		for (TableRow row : this) {
			values.add(row.getTableValues());
		}
		return values;
	}

	public List<Map<String, String>> getStringValues() {
		List<Map<String, String>> values = new ArrayList<>();
		for (TableRow row : this) {
			values.add(row.getTableStringValues());
		}
		return values;
	}

	/**
	 * Get table's rows number without header row
	 */
	public int getRowsNumber() {
		return rowsNumber;
	}

	Row getHeaderRow() {
		return headerRow;
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

	public TableRow getFirstRow() {
		return getRow(1);
	}

	public TableRow getLastRow() {
		return getRow(this.rowsNumber);
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

	private void setColumnNumbers(Row headerRow, Integer firstColumnNumber, Integer lastColumnNumber) {
		if (firstColumnNumber == null) {
			firstColumnNumber = headerRow.getFirstCellNum() + 1;
		}
		if (lastColumnNumber == null) {
			lastColumnNumber = (int) headerRow.getLastCellNum();
		}

		this.columnNumbers = IntStream.rangeClosed(firstColumnNumber, lastColumnNumber).boxed().collect(Collectors.toSet());
		List<Cell> nonEmptyCells = getNonEmptyPoiCells(headerRow, columnNumbers);
		if (columnNumbers.size() > nonEmptyCells.size()) {
			log.warn("Table's header has null or empty cell(s), cells from this column will be excluded from table ExcelTable instance");
			Set<Integer> nonEmptyColumnNumbers = nonEmptyCells.stream().mapToInt(c -> c.getColumnIndex() + 1).boxed().collect(Collectors.toSet());
			for (int cn : columnNumbers) {
				if (!nonEmptyColumnNumbers.contains(cn)) {
					columnNumbers.remove(cn);
				}
			}
		}

		Set<String> rowValues = new HashSet<>();
		for (Cell cell : nonEmptyCells) {
			String cellValue = new ExcelCell(cell, ExcelCell.STRING_TYPE).getStringValue();
			if (cellValue.isEmpty()) {
				log.warn("Table's header has empty cell value in column number {}, cells from this column will be excluded from table ExcelTable instance", cell.getColumnIndex() + 1);
				this.columnNumbers.remove(cell.getColumnIndex() + 1);
			} else if (rowValues.contains(cellValue)) {
				log.warn("Table's header has duplicated cell value {} in column number {}, cells from this column will be excluded from table ExcelTable instance", cellValue,
						cell.getColumnIndex() + 1);
				this.columnNumbers.remove(cell.getColumnIndex() + 1);
			} else {
				rowValues.add(cellValue);
			}
		}
	}

	private int calculateTableRowsNumber(Row headerRow) {
		Sheet sheet = headerRow.getSheet();
		for (int rowNumber = headerRow.getRowNum() + 1; rowNumber <= sheet.getLastRowNum(); rowNumber++) {
			Row row = sheet.getRow(rowNumber);
			if (getNonEmptyPoiCells(row, this.columnNumbers).isEmpty()) {
				return rowNumber - headerRow.getRowNum() - 1;
			}
		}
		return sheet.getLastRowNum() - headerRow.getRowNum() - 1;
	}

	private List<Cell> getNonEmptyPoiCells(Row row, Set<Integer> columnNumbers) {
		List<Cell> cells = new ArrayList<>();
		for (Cell cell : row) {
			if (cell != null && cell.getCellTypeEnum() != org.apache.poi.ss.usermodel.CellType.BLANK && columnNumbers.contains(cell.getColumnIndex() + 1)) {
				cells.add(cell);
			}
		}
		return cells;
	}

	private Row removeNonTableCells(Row row) {
		List<Cell> nonEmptyCells = getNonEmptyPoiCells(row, this.columnNumbers);
		for (Cell cell : row) {
			if (!nonEmptyCells.contains(cell)) {
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
