package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import toolkit.exceptions.IstfException;

public class ExcelSheet extends ExcelArea implements Iterable<ExcelRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelSheet.class);

	private Sheet sheet;
	private int sheetIndex;
	private ExcelManager excelManager;
	private Set<CellType<?>> allowableCellTypes;
	private Map<Integer, ExcelRow> rows;
	private Set<ExcelTable> tables;

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager) {
		this(sheet, sheetIndex, excelManager, ExcelCell.getBaseTypes());
	}

	public ExcelSheet(Sheet sheet, int sheetIndex, ExcelManager excelManager, Set<CellType<?>> allowableCellTypes) {
		this.sheet = sheet;
		this.sheetIndex = sheetIndex;
		this.excelManager = excelManager;
		this.allowableCellTypes = new HashSet<>(allowableCellTypes);
		this.tables = new HashSet<>();
	}

	public int getSheetIndex() {
		return sheetIndex;
	}

	public ExcelManager getExcelManager() {
		return excelManager;
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.allowableCellTypes);
	}

	/**
	 * @return only previously added tables by {@link #addTable(ExcelTable)} or found by {@link #getTable(String...)}, {@link #getTable(boolean, String...)} and {@link #getTable(int, String...)} methods
	 */
	public List<ExcelTable> getTables() {
		return new ArrayList<>(this.tables);
	}

	public String getSheetName() {
		return getPoiSheet().getSheetName();
	}

	public int getRowsNumber() {
		return getRowsMap().size();
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


	public ExcelRow getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public ExcelRow getLastRow() {
		return getRow(getLastRowIndex());
	}

	public List<ExcelRow> getRows() {
		return new ArrayList<>(getRowsMap().values());
	}

	public boolean hasRow(int rowIndex) {
		return getRowsMap().containsKey(rowIndex);
	}

	Sheet getPoiSheet() {
		return sheet;
	}

	@Override
	@Nonnull
	public Iterator<ExcelRow> iterator() {
		return new RowIterator<>(getRowsIndexes(), this::getRow);
	}

	public ExcelSheet addTable(ExcelTable table) {
		this.tables.add(table);
		return this;
	}

	/**
	 * Register cell types for next found ExcelTables and ExcelRows and update cell types for found {@link #tables}
	 */
	public ExcelSheet registerCellType(CellType<?>... cellTypes) {
		this.allowableCellTypes.addAll(Arrays.asList(cellTypes));
		getTables().forEach(t -> t.registerCellType(cellTypes));
		return this;
	}

	public ExcelRow getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %1$s on sheet %2$s or it's empty", rowIndex, getSheetName()).isTrue();
		return getRowsMap().get(rowIndex);
	}

	public ExcelCell getCell(int rowIndex, int columnIndex) {
		return getRow(rowIndex).getCell(columnIndex);
	}

	public List<Object> getRowValues(int rowIndex) {
		return getRowValues(rowIndex, 1);
	}

	public List<Object> getRowValues(int rowIndex, int fromColumnIndex) {
		ExcelRow row = getRow(rowIndex);
		return getRowValues(rowIndex, fromColumnIndex, row.getLastColumnIndex());
	}

	/**
	 * Get all non-null Object cell values from provided {@code rowIndex} starting inclusively from {@code fromColumnIndex} and up to inclusively {@code toColumnIndex}
	 *
	 * @param rowIndex row number from which values should be taken, index starts from 1
	 * @param fromColumnIndex the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnIndex the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnIndex}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnIndex/toColumnIndex} bounds
	 */
	public List<Object> getRowValues(int rowIndex, int fromColumnIndex, int toColumnIndex) {
		assertThat(fromColumnIndex).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowIndex);
		return IntStream.rangeClosed(fromColumnIndex, toColumnIndex).filter(row::hasColumn).mapToObj(row::getValue).collect(Collectors.toList());
	}

	public List<String> getRowStringValues(int rowIndex) {
		return getRowStringValues(rowIndex, 1);
	}

	public List<String> getRowStringValues(int rowIndex, int fromColumnIndex) {
		ExcelRow row = getRow(rowIndex);
		return getRowStringValues(rowIndex, fromColumnIndex, row.getLastColumnIndex());
	}

	/**
	 * Get all non-null String cell values from provided {@code rowIndex} starting inclusively from {@code fromColumnIndex} and up to inclusively {@code toColumnIndex}
	 *
	 * @param rowIndex row number from which values should be taken, index starts from 1
	 * @param fromColumnIndex the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnIndex the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnIndex}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnIndex/toColumnIndex} bounds
	 */
	public List<String> getRowStringValues(int rowIndex, int fromColumnIndex, int toColumnIndex) {
		assertThat(fromColumnIndex).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowIndex);
		return IntStream.rangeClosed(fromColumnIndex, toColumnIndex).filter(row::hasColumn).mapToObj(row::getStringValue).collect(Collectors.toList());
	}

	public ExcelRow getRow(String... valuesInCells) {
		return getRow(false, valuesInCells);
	}

	public ExcelRow getRow(boolean isLowest, String... valuesInCells) {
		Set<String> expectedColumnNames = new HashSet<>(Arrays.asList(valuesInCells));
		List<Row> foundRows = new ArrayList<>();
		Map<Integer, Pair<Row, String>> foundRowsWithPartialMatch = new HashMap<>();
		for (Row row : getPoiSheet()) {
			List<String> rowValues = getRowStringValues(row.getRowNum() + 1);
			Set<String> columnNames = new HashSet<>(expectedColumnNames);
			if (rowValues.containsAll(columnNames)) {
				foundRows.add(row);
			} else if (columnNames.removeAll(rowValues)) {
				foundRowsWithPartialMatch.put(columnNames.size(), Pair.of(row, columnNames.toString()));
			}

			if (!foundRows.isEmpty() && !isLowest) {
				break;
			}
		}

		if (foundRows.isEmpty()) {
			String errorMessage = String.format("Unable to find row with all these values: %1$s on sheet \"%2$s\"", expectedColumnNames, getPoiSheet().getSheetName());
			if (!foundRowsWithPartialMatch.isEmpty()) {
				int bestMatch = foundRowsWithPartialMatch.keySet().stream().min(Integer::compare).get();
				int rowNumber = foundRowsWithPartialMatch.get(bestMatch).getLeft().getRowNum();
				String missedVales = foundRowsWithPartialMatch.get(bestMatch).getRight();
				errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed cell values: %3$s", errorMessage, rowNumber, missedVales);
			}
			throw new IstfException(errorMessage);
		}

		Row poiRow = foundRows.get(foundRows.size() - 1);
		return new ExcelRow(poiRow, poiRow.getRowNum() + 1, this, getCellTypes());
	}

	/**
	 * Only columns with unique names from array will be searched. Returns <b>last</b> found ExcelTable
	 */
	public ExcelTable getTable(String... headerColumnNames) {
		return getTable(false, headerColumnNames);
	}

	public ExcelTable getTable(boolean isLowest, String... headerColumnNames) {
		ExcelRow headerRow = getRow(isLowest, headerColumnNames);
		return getTable(headerRow.getRowIndex(), headerColumnNames);
	}


	/**
	 *  Get ExcelTable object on current sheet with provided {@code headerColumnNames} header columns found in {@code headerRowIndex} row number.
	 *
	 * @param headerRowIndex Table's header row number on current sheet. Index starts from 1
	 * @param headerColumnNames header column names of needed ExcelTable. If array is empty then all columns from {@code headerRowIndex} will be used as column names
	 * @return {@link ExcelTable} object representation of found excel table
	 */
	public ExcelTable getTable(int headerRowIndex, String... headerColumnNames) {
		assertThat(headerRowIndex).as("Header row number should be greater than 0").isPositive();
		ExcelRow headerRow = getRow(headerRowIndex);
		assertThat(headerRow.isEmpty()).as("Header row should not be empty").isFalse();
		Set<Integer> columnIndexes = null;

		if (ArrayUtils.isNotEmpty(headerColumnNames)) {
			Set<String> headerColumns = new HashSet<>(Arrays.asList(headerColumnNames));
			Set<String> foundHeaderColumns = new HashSet<>();
			columnIndexes = new HashSet<>();
			for (ExcelCell cell : headerRow) {
				String value = cell.getStringValue();
				if (headerColumns.contains(value)) {
					columnIndexes.add(cell.getColumnIndex());
					foundHeaderColumns.add(value);
				}
			}

			foundHeaderColumns.retainAll(headerColumns);
			if (headerColumns.size() != foundHeaderColumns.size()) {
				headerColumns.removeAll(foundHeaderColumns);
				throw new IstfException(String.format("There are missed header columns %1$s in row number %2$s", headerColumns, headerRowIndex));
			}
		}

		ExcelTable t = new ExcelTable(headerRow.getPoiRow(), columnIndexes, this, getCellTypes());
		return addTable(t).getTable(t);
	}

	public ExcelSheet eraseRow(int rowIndex) {
		return eraseRow(getRow(rowIndex));
	}

	ExcelSheet eraseRow(ExcelRow row) {
		getPoiSheet().removeRow(row.getPoiRow());
		return this;
	}

	public ExcelSheet deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		Sheet sheet = getPoiSheet();
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %1$s on sheet %2$s", index, sheet.getSheetName()).isTrue();
			sheet.shiftRows(index - rowsShifts, sheet.getLastRowNum(), -1);
			rowsShifts++;
		}
		return this;
	}

	ExcelSheet deleteRows(ExcelRow... rows) {
		return deleteRows(Arrays.stream(rows).mapToInt(ExcelRow::getRowIndex).boxed().toArray(Integer[]::new));
	}

	ExcelTable getTable(ExcelTable table) {
		return this.tables.stream().filter(t -> t.equals(table)).findFirst().orElseThrow(() -> new IstfException("Table element does not exist in internal tables collection"));
	}

	public ExcelSheet save() {
		getExcelManager().save();
		return this;
	}

	public ExcelSheet save(File destinationFile) {
		getExcelManager().save(destinationFile);
		return this;
	}

	public ExcelSheet close() {
		getExcelManager().close();
		return this;
	}

	public ExcelSheet saveAndClose() {
		getExcelManager().saveAndClose();
		return this;
	}

	public ExcelSheet saveAndClose(File destinationFile) {
		getExcelManager().saveAndClose(destinationFile);
		return this;
	}

	@Override
	public String toString() {
		return "ExcelSheet{" +
				"sheetNumber=" + getSheetIndex() +
				", sheetName=" + getSheetName() +
				'}';
	}

	private Map<Integer, ExcelRow> getRowsMap() {
		if (this.rows == null) {
			this.rows = new HashMap<>();
			for (int rowNumber = 1; rowNumber <= getPoiSheet().getLastRowNum(); rowNumber++) {
				ExcelRow row = new ExcelRow(getPoiSheet().getRow(rowNumber - 1), rowNumber, this, getCellTypes());
				this.rows.put(rowNumber, row);
			}
		}
		return this.rows;
	}
}
