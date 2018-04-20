package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import com.google.common.collect.ImmutableSortedMap;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelArea;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;

public class ExcelTable extends ExcelArea<TableCell, TableRow, TableColumn> {
	private final Row headerRow;
	private final ExcelSheet excelSheet;

	private TableHeader header;

	public ExcelTable(Row headerRow, ExcelSheet sheet) {
		this(headerRow, sheet, sheet.getCellTypes());
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, List<CellType<?>> cellTypes) {
		this(headerRow, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, List<Integer> columnsIndexesOnSheet, ExcelSheet sheet, List<CellType<?>> cellTypes) {
		this(headerRow, columnsIndexesOnSheet, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, List<Integer> columnsIndexesOnSheet, List<Integer> rowsIndexesOnSheet, ExcelSheet excelSheet, List<CellType<?>> cellTypes) {
		super(excelSheet.getPoiSheet(),
				CollectionUtils.isNotEmpty(columnsIndexesOnSheet) ? columnsIndexesOnSheet : getHeaderColumnsIndexes(headerRow),
				CollectionUtils.isNotEmpty(rowsIndexesOnSheet) ? rowsIndexesOnSheet : getTableRowsIndexes(headerRow, columnsIndexesOnSheet),
				excelSheet.getExcelManager(), cellTypes);
		this.headerRow = headerRow;
		this.excelSheet = excelSheet;
	}

	public TableHeader getHeader() {
		if (this.header == null) {
			this.header = new TableHeader(this.headerRow, getColumnsIndexesOnSheet(), this);
		}
		return this.header;
	}

	public ExcelSheet getSheet() {
		return this.excelSheet;
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
		return getHeader().getColumnsNames();
	}

	private static List<Integer> getHeaderColumnsIndexes(Row headerRow) {
		List<Integer> columnsIndexes = new LinkedList<>();
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

	private static List<Integer> getTableRowsIndexes(Row headerRow, List<Integer> columnsIndexesOnSheet) {
		List<Integer> rIndexes = new LinkedList<>();
		List<Integer> cIndexes = CollectionUtils.isNotEmpty(columnsIndexesOnSheet) ? columnsIndexesOnSheet : getHeaderColumnsIndexes(headerRow);

		for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= headerRow.getSheet().getLastRowNum(); rowIndex++) {
			if (isRowEmpty(headerRow.getSheet().getRow(rowIndex), cIndexes)) {
				break;
			}
			rIndexes.add(rowIndex + 1);
		}
		return rIndexes;
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

	@Override
	protected ImmutableSortedMap<Integer, TableRow> gatherAreaIndexesAndRowsMap(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, TableRow> indexesAndRowsBuilder = ImmutableSortedMap.naturalOrder();
		int rowIndexInTable = 1;
		for (Integer sheetRowIndex : rowsIndexesOnSheet) {
			TableRow row = new TableRow(getSheet().getPoiSheet().getRow(sheetRowIndex - 1), rowIndexInTable, sheetRowIndex, columnsIndexesOnSheet, this, cellTypes);
			indexesAndRowsBuilder.put(rowIndexInTable, row);
			rowIndexInTable++;
		}
		return indexesAndRowsBuilder.build();
	}

	@Override
	protected ImmutableSortedMap<Integer, TableColumn> gatherAreaIndexesAndColumnsMap(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, TableColumn> indexesAndColumnsBuilder = ImmutableSortedMap.naturalOrder();
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			TableColumn column = new TableColumn(columnIndexInTable, columnIndexOnSheet, rowsIndexesOnSheet, this, cellTypes);
			indexesAndColumnsBuilder.put(columnIndexInTable, column);
			columnIndexInTable++;
		}
		return indexesAndColumnsBuilder.build();
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

	@Override
	public ExcelTable deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %1$s in table %2$s", index, this).isTrue();
			ListIterator<Integer> rowsIterator = new ArrayList<>(getRowsIndexes()).listIterator(index - rowsShifts);
			while (rowsIterator.hasNext()) {
				TableRow nextRow = getRow(rowsIterator.next());
				TableRow currentRow = getRow(rowsIterator.previousIndex());
				nextRow.copy(currentRow.getIndex());
			}
			clearRows(rowsIterator.nextIndex());
			excludeRows(rowsIterator.nextIndex());
			rowsShifts++;
		}
		return this;
	}

	@Override
	public ExcelTable excludeColumns(Integer... columnsIndexes) {
		super.excludeColumns(columnsIndexes);
		getHeader().removeCellsIndexes(columnsIndexes);
		return this;
	}

	public ExcelTable excludeColumns(String... headerColumnNames) {
		Integer[] columnsIndexes = Arrays.stream(headerColumnNames).map(this::getColumnIndex).toArray(Integer[]::new);
		return excludeColumns(columnsIndexes);
	}

	public int getColumnIndex(String headerColumnName) {
		return getColumnIndex(headerColumnName, false);
	}

	public int getColumnIndex(String headerColumnName, boolean ignoreCase) {
		return getHeader().getColumnIndex(headerColumnName, ignoreCase);
	}

	public boolean hasColumn(String headerColumnName) {
		return hasColumn(headerColumnName, false);
	}

	public boolean hasColumn(String headerColumnName, boolean ignoreCase) {
		return getHeader().hasColumn(headerColumnName, ignoreCase);
	}

	public TableColumn getColumn(String headerColumnName) {
		return getColumn(headerColumnName, false);
	}

	public TableColumn getColumn(String headerColumnName, boolean ignoreCase) {
		return getColumn(getColumnIndex(headerColumnName, ignoreCase));
	}

	public TableRow getRow(String headerColumnName, Object cellValue) {
		return getRow(headerColumnName, false, cellValue);
	}

	public TableRow getRow(String headerColumnName, boolean ignoreHeaderColumnCase, Object cellValue) {
		return getRows(headerColumnName, ignoreHeaderColumnCase, cellValue).get(0);
	}

	public List<TableRow> getRows(String headerColumnName, Object cellValue) {
		return getRows(headerColumnName, false, cellValue);
	}

	public List<TableRow> getRows(String headerColumnName, boolean ignoreHeaderColumnCase, Object cellValue) {
		List<TableRow> foundRows = getRows().stream().filter(r -> r.hasValue(headerColumnName, ignoreHeaderColumnCase, cellValue)).collect(Collectors.toList());
		assertThat(foundRows).as("There are no rows in table with value \"%1$s\" in column \"%2$s\"", cellValue, headerColumnName).isNotEmpty();
		return foundRows;
	}

	public TableRow getRow(Map<String, Object> query) {
		return getRows(query).get(0);
	}

	public List<TableRow> getRows(Map<String, Object> query) {
		List<TableRow> foundRows = new ArrayList<>(getRows());
		for (Map.Entry<String, Object> columnNameAndCellValue : query.entrySet()) {
			foundRows.removeIf(r -> !r.hasValue(columnNameAndCellValue.getKey(), columnNameAndCellValue.getValue()));
		}
		assertThat(foundRows).as("There are no rows in table with column names and cell values query: " + query.entrySet()).isNotEmpty();
		return foundRows;
	}

	public TableCell getCell(int rowIndex, String headerColumnName) {
		return getCell(rowIndex, headerColumnName, false);
	}

	public TableCell getCell(int rowIndex, String headerColumnName, boolean ignoreCase) {
		return getRow(rowIndex).getCell(headerColumnName, ignoreCase);
	}

	public Object getValue(int rowIndex, String headerColumnName) {
		return getValue(rowIndex, headerColumnName, false);
	}

	public Object getValue(int rowIndex, String headerColumnName, boolean ignoreCase) {
		return getCell(rowIndex, headerColumnName, ignoreCase).getValue();
	}

	public String getStringValue(int rowIndex, String headerColumnName) {
		return getStringValue(rowIndex, headerColumnName, false);
	}

	public String getStringValue(int rowIndex, String headerColumnName, boolean ignoreCase) {
		return getCell(rowIndex, headerColumnName, ignoreCase).getStringValue();
	}

	public ExcelTable clearRow(String headerColumnName, Object cellValue) {
		return ((TableRow) getRow(headerColumnName, cellValue).clear()).getTable();
	}

	public ExcelTable clearColumns(String... headerColumnNames) {
		return (ExcelTable) clearColumns(Arrays.stream(headerColumnNames).map(this::getColumnIndex).toArray(Integer[]::new));
	}

	public ExcelTable copyColumn(String headerColumnName, String destinationHeaderColumnName) {
		return (ExcelTable) copyColumn(getColumnIndex(headerColumnName), getHeader().getColumnIndex(destinationHeaderColumnName));
	}

	public ExcelTable deleteColumns(String... headerColumnNames) {
		//TODO-dchubkov: implement delete columns by names
		throw new NotImplementedException("Columns deletion by header column names is not implemented yet");
	}

	public ExcelTable deleteRows(String headerColumnName, Object cellValue) {
		List<TableRow> rowsToDelete = getRows(headerColumnName, cellValue);
		return deleteRows(rowsToDelete.stream().map(TableRow::getIndex).toArray(Integer[]::new));
	}
}
