package aaa.utils.excel.io.entity.area;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import aaa.utils.excel.io.entity.queue.CellsQueue;
import aaa.utils.excel.io.entity.queue.ExcelRow;
import toolkit.exceptions.IstfException;

public abstract class CellsArea<E extends ExcelCell, R extends ExcelRow<E>, C extends CellsQueue<E>> implements Writable, Iterable<R> {
	protected Sheet sheet;
	protected Set<Integer> columnsIndexes;
	protected Set<Integer> rowsIndexes;
	protected ExcelManager excelManager;
	protected Set<CellType<?>> cellTypes;
	protected boolean considerRowsOnComparison;
	protected boolean considerColumnsOnComparison;

	protected CellsArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager) {
		this(sheet, columnsIndexes, rowsIndexes, excelManager, excelManager.getCellTypes());
	}

	protected CellsArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		this.sheet = sheet;
		this.columnsIndexes = CollectionUtils.isNotEmpty(columnsIndexes) ? columnsIndexes : getColumnsIndexes(sheet);
		this.rowsIndexes = CollectionUtils.isNotEmpty(rowsIndexes) ? rowsIndexes : getRowsIndexes(sheet);
		this.excelManager = excelManager;
		this.cellTypes = new HashSet<>(cellTypes);
		this.considerRowsOnComparison = true;
		this.considerColumnsOnComparison = true;
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

	public R getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public R getLastRow() {
		return getRow(getLastRowIndex());
	}

	public List<R> getRows() {
		return new ArrayList<>(getRowsMap().values());
	}

	public List<C> getColumns() {
		return new ArrayList<>(getColumnsMap().values());
	}

	protected abstract Map<Integer, R> getRowsMap();

	protected abstract Map<Integer, C> getColumnsMap();

	@Override
	@Nonnull
	public Iterator<R> iterator() {
		return new RowIterator<>(getRowsIndexes(), this::getRow);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		List<Boolean> conditions = new ArrayList<>();
		CellsArea<?, ?, ?> otherArea = (CellsArea<?, ?, ?>) other;
		conditions.add(Objects.equals(getPoiSheet().getSheetName(), otherArea.getPoiSheet().getSheetName()));
		conditions.add(Objects.equals(getCellTypes(), otherArea.getCellTypes()));
		if (considerRowsOnComparison) {
			conditions.add(Objects.equals(getRowsIndexes(), otherArea.getRowsIndexes()));
		}
		if (considerColumnsOnComparison) {
			conditions.add(Objects.equals(getColumnsIndexes(), otherArea.getColumnsIndexes()));
		}

		return !conditions.contains(false);
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(getPoiSheet().getSheetName(), getCellTypes());
		if (considerRowsOnComparison) {
			hash += Objects.hash(getRowsIndexes());
		}
		if (considerColumnsOnComparison) {
			hash += Objects.hash(getColumnsIndexes());
		}
		return hash;
	}

	@Override
	public ExcelManager getExcelManager() {
		return excelManager;
	}

	@Override
	public String toString() {
		return "CellsArea{" +
				"sheetName=" + getPoiSheet().getSheetName() +
				", columnsIndexes=" + getColumnsIndexes() +
				", rowsIndexes=" + getRowsIndexes() +
				", cellTypes=" + getCellTypes() +
				'}';
	}

	public CellsArea<E, R, C> setComparisonRules(boolean considerRowsOnComparison, boolean considerColumnsOnComparison) {
		this.considerRowsOnComparison = considerRowsOnComparison;
		this.considerColumnsOnComparison = considerColumnsOnComparison;
		return this;
	}

	public E getFirstColumnCell(int rowIndex) {
		return getRow(rowIndex).getFirstCell();
	}

	public E getLastColumnCell(int rowIndex) {
		return getRow(rowIndex).getLastCell();
	}

	public R getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %1$s on sheet %2$s", rowIndex, getPoiSheet().getSheetName()).isTrue();
		return getRowsMap().get(rowIndex);
	}

	public C getColumn(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column number %1$s on sheet %2$s", columnIndex, getPoiSheet().getSheetName()).isTrue();
		return getColumnsMap().get(columnIndex);
	}

	public boolean hasRow(int rowIndex) {
		return getRowsMap().containsKey(rowIndex);
	}

	public boolean hasColumn(int columnIndex) {
		return getColumnsMap().containsKey(columnIndex);
	}

	public E getCell(int rowIndex, int columnIndex) {
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

	public R getRow(String... valuesInCells) {
		return getRow(false, false, valuesInCells);
	}

	public R getRow(boolean isLowest, boolean ignoreCase, String... valuesInCells) {
		Set<String> initialExpectedValues = new HashSet<>(Arrays.asList(valuesInCells));
		List<R> foundRows = new ArrayList<>();
		Map<Integer, Pair<R, String>> foundRowsWithPartialMatch = new LinkedHashMap<>();
		for (R row : this) {
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
