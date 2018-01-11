package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
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

	protected CellsArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
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

	public CellsQueue getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public CellsQueue getLastRow() {
		return getRow(getLastRowIndex());
	}

	@SuppressWarnings("unchecked")
	public <Q extends CellsQueue> List<Q> getRows() {
		return new ArrayList<>((Collection<Q>) getRowsMap().values());
	}

	@SuppressWarnings("unchecked")
	public <Q extends CellsQueue> List<Q> getColumns() {
		return new ArrayList<>((Collection<Q>) getColumnsMap().values());
	}

	protected abstract <Q extends CellsQueue> Map<Integer, Q> getRowsMap();

	protected abstract <Q extends ExcelColumn> Map<Integer, Q> getColumnsMap();

	public ExcelCell getFirstColumnCell(int rowIndex) {
		return getRow(rowIndex).getFirstCell();
	}

	public ExcelCell getLastColumnCell(int rowIndex) {
		return getRow(rowIndex).getLastCell();
	}

	@SuppressWarnings("unchecked")
	public <Q extends CellsQueue> Q getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %1$s on sheet %2$s", rowIndex, getPoiSheet().getSheetName()).isTrue();
		return (Q) getRowsMap().get(rowIndex);
	}

	@SuppressWarnings("unchecked")
	public <Q extends CellsQueue> Q getColumn(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column number %1$s on sheet %2$s", columnIndex, getPoiSheet().getSheetName()).isTrue();
		return (Q) getColumnsMap().get(columnIndex);
	}

	public boolean hasRow(int rowIndex) {
		return getRowsMap().containsKey(rowIndex);
	}

	public boolean hasColumn(int columnIndex) {
		return getColumnsMap().containsKey(columnIndex);
	}

	public ExcelCell getCell(int rowIndex, int columnIndex) {
		return getRow(rowIndex).getCell(columnIndex);
	}

	public Object getValue(int rowIndex, int columnIndex) {
		return getCell(rowIndex, columnIndex).getValue();
	}

	public String getStringValue(int rowIndex, int columnIndex) {
		return getCell(rowIndex, columnIndex).getStringValue();
	}

	public List<String> getRowStringValues(int rowIndex) {
		return getRowStringValues(rowIndex, 1);
	}

	public List<String> getRowStringValues(int rowIndex, int fromColumnIndex) {
		return getRowStringValues(rowIndex, fromColumnIndex, getLastColumnIndex());
	}

	/**
	 * Get all String cell values from provided {@code rowIndex} starting inclusively from {@code fromColumnIndex} and up to inclusively {@code toColumnIndex}
	 *
	 * @param rowIndex row number from which values should be taken, index starts from 1
	 * @param fromColumnIndex the inclusive initial column number on sheet to get values from. Should be positive, index starts from 1
	 * @param toColumnIndex the inclusive last column number on sheet to get values from. Should be greater or equal to {@code fromColumnIndex}
	 *
	 * @return List of String cell values found on provided {@code rowIndex} within {@code fromColumnIndex/toColumnIndex} bounds
	 */
	public List<String> getRowStringValues(int rowIndex, int fromColumnIndex, int toColumnIndex) {
		assertThat(fromColumnIndex).as("Start column index should be greater than 0").isPositive();
		assertThat(toColumnIndex).as("End column index should be greater or equal to start column index").isGreaterThanOrEqualTo(fromColumnIndex);
		return IntStream.rangeClosed(fromColumnIndex, toColumnIndex).filter(getRow(rowIndex)::hasCell).mapToObj(getRow(rowIndex)::getStringValue).collect(Collectors.toList());
	}

	public List<Object> getColumnStringValues(int columnIndex) {
		return getColumnStringValues(columnIndex, 1);
	}

	public List<Object> getColumnStringValues(int columnIndex, int fromRowIndex) {
		return getColumnStringValues(columnIndex, fromRowIndex, getLastRowIndex());
	}

	/**
	 * Get all String cell values from provided {@code columnIndex} starting inclusively from {@code fromRowIndex} and up to inclusively {@code toRowIndex}
	 *
	 * @param columnIndex column number from which values should be taken, index starts from 1
	 * @param fromRowIndex the inclusive initial row number on sheet to get values from. Should be positive, index starts from 1
	 * @param toRowIndex the inclusive last row number on sheet to get values from. Should be greater or equal to {@code fromRowIndex}
	 *
	 * @return List of String cell values found on provided {@code columnIndex} within {@code fromRowIndex/toRowIndex} bounds
	 */
	public List<Object> getColumnStringValues(int columnIndex, int fromRowIndex, int toRowIndex) {
		assertThat(fromRowIndex).as("Start row index should be greater than 0").isPositive();
		assertThat(toRowIndex).as("End row index should be greater or equal to start row index").isGreaterThanOrEqualTo(fromRowIndex);
		return IntStream.rangeClosed(fromRowIndex, toRowIndex).filter(getColumn(columnIndex)::hasCell).mapToObj(getColumn(columnIndex)::getValue).collect(Collectors.toList());
	}

	public CellsQueue getRow(String... valuesInCells) {
		return getRow(false, valuesInCells);
	}

	public CellsQueue getRow(boolean isLowest, String... valuesInCells) {
		Set<String> initialExpectedValues = new HashSet<>(Arrays.asList(valuesInCells));
		List<CellsQueue> foundRows = new ArrayList<>();
		Map<Integer, Pair<CellsQueue, String>> foundRowsWithPartialMatch = new LinkedHashMap<>();
		for (CellsQueue row : getRows()) {
			List<String> actualValues = row.getStringValues();
			Set<String> expectedValues = new HashSet<>(initialExpectedValues);
			if (actualValues.containsAll(expectedValues)) {
				foundRows.add(row);
			} else if (expectedValues.removeAll(actualValues)) {
				foundRowsWithPartialMatch.put(expectedValues.size(), Pair.of(row, expectedValues.toString()));
			}

			if (!foundRows.isEmpty() && !isLowest) {
				break;
			}
		}

		if (foundRows.isEmpty()) {
			String errorMessage = String.format("Unable to find row with all these values: %1$s on sheet \"%2$s\"", initialExpectedValues, getPoiSheet().getSheetName());
			if (!foundRowsWithPartialMatch.isEmpty()) {
				int bestMatch = foundRowsWithPartialMatch.keySet().stream().min(Integer::compare).get();
				int rowNumber = foundRowsWithPartialMatch.get(bestMatch).getLeft().getIndex();
				String missedVales = foundRowsWithPartialMatch.get(bestMatch).getRight();
				errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed cell values: %3$s", errorMessage, rowNumber, missedVales);
			}
			throw new IstfException(errorMessage);
		}

		return foundRows.get(foundRows.size() - 1);
	}

	public CellsArea registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getRows().forEach(r -> r.registerCellType(cellTypes));
		return this;
	}

	public CellsArea excludeColumns(Integer... columnsIndexes) {
		for (Integer cIndex : columnsIndexes) {
			this.columnsIndexes.remove(cIndex);
			for (CellsQueue row : getRows()) {
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
		for (CellsQueue row : getRows()) {
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
		for (CellsQueue row : getRows()) {
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
		int maxCellsNumber = 1;
		for (Row row : sheet) {
			if (row.getLastCellNum() > maxCellsNumber) {
				maxCellsNumber = row.getLastCellNum();
			}
		}
		return IntStream.rangeClosed(1, maxCellsNumber).boxed().collect(Collectors.toSet());
	}

	private Set<Integer> getRowsIndexes(Sheet sheet) {
		return IntStream.rangeClosed(1, sheet.getLastRowNum() + 1).boxed().collect(Collectors.toSet());
	}
}
