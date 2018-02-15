package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import aaa.utils.excel.io.entity.queue.CellsQueue;

public class ExcelTable extends EditableCellsArea implements Iterable<TableRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelTable.class);

	private Row headerRow;
	private ExcelSheet excelSheet;
	private TableHeader header;
	private Map<Integer, TableRow> rows;
	private Map<Integer, TableColumn> columns;

	public ExcelTable(Row headerRow, ExcelSheet sheet) {
		this(headerRow, sheet, ExcelCell.getBaseTypes());
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this(headerRow, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, Set<Integer> columnsIndexes, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this(headerRow, columnsIndexes, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelSheet excelSheet, Set<CellType<?>> cellTypes) {
		super(excelSheet.getPoiSheet(),
				CollectionUtils.isNotEmpty(columnsIndexes) ? columnsIndexes : getHeaderColumnsIndexes(headerRow),
				CollectionUtils.isNotEmpty(rowsIndexes) ? rowsIndexes : getTableRowsIndexes(headerRow, columnsIndexes),
				excelSheet.getExcelManager(), cellTypes);
		this.headerRow = headerRow;
		this.excelSheet = excelSheet;
	}

	public TableHeader getHeader() {
		if (header == null) {
			header = new TableHeader(getHeaderRow(), this);
		}
		return header;
	}

	public ExcelSheet getSheet() {
		return excelSheet;
	}

	public List<Integer> getColumnsIndexesOnSheet() {
		return new ArrayList<>(this.columnsIndexes);
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

	public List<String> getColumnsNames() {
		return new ArrayList<>(getHeader().getColumnsNames());
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableRow> getRowsMap() {
		if (this.rows == null) {
			this.rows = new LinkedHashMap<>(this.rowsIndexes.size());
			int tableRowIndex = 1;
			for (Integer sheetRowIndex : this.rowsIndexes) {
				Row row = getSheet().getPoiSheet().getRow(sheetRowIndex - 1);
				this.rows.put(tableRowIndex, new TableRow(row, tableRowIndex, sheetRowIndex, this));
				tableRowIndex++;
			}
		}
		return this.rows;
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableColumn> getColumnsMap() {
		if (this.columns == null) {
			this.columns = new LinkedHashMap<>(this.columnsIndexes.size());
			int tableColumnIndex = 1;
			for (Integer sheetColumnIndex : getColumnsIndexesOnSheet()) {
				Row row = getSheet().getPoiSheet().getRow(sheetColumnIndex - 1);
				this.columns.put(tableColumnIndex, new TableColumn(tableColumnIndex, sheetColumnIndex, this));
				tableColumnIndex++;
			}
		}
		return this.columns;
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
				"sheetName=" + getPoiSheet().getSheetName() +
				", headerColumnNames=" + getHeader().getColumnsNames() +
				", rowsNumber=" + getRowsNumber() +
				", columnsNumber=" + getColumnsNumber() +
				", cellTypes=" + getCellTypes() +
				'}';
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableRow getRow(int rowIndex) {
		return super.getRow(rowIndex);
	}

	@SuppressWarnings("unchecked")
	@Override
	public TableColumn getColumn(int columnIndex) {
		return super.getColumn(columnIndex);
	}

	@Override
	public ExcelTable deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %s in table", index).isTrue();
			ListIterator<Integer> rowsIterator = new ArrayList<>(getRowsMap().keySet()).listIterator(index - rowsShifts);
			while (rowsIterator.hasNext()) {
				TableRow nextRow = getRowsMap().get(rowsIterator.next());
				TableRow currentRow = getRowsMap().get(rowsIterator.previousIndex());
				nextRow.copy(currentRow.getIndex());
			}
			clearRows(rowsIterator.nextIndex());
			getRowsMap().remove(rowsIterator.nextIndex());
			rowsShifts++;
		}
		return this;
	}

	@Override
	public ExcelTable excludeColumns(Integer... columnsIndexes) {
		List<Integer> sheetIndexes = new ArrayList<>();
		for (Integer cIndex : columnsIndexes) {
			for (CellsQueue row : getRows()) {
				((TableRow) row).getCellsMap().remove(cIndex);
				getHeader().getCellsMap().remove(cIndex);
				sheetIndexes.add(((TableRow) row).getIndexOnSheet());
			}
			this.columnsIndexes.removeAll(sheetIndexes);
		}
		return this;
	}

	private static boolean isRowEmpty(Row row, Collection<Integer> cellsIndexes) {
		if (row == null || row.getLastCellNum() <= 0) {
			return true;
		}
		for (Cell cell : row) {
			if (cellsIndexes.contains(cell.getColumnIndex() + 1) && cell != null && cell.getCellTypeEnum() != org.apache.poi.ss.usermodel.CellType.BLANK) {
				return false;
			}
		}
		return true;
	}

	private static Set<Integer> getHeaderColumnsIndexes(Row headerRow) {
		Set<Integer> columnsIndexes = new HashSet<>();
		for (Cell cell : headerRow) {
			if (cell != null && cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING && StringUtils.isNotBlank(cell.getStringCellValue())) {
				columnsIndexes.add(cell.getColumnIndex() + 1);
			}
		}
		assertThat(columnsIndexes)
				.as("There are no non-empty String columns in header row number %1$s on sheet \"%1$s\"", headerRow.getRowNum() + 1, headerRow.getSheet().getSheetName())
				.isNotEmpty();
		return columnsIndexes;
	}

	private static Set<Integer> getTableRowsIndexes(Row headerRow, Set<Integer> columnsIndexes) {
		Set<Integer> rIndexes = new HashSet<>();
		Set<Integer> cIndexes = CollectionUtils.isNotEmpty(columnsIndexes) ? columnsIndexes : getHeaderColumnsIndexes(headerRow);
		for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= headerRow.getSheet().getLastRowNum(); rowIndex++) {
			if (isRowEmpty(headerRow.getSheet().getRow(rowIndex), cIndexes)) {
				break;
			}
			rIndexes.add(rowIndex + 1);
		}
		return rIndexes;
	}

	public int getColumnIndex(String headerColumnName) {
		return getHeader().getColumnIndex(headerColumnName);
	}

	public TableColumn getColumn(String headerColumnName) {
		return getColumn(getColumnIndex(headerColumnName));
	}

	public boolean hasColumn(String headerColumnName) {
		return getHeader().hasColumn(headerColumnName);
	}

	public List<TableRow> getRows(String headerColumnName, Object cellValue) {
		List<TableRow> foundRows = getRows();
		foundRows = foundRows.stream().filter(r -> r.hasValue(headerColumnName, cellValue)).collect(Collectors.toList());
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

	public TableCell getCell(int rowIndex, String columnName) {
		return getRow(rowIndex).getCell(columnName);
	}

	public Object getValue(int rowIndex, String columnName) {
		return getCell(rowIndex, columnName).getValue();
	}

	public String getStringValue(int rowIndex, String columnName) {
		return getCell(rowIndex, columnName).getStringValue();
	}

	public ExcelTable excludeColumns(String... columnNames) {
		return excludeColumns(Arrays.stream(columnNames).map(this::getColumnIndex).toArray(Integer[]::new));
	}

	public ExcelTable clearRow(String headerColumnName, Object cellValue) {
		getRow(headerColumnName, cellValue).clear();
		return this;
	}

	public ExcelTable clearColumns(String... columnNames) {
		return (ExcelTable) clearColumns(Arrays.stream(columnNames).map(this::getColumnIndex).toArray(Integer[]::new));
	}

	public ExcelTable copyColumn(String columnName, String destinationColumnName) {
		return (ExcelTable) copyColumn(getColumnIndex(columnName), getHeader().getColumnIndex(destinationColumnName));
	}

	public EditableCellsArea deleteColumns(String... columnNames) {
		//TODO-dchubkov: implement delete columns by names
		throw new NotImplementedException("Columns deletion by header column names is not implemented yet");
	}

	public ExcelTable deleteRows(String headerColumnName, Object cellValue) {
		List<TableRow> rowsToDelete = getRows(headerColumnName, cellValue);
		return deleteRows(rowsToDelete.stream().mapToInt(TableRow::getIndex).boxed().toArray(Integer[]::new));
	}

	private Set<String> getHeaderColumnsNames(Row headerRow) {
		Set<String> headerColumnsNames = new HashSet<>();
		for (Cell cell : headerRow) {
			if (cell != null && cell.getCellTypeEnum() == org.apache.poi.ss.usermodel.CellType.STRING) {
				headerColumnsNames.add(cell.getStringCellValue());
			}
		}
		return headerColumnsNames;
	}
}
