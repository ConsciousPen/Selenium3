package aaa.utils.excel.io.entity.area.sheet;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.poi.ss.usermodel.Sheet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelArea;
import aaa.utils.excel.io.entity.area.table.ExcelTable;
import toolkit.exceptions.IstfException;

public class ExcelSheet extends ExcelArea<SheetCell, SheetRow, SheetColumn> {
	private final int sheetIndex;
	private List<ExcelTable> tables;

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager) {
		this(sheet, sheetIndex, excelManager, excelManager.getCellTypes());
	}

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager, List<CellType<?>> cellTypes) {
		this(sheet, sheetIndex, null, null, excelManager, cellTypes);
	}

	public ExcelSheet(Sheet sheet, int sheetIndex, List<Integer> columnsIndexes, List<Integer> rowsIndexes, ExcelManager excelManager, List<CellType<?>> cellTypes) {
		super(sheet, columnsIndexes, rowsIndexes, excelManager, cellTypes);
		this.sheetIndex = sheetIndex;
		this.tables = ImmutableList.of();
	}

	public int getSheetIndex() {
		return this.sheetIndex;
	}

	/**
	 * @return only previously added tables by {@link #addTable(ExcelTable)} or found by {@link #getTable(String...)}, {@link #getTable(boolean, String...)} and  {@link #getTable(int, List, boolean, String...)} methods
	 */
	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<ExcelTable> getTables() {
		return this.tables;
	}

	@Override
	protected ImmutableSortedMap<Integer, SheetRow> gatherAreaIndexesAndRowsMap(List<Integer> rowsIndexes, List<Integer> columnsIndexes, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, SheetRow> indexesAndRowsBuilder = ImmutableSortedMap.naturalOrder();
		for (int rowIndex : rowsIndexes) {
			SheetRow row = new SheetRow(getPoiSheet().getRow(rowIndex - 1), rowIndex, columnsIndexes, this, cellTypes);
			indexesAndRowsBuilder.put(rowIndex, row);
		}
		return indexesAndRowsBuilder.build();
	}

	@Override
	protected ImmutableSortedMap<Integer, SheetColumn> gatherAreaIndexesAndColumnsMap(List<Integer> rowsIndexes, List<Integer> columnsIndexes, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, SheetColumn> indexesAndColumnsBuilder = ImmutableSortedMap.naturalOrder();
		for (Integer columnIndex : columnsIndexes) {
			SheetColumn column = new SheetColumn(columnIndex, rowsIndexes, this, cellTypes);
			indexesAndColumnsBuilder.put(columnIndex, column);
		}
		return indexesAndColumnsBuilder.build();
	}

	/**
	 * Register cell types for next found ExcelTables and ExcelRows and update cell types for found {@link #tables}
	 */
	@Override
	public ExcelSheet registerCellType(CellType<?>... cellTypes) {
		super.registerCellType(cellTypes);
		getTables().forEach(t -> t.registerCellType(cellTypes));
		return this;
	}

	@Override
	public String toString() {
		return "ExcelSheet{" +
				"sheetNumber=" + getSheetIndex() +
				", sheetName=" + getSheetName() +
				", rowsNumber=" + getRowsNumber() +
				", columnsNumber=" + getColumnsNumber() +
				", tablesNumber=" + getTables().size() +
				", cellTypes=" + getCellTypes() +
				'}';
	}

	public ExcelTable getTable(String... headerColumnsNames) {
		return getTable(false, headerColumnsNames);
	}

	public ExcelTable getTable(boolean ignoreCase, String... headerColumnsNames) {
		SheetRow row = getRow(ignoreCase, headerColumnsNames);
		return getTable(row.getIndex(), null, ignoreCase, headerColumnsNames);
	}

	public ExcelTable getTable(int headerRowIndexOnSheet, String... headerColumnsNames) {
		return getTable(headerRowIndexOnSheet, null, headerColumnsNames);
	}

	public ExcelTable getTable(int headerRowIndexOnSheet, List<Integer> rowsIndexesInTable, String... headerColumnsNames) {
		return getTable(headerRowIndexOnSheet, rowsIndexesInTable, false, headerColumnsNames);
	}

	/**
	 *  Get ExcelTable object on current sheet with provided {@code headerColumnsNames} header columns found in {@code headerRowIndexOnSheet} row number.
	 *
	 * @param headerRowIndexOnSheet Table's header row number on current sheet. Index starts from 1
	 * @param rowsIndexesInTable rows indexes in table to be used as table rows. If null then all rows from header row up to first row with all empty {@code headerColumnsNames} will be used as tables' rows
	 * @param ignoreCase if true then ignore header column names while searching header columns indexes within {@code headerRowIndexOnSheet} row
	 * @param headerColumnsNames header column names of needed ExcelTable. If array is empty then all columns from {@code headerRowIndexOnSheet} will be used as column names
	 * @return {@link ExcelTable} object representation of found excel table
	 */
	public ExcelTable getTable(int headerRowIndexOnSheet, List<Integer> rowsIndexesInTable, boolean ignoreCase, String... headerColumnsNames) {
		assertThat(headerRowIndexOnSheet).as("Header row number should be greater than 0").isPositive();
		SheetRow headerRow = getRow(headerRowIndexOnSheet);
		assertThat(headerRow.isEmpty()).as("Header row should not be empty").isFalse();
		List<Integer> columnsIndexesOnSheet = null;
		List<Integer> rowsIndexesOnSheet = CollectionUtils.isNotEmpty(rowsIndexesInTable) ? rowsIndexesInTable.stream().map(r -> r + headerRowIndexOnSheet).collect(Collectors.toList()) : null;

		if (ArrayUtils.isNotEmpty(headerColumnsNames)) {
			Set<String> missedHeaderColumnsNames = new HashSet<>(Arrays.asList(headerColumnsNames));
			columnsIndexesOnSheet = new LinkedList<>();
			for (SheetCell cell : headerRow) {
				String cellValue = cell.getStringValue();
				if (cellValue == null) {
					continue;
				}

				Predicate<String> cellValueEqualsToHeaderName = ignoreCase ? cellValue::equalsIgnoreCase : cellValue::equals;
				if (missedHeaderColumnsNames.removeIf(cellValueEqualsToHeaderName)) {
					columnsIndexesOnSheet.add(cell.getColumnIndex());
				}
			}

			if (!missedHeaderColumnsNames.isEmpty()) {
				throw new IstfException(String.format("There are missed header columns %1$s in row number %2$s on sheet \"%3$s\"", missedHeaderColumnsNames, headerRowIndexOnSheet, getSheetName()));
			}
		}

		ExcelTable t = new ExcelTable(headerRow.getPoiRow(), columnsIndexesOnSheet, rowsIndexesOnSheet, this, getCellTypes());
		t.considerRowsOnComparison(isRowsComparisonRuleSet());
		t.considerColumnsOnComparison(isColumnsComparisonRuleSet());
		return addTable(t).getTable(t);
	}

	protected ExcelSheet addTable(ExcelTable table) {
		if (!getTables().contains(table)) {
			this.tables = ImmutableList.<ExcelTable>builder().addAll(getTables()).add(table).build();
		}
		return this;
	}

	protected ExcelTable getTable(ExcelTable table) {
		return this.tables.stream().filter(t -> t.equals(table)).findFirst().orElseThrow(() -> new IstfException("Internal tables collection does not contain: " + table));
	}
}
