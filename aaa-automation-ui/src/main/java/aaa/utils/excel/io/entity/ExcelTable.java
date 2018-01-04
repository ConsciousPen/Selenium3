package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.iterator.RowIterator;

public class ExcelTable implements Iterable<TableRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelTable.class);

	private Set<Integer> columnIndexes;
	private Row headerRow;
	private ExcelSheet sheet;
	private Integer rowsNumber;
	private Set<CellType<?>> cellTypes;
	private TableHeader header;
	private Map<Integer, TableRow> rows;

	public ExcelTable(Row headerRow, ExcelSheet sheet) {
		this(headerRow, sheet, ExcelCell.getBaseTypes());
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this(headerRow, sheet, null, cellTypes);
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, Set<Integer> columnIndexes, Set<CellType<?>> cellTypes) {
		this(headerRow, sheet, null, columnIndexes, cellTypes);
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, Integer rowsNumber, Set<Integer> columnIndexes, Set<CellType<?>> cellTypes) {
		this.cellTypes = new HashSet<>(cellTypes);
		this.columnIndexes = columnIndexes != null ? columnIndexes : getHeaderColumnIndexes(headerRow);
		this.headerRow = headerRow;
		this.sheet = sheet;
		this.rowsNumber = rowsNumber != null ? rowsNumber : getNonEmptyTableRowsNumber(headerRow);
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public TableHeader getHeader() {
		if (header == null) {
			header = new TableHeader(getHeaderRow(), columnIndexes, this);
		}
		return header;
	}

	public ExcelSheet getSheet() {
		return sheet;
	}

	public List<TableRow> getRows() {
		return new ArrayList<>(getRowsMap().values());
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
		return getRowsMap().size();
	}

	public int getColumnsNumber() {
		return getColumnsIndexes().size();
	}

	public List<Integer> getColumnsIndexes() {
		return new ArrayList<>(getHeader().getColumnsIndexes());
	}

	public List<String> getColumnsNames() {
		return new ArrayList<>(getHeader().getColumnsNames());
	}

	public List<Integer> getRowsIndexes() {
		return new ArrayList<>(getRowsMap().keySet());
	}

	public int getFirstRowIndex() {
		return getRowsIndexes().get(0);
	}

	public int getLastRowIndex() {
		List<Integer> rowIndexes = getRowsIndexes();
		return rowIndexes.get(rowIndexes.size() - 1);
	}

	public int getFirstColumnIndex() {
		return getHeader().getFirstColumnIndex();
	}

	public int getLastColumnIndex() {
		return getHeader().getLastColumnIndex();
	}

	public TableRow getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public TableRow getLastRow() {
		return getRow(getLastRowIndex());
	}

	Row getHeaderRow() {
		return headerRow;
	}

	@Override
	@Nonnull
	public Iterator<TableRow> iterator() {
		return new RowIterator<>(getRowsIndexes(), this::getRow);
	}

	@Override
	public String toString() {
		return "ExcelTable{" +
				"sheet=" + getSheet() +
				", header=" + getHeader() +
				", rowsNumber=" + getRowsNumber() +
				", tableRows=" + getRows() +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ExcelTable that = (ExcelTable) o;
		return Objects.equals(header, that.header) &&
				Objects.equals(rows, that.rows);
	}

	@Override
	public int hashCode() {
		return Objects.hash(header, rows);
	}

	public boolean hasRow(int rowIndex) {
		return getRowsMap().containsKey(rowIndex);
	}

	/**
	 * Get {@link TableRow} object by table's row index. Index starts from 1 (0 belongs to header)
	 */
	public TableRow getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %s in table", rowIndex).isTrue();
		return getRowsMap().get(rowIndex);
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

	public ExcelTable registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getRows().forEach(r -> r.registerCellType(cellTypes));
		return this;
	}

	public ExcelTable save() {
		getSheet().save();
		return this;
	}

	public ExcelTable save(File destinationFile) {
		getSheet().save(destinationFile);
		return this;
	}

	public ExcelTable close() {
		getSheet().close();
		return this;
	}

	public ExcelTable saveAndClose() {
		getSheet().saveAndClose();
		return this;
	}

	public ExcelTable saveAndClose(File destinationFile) {
		getSheet().saveAndClose(destinationFile);
		return this;
	}

	public ExcelTable excludeColumns(String... columnNames) {
		for (String cName : columnNames) {
			int columnNumber = getHeader().getColumnIndex(cName);
			this.columnIndexes.remove(columnNumber);
			getHeader().excludeColumn(columnNumber);
			for (TableRow row : this) {
				row.excludeColumn(columnNumber);
			}
		}
		return this;
	}

	public ExcelTable excludeRows(Integer... rowsIndexes) {
		for (int rIndex : rowsIndexes) {
			assertThat(hasRow(rIndex)).as("There is no row number %s in table", rIndex).isTrue();
			getRowsMap().remove(rIndex);
		}
		return this;
	}

	public ExcelTable eraseRow(String headerColumnName, Object cellValue) {
		getRow(headerColumnName, cellValue).erase();
		return this;
	}

	public ExcelTable eraseRows(Integer... rowsIndexes) {
		for (Integer index : rowsIndexes) {
			getRow(index).erase();
		}
		return this;
	}

	public ExcelTable deleteRows(String headerColumnName, Object cellValue) {
		List<TableRow> rowsToDelete = getRows(headerColumnName, cellValue);
		return deleteRows(rowsToDelete.toArray(new TableRow[rowsToDelete.size()]));
	}

	ExcelTable deleteRows(TableRow... rows) {
		return deleteRows(Arrays.stream(rows).mapToInt(ExcelRow::getRowIndex).boxed().toArray(Integer[]::new));
	}

	public ExcelTable deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %s in table", index).isTrue();
			ListIterator<Integer> rowsIterator = new ArrayList<>(getRowsMap().keySet()).listIterator(index - rowsShifts);
			while (rowsIterator.hasNext()) {
				TableRow nextRow = getRowsMap().get(rowsIterator.next());
				TableRow currentRow = getRowsMap().get(rowsIterator.previousIndex());
				nextRow.copy(currentRow);
			}
			eraseRows(rowsIterator.nextIndex());
			getRowsMap().remove(rowsIterator.nextIndex());
			rowsShifts++;
		}
		return this;
	}

	private int getNonEmptyTableRowsNumber(Row headerRow) {
		Sheet sheet = getSheet().getPoiSheet();
		for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
			ExcelRow excelRow = new ExcelRow(sheet.getRow(rowIndex), rowIndex, this.columnIndexes, getSheet(), getCellTypes());
			if (excelRow.isEmpty()) {
				return rowIndex - headerRow.getRowNum() - 1;
			}
		}
		return sheet.getLastRowNum() - headerRow.getRowNum();
	}

	private Set<Integer> getHeaderColumnIndexes(Row row) {
		Set<Integer> cellNumbers = new HashSet<>();
		for (Cell cell : row) {
			if (cell != null && cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING) {
				cellNumbers.add(cell.getColumnIndex() + 1);
			}
		}
		return cellNumbers;
	}

	private Map<Integer, TableRow> getRowsMap() {
		if (this.rows == null) {
			this.rows = new HashMap<>(this.rowsNumber);
			for (int rowNumber = 1; rowNumber <= this.rowsNumber; rowNumber++) {
				Row row = getSheet().getPoiSheet().getRow(getHeaderRow().getRowNum() + rowNumber);
				this.rows.put(rowNumber, new TableRow(row, rowNumber, this.columnIndexes,this));
			}
		}
		return this.rows;
	}
}
