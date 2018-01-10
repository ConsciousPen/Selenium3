package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public abstract class CellsArea {
	protected Sheet sheet;
	protected Set<Integer> columnsIndexes;
	protected Set<Integer> rowsIndexes;
	protected ExcelManager excelManager;
	protected Set<CellType<?>> cellTypes;

	public CellsArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		this.sheet = sheet;
		this.columnsIndexes = CollectionUtils.isNotEmpty(columnsIndexes) ? columnsIndexes : getColumnsIndexes(sheet);
		this.rowsIndexes = CollectionUtils.isNotEmpty(rowsIndexes) ? rowsIndexes : getRowsIndexes(sheet);
		this.excelManager = excelManager;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public Sheet getPoiSheet() {
		return sheet;
	}

	public List<Integer> getColumnsIndexes() {
		return new ArrayList<>(getColumnsMap().keySet());
	}

	public List<Integer> getRowsIndexes() {
		return new ArrayList<>(getRowsMap().keySet());
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public ExcelManager getExcelManager() {
		return this.excelManager;
	}

	public int getRowsNumber() {
		return getRowsMap().size();
	}

	public int getColumnsNumber() {
		return getColumnsIndexes().size();
	}

	public int getFirstRowIndex() {
		return getRowsIndexes().get(0);
	}

	public int getLastRowIndex() {
		List<Integer> rowsIndexes = getRowsIndexes();
		return rowsIndexes.get(rowsIndexes.size() - 1);
	}

	public int getFirstColumnIndex() {
		return getColumnsIndexes().get(0);
	}

	public int getLastColumnIndex() {
		List<Integer> columnsIndexes = getColumnsIndexes();
		return columnsIndexes.get(columnsIndexes.size() - 1);
	}

	public ExcelRow getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public ExcelRow getLastRow() {
		return getRow(getLastRowIndex());
	}

	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> List<R> getRows() {
		return new ArrayList<>((Collection<R>) getRowsMap().values());
	}

	protected abstract <R extends ExcelRow> Map<Integer, R> getRowsMap();

	protected abstract <C extends ExcelColumn> Map<Integer, C> getColumnsMap();

	public ExcelCell getFirstCell(int rowIndex) {
		return getRow(rowIndex).getFirstCell();
	}

	public ExcelCell getLastCell(int rowIndex) {
		return getRow(rowIndex).getLastCell();
	}

	/**
	 * Get {@link SheetRow} object by table's row index. Index starts from 1 (0 belongs to header)
	 */
	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %1$s on sheet %2$s or it's empty", rowIndex, getPoiSheet().getSheetName()).isTrue();
		return (R) getRowsMap().get(rowIndex);
	}

	public boolean hasRow(int rowIndex) {
		return getRowsMap().containsKey(rowIndex);
	}

	public ExcelCell getCell(int rowIndex, int columnIndex) {
		return getRow(rowIndex).getCell(columnIndex);
	}

	public List<Object> getRowValues(int rowIndex) {
		return getRowValues(rowIndex, 1);
	}

	public List<Object> getRowValues(int rowIndex, int fromColumnIndex) {
		return getRowValues(rowIndex, fromColumnIndex, getRow(rowIndex).getLastCellIndex());
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
		return IntStream.rangeClosed(fromColumnIndex, toColumnIndex).filter(getRow(rowIndex)::hasCell).mapToObj(getRow(rowIndex)::getValue).collect(Collectors.toList());
	}

	public List<String> getRowStringValues(int rowIndex) {
		return getRowStringValues(rowIndex, 1);
	}

	public List<String> getRowStringValues(int rowIndex, int fromColumnIndex) {
		return getRowStringValues(rowIndex, fromColumnIndex, getRow(rowIndex).getLastCellIndex());
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
		return IntStream.rangeClosed(fromColumnIndex, toColumnIndex).filter(getRow(rowIndex)::hasCell).mapToObj(getRow(rowIndex)::getStringValue).collect(Collectors.toList());
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
				int rowNumber = foundRowsWithPartialMatch.get(bestMatch).getLeft().getRowNum() + 1;
				String missedVales = foundRowsWithPartialMatch.get(bestMatch).getRight();
				errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed cell values: %3$s", errorMessage, rowNumber, missedVales);
			}
			throw new IstfException(errorMessage);
		}

		Row poiRow = foundRows.get(foundRows.size() - 1);
		return new SheetRow(poiRow, poiRow.getRowNum() + 1, getExcelManager().getSheet(getPoiSheet().getSheetName()));
	}

	public CellsArea registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getRows().forEach(r -> r.registerCellType(cellTypes));
		return this;
	}

	public CellsArea excludeColumns(Integer... columnsIndexes) {
		for (Integer cIndex : columnsIndexes) {
			this.columnsIndexes.remove(cIndex);
			for (ExcelRow row : getRows()) {
				row.getCellsMap().remove(cIndex);
			}
		}
		return this;
	}

	public CellsArea excludeRows(Integer... rowsIndexes) {
		for (int rIndex : rowsIndexes) {
			assertThat(hasRow(rIndex)).as("There is no row number %s", rIndex).isTrue();
			getRowsMap().remove(rIndex);
		}
		return this;
	}

	public CellsArea clearColumns(Integer... columnsIndexes) {
		for (ExcelRow row : getRows()) {
			for (Integer index : columnsIndexes) {
				row.getCell(index).clear();
			}
		}
		return this;
	}

	public CellsArea clearRows(Integer... rowsIndexes) {
		for (Integer index : rowsIndexes) {
			getRow(index).clear();
		}
		return this;
	}

	public CellsArea copyColumn(int columnIndex, int destinationColumnIndex) {
		for (ExcelRow row : getRows()) {
			row.getCell(columnIndex).copy(row.getIndex(), row.getCell(destinationColumnIndex).getColumnIndex());
		}
		return this;
	}

	public CellsArea copyRow(int rowIndex, int destinationRowIndex) {
		getRow(rowIndex).copy(destinationRowIndex);
		return this;
	}

	public CellsArea deleteColumns(Integer... columnsIndexes) {
		//TODO-dchubkov: implement delete columns
		throw new NotImplementedException("Columns deletion is not implemented yet");
	}

	public CellsArea deleteRows(Integer... rowsIndexes) {
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

	public CellsArea save() {
		getExcelManager().save();
		return this;
	}

	public CellsArea save(File destinationFile) {
		getExcelManager().save(destinationFile);
		return this;
	}

	public CellsArea close() {
		getExcelManager().close();
		return this;
	}

	public CellsArea saveAndClose() {
		getExcelManager().saveAndClose();
		return this;
	}

	public CellsArea saveAndClose(File destinationFile) {
		getExcelManager().saveAndClose(destinationFile);
		return this;
	}

	private Set<Integer> getColumnsIndexes(Sheet sheet) {
		int maxColumnsNumber = 1;
		for (Row row : sheet) {
			if (row.getLastCellNum() > maxColumnsNumber) {
				maxColumnsNumber = row.getLastCellNum();
			}
		}
		return IntStream.rangeClosed(1, maxColumnsNumber).boxed().collect(Collectors.toSet());
	}

	private Set<Integer> getRowsIndexes(Sheet sheet) {
		return IntStream.rangeClosed(1, sheet.getLastRowNum() + 1).boxed().collect(Collectors.toSet());
	}
}
