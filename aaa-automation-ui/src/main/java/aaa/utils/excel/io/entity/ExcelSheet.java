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
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import toolkit.exceptions.IstfException;

public class ExcelSheet implements Iterable<ExcelRow> {
	protected static Logger log = LoggerFactory.getLogger(ExcelSheet.class);

	private Sheet sheet;
	private int sheetNumber;
	private ExcelManager excelManager;
	private Set<CellType<?>> allowableCellTypes;
	private Set<ExcelTable> tables;

	public ExcelSheet(Sheet sheet, int sheetNumber, ExcelManager excelManager) {
		this(sheet, sheetNumber, excelManager, ExcelCell.getBaseTypes());
	}

	public ExcelSheet(Sheet sheet, int sheetNumber, ExcelManager excelManager, Set<CellType<?>> allowableCellTypes) {
		this.sheet = sheet;
		this.sheetNumber = sheetNumber;
		this.excelManager = excelManager;
		this.allowableCellTypes = new HashSet<>(allowableCellTypes);
		this.tables = new HashSet<>();
	}

	public int getSheetNumber() {
		return sheetNumber;
	}

	public ExcelManager getExcelManager() {
		return excelManager;
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.allowableCellTypes);
	}

	/**
	 * @return only previously added tables by {@link #addTable(ExcelTable)} or found by {@link #getTable(String...)}, {@link #getTable(boolean, String...)} and {@link #getTable(int)} methods
	 */
	public List<ExcelTable> getTables() {
		return new ArrayList<>(this.tables);
	}

	public String getSheetName() {
		return getPoiSheet().getSheetName();
	}

	public int getFirstRowNum() {
		return getPoiSheet().getFirstRowNum() + 1;
	}

	public int getLastRowNum() {
		return getPoiSheet().getLastRowNum() + 1;
	}

	public ExcelRow getFirstRow() {
		return getRow(getFirstRowNum());
	}

	public ExcelRow getLastRow() {
		return getRow(getLastRowNum());
	}

	Sheet getPoiSheet() {
		return sheet;
	}

	@Override
	@Nonnull
	public Iterator<ExcelRow> iterator() {
		return new RowIterator<>(getFirstRowNum(), getLastRowNum(), this::getRow);
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

	public ExcelRow getRow(int rowNumber) {
		return new ExcelRow(getPoiSheet().getRow(rowNumber - 1), this);
	}

	public ExcelCell getCell(int rowNumber, int columnNumber) {
		return getRow(rowNumber).getCell(columnNumber);
	}

	public List<Object> getRowValues(int rowNumber) {
		return getRowValues(rowNumber, 1);
	}

	public List<Object> getRowValues(int rowNumber, int fromColumnNumber) {
		ExcelRow row = getRow(rowNumber);
		return getRowValues(rowNumber, fromColumnNumber, row.getLastColumnNumber());
	}

	/**
	 * Get all non-null Object cell values from provided {@code rowNumber} starting inclusively from {@code fromColumnNumber} and up to inclusively {@code toColumnNumber}
	 *
	 * @param rowNumber row number from which values should be taken, index starts from 1
	 * @param fromColumnNumber the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnNumber the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnNumber}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnNumber/toColumnNumber} bounds
	 */
	public List<Object> getRowValues(int rowNumber, int fromColumnNumber, int toColumnNumber) {
		assertThat(fromColumnNumber).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowNumber);
		return IntStream.rangeClosed(fromColumnNumber, toColumnNumber).filter(row::hasColumn).mapToObj(row::getValue).collect(Collectors.toList());
	}

	public List<String> getRowStringValues(int rowNumber) {
		return getRowStringValues(rowNumber, 1);
	}

	public List<String> getRowStringValues(int rowNumber, int fromColumnNumber) {
		ExcelRow row = getRow(rowNumber);
		return getRowStringValues(rowNumber, fromColumnNumber, row.getLastColumnNumber());
	}

	/**
	 * Get all non-null String cell values from provided {@code rowNumber} starting inclusively from {@code fromColumnNumber} and up to inclusively {@code toColumnNumber}
	 *
	 * @param rowNumber row number from which values should be taken, index starts from 1
	 * @param fromColumnNumber the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnNumber the inclusive last column number on sheet to get values from. Should be greater than {@code fromColumnNumber}
	 *
	 * @return List of non-null String cell values found on provided {@code row} within {@code fromColumnNumber/toColumnNumber} bounds
	 */
	public List<String> getRowStringValues(int rowNumber, int fromColumnNumber, int toColumnNumber) {
		assertThat(fromColumnNumber).as("From column number should be greater than 0").isPositive();
		ExcelRow row = getRow(rowNumber);
		return IntStream.rangeClosed(fromColumnNumber, toColumnNumber).filter(row::hasColumn).mapToObj(row::getStringValue).collect(Collectors.toList());
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

		ExcelRow row = new ExcelRow(foundRows.get(foundRows.size() - 1), this);
		List<String> extraHeaderColumns = new ArrayList<>(row.getStringValues());
		extraHeaderColumns.removeAll(expectedColumnNames);
		if (!extraHeaderColumns.isEmpty()) {
			log.warn("Found row contains extra cell values: {}", extraHeaderColumns);
		}
		return row;
	}

	/**
	 * Only columns with unique names from array will be searched. Returns <b>last</b> found ExcelTable
	 */
	public ExcelTable getTable(String... headerColumnNames) {
		return getTable(false, headerColumnNames);
	}

	public ExcelTable getTable(boolean isLowest, String... headerColumnNames) {
		Row headerRow = getRow(isLowest, headerColumnNames).getPoiRow();
		ExcelTable t = new ExcelTable(headerRow, this);
		return addTable(t).getTable(t);
	}

	/**
	 *  Get ExcelTable object on current sheet with all non-null header column names found in {@code headerRowNumber} row number.
	 *
	 * @param headerRowNumber Table's header row number on current sheet. Index starts from 1
	 * @return {@link ExcelTable} object representation of found excel table
	 */
	public ExcelTable getTable(int headerRowNumber) {
		assertThat(headerRowNumber).as("Header row number should be greater than 0").isPositive();
		ExcelTable t = new ExcelTable(getRow(headerRowNumber).getPoiRow(), this);
		return addTable(t).getTable(t);
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
				"sheetNumber=" + getSheetNumber() +
				", sheetName=" + getSheetName() +
				'}';
	}

}
