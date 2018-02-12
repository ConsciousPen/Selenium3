package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelArea;
import aaa.utils.excel.io.entity.area.sheet.ExcelSheet;

public class ExcelTable extends ExcelArea<TableCell, TableRow, TableColumn> {
	private Row headerRow;
	private ExcelSheet excelSheet;
	private TableHeader header;

	public ExcelTable(Row headerRow, ExcelSheet sheet) {
		this(headerRow, sheet, sheet.getCellTypes());
	}

	public ExcelTable(Row headerRow, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this(headerRow, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, Set<Integer> columnsIndexesOnSheet, ExcelSheet sheet, Set<CellType<?>> cellTypes) {
		this(headerRow, columnsIndexesOnSheet, null, sheet, cellTypes);
	}

	public ExcelTable(Row headerRow, Set<Integer> columnsIndexesOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelSheet excelSheet, Set<CellType<?>> cellTypes) {
		super(excelSheet.getPoiSheet(),
				CollectionUtils.isNotEmpty(columnsIndexesOnSheet) ? columnsIndexesOnSheet : getHeaderColumnsIndexes(headerRow),
				CollectionUtils.isNotEmpty(rowsIndexesOnSheet) ? rowsIndexesOnSheet : getTableRowsIndexes(headerRow, columnsIndexesOnSheet),
				excelSheet.getExcelManager(), cellTypes);
		this.headerRow = headerRow;
		this.excelSheet = excelSheet;
	}

	public TableHeader getHeader() {
		if (this.header == null) {
			this.header = new TableHeader(this.headerRow, new HashSet<>(getColumnsIndexesOnSheet()), this);
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
		return new ArrayList<>(getHeader().getColumnsNames());
	}

	@Override
	public List<Integer> getColumnsIndexesOnSheet() {
		return super.getColumnsIndexesOnSheet();
	}

	@Override
	protected Map<Integer, TableRow> gatherAreaIndexesAndRowsMap(Set<Integer> rowsIndexesOnSheet, Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, TableRow> tableIndexesAndRowsMap = new LinkedHashMap<>(rowsIndexesOnSheet.size());
		int rowIndexInTable = 1;
		for (Integer sheetRowIndex : rowsIndexesOnSheet) {
			TableRow row = new TableRow(getSheet().getPoiSheet().getRow(sheetRowIndex - 1), rowIndexInTable, sheetRowIndex, columnsIndexesOnSheet, this, cellTypes);
			tableIndexesAndRowsMap.put(rowIndexInTable, row);
			rowIndexInTable++;
		}
		return tableIndexesAndRowsMap;
	}

	@Override
	protected Map<Integer, TableColumn> gatherAreaIndexesAndColumnsMap(Set<Integer> rowsIndexesOnSheet, Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, TableColumn> tableIndexesAndColumnsMap = new LinkedHashMap<>(columnsIndexesOnSheet.size());
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Row row = getSheet().getPoiSheet().getRow(columnIndexOnSheet - 1);
			tableIndexesAndColumnsMap.put(columnIndexInTable, new TableColumn(columnIndexInTable, columnIndexOnSheet, rowsIndexesOnSheet, this, cellTypes));
			columnIndexInTable++;
		}
		return tableIndexesAndColumnsMap;
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
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %s in table", index).isTrue();
			ListIterator<Integer> rowsIterator = new ArrayList<>(getRowsIndexes()).listIterator(index - rowsShifts);
			while (rowsIterator.hasNext()) {
				TableRow nextRow = getRow(rowsIterator.next());
				TableRow currentRow = getRow(rowsIterator.previousIndex());
				nextRow.copy(currentRow.getIndex());
			}
			clearRows(rowsIterator.nextIndex());
			removeRowsIndexes(rowsIterator.nextIndex());
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

	private static Set<Integer> getTableRowsIndexes(Row headerRow, Set<Integer> columnsIndexesOnSheet) {
		Set<Integer> rIndexes = new HashSet<>();
		Set<Integer> cIndexes = CollectionUtils.isNotEmpty(columnsIndexesOnSheet) ? columnsIndexesOnSheet : getHeaderColumnsIndexes(headerRow);
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

	public boolean hasColumn(String headerColumnName) {
		return getHeader().hasColumn(headerColumnName);
	}

	public TableColumn getColumn(String headerColumnName) {
		return getColumn(getColumnIndex(headerColumnName));
	}

	public TableRow getRow(String headerColumnName, Object cellValue) {
		return getRows(headerColumnName, cellValue).get(0);
	}

	public List<TableRow> getRows(String headerColumnName, Object cellValue) {
		List<TableRow> foundRows = getRows();
		foundRows = foundRows.stream().filter(r -> r.hasValue(headerColumnName, cellValue)).collect(Collectors.toList());
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
		return getRow(rowIndex).getCell(headerColumnName);
	}

	public Object getValue(int rowIndex, String headerColumnName) {
		return getCell(rowIndex, headerColumnName).getValue();
	}

	public String getStringValue(int rowIndex, String headerColumnName) {
		return getCell(rowIndex, headerColumnName).getStringValue();
	}

	public ExcelTable excludeColumns(String... headerColumnNames) {
		return excludeColumns(Arrays.stream(headerColumnNames).map(this::getColumnIndex).toArray(Integer[]::new));
	}

	public ExcelTable clearRow(String headerColumnName, Object cellValue) {
		getRow(headerColumnName, cellValue).clear();
		return this;
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
