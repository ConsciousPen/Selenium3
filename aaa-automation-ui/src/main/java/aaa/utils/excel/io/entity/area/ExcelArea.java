package aaa.utils.excel.io.entity.area;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import toolkit.exceptions.IstfException;

public abstract class ExcelArea<CELL extends ExcelCell, ROW extends ExcelRow<CELL>, COLUMN extends ExcelColumn<CELL>> implements Writable, Iterable<ROW> {
	private Sheet sheet;
	private Set<Integer> columnsIndexesOnSheet;
	private Set<Integer> rowsIndexesOnSheet;
	private ExcelManager excelManager;
	private Set<CellType<?>> cellTypes;
	private boolean considerRowsOnComparison;
	private boolean considerColumnsOnComparison;
	private Map<Integer, ROW> areaIndexesAndRowsMap;
	private Map<Integer, COLUMN> areaIndexesAndColumnsMap;

	protected ExcelArea(Sheet sheet, Set<Integer> columnsIndexes, Set<Integer> rowsIndexes, ExcelManager excelManager) {
		this(sheet, columnsIndexes, rowsIndexes, excelManager, excelManager.getCellTypes());
	}

	protected ExcelArea(Sheet sheet, Set<Integer> columnsIndexesOnSheet, Set<Integer> rowsIndexesOnSheet, ExcelManager excelManager, Set<CellType<?>> cellTypes) {
		this.sheet = sheet;
		this.columnsIndexesOnSheet = CollectionUtils.isNotEmpty(columnsIndexesOnSheet)
				? new LinkedHashSet<>(columnsIndexesOnSheet.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)))
				: getColumnsIndexes(sheet);
		this.rowsIndexesOnSheet = CollectionUtils.isNotEmpty(rowsIndexesOnSheet)
				? new LinkedHashSet<>(rowsIndexesOnSheet.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new)))
				: getRowsIndexes(sheet);
		this.excelManager = excelManager;
		this.cellTypes = new HashSet<>(cellTypes);
		this.considerRowsOnComparison = true;
		this.considerColumnsOnComparison = true;
	}

	public Sheet getPoiSheet() {
		return this.sheet;
	}

	public String getSheetName() {
		return getPoiSheet().getSheetName();
	}

	public List<Integer> getColumnsIndexes() {
		return new ArrayList<>(getAreaIndexesAndColumnsMap().keySet());
	}

	public List<Integer> getRowsIndexes() {
		return new ArrayList<>(getAreaIndexesAndRowsMap().keySet());
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public int getRowsNumber() {
		return getAreaIndexesAndRowsMap().size();
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

	public ROW getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public ROW getLastRow() {
		return getRow(getLastRowIndex());
	}

	public List<ROW> getRows() {
		return new ArrayList<>(getAreaIndexesAndRowsMap().values());
	}

	public List<COLUMN> getColumns() {
		return new ArrayList<>(getAreaIndexesAndColumnsMap().values());
	}

	public boolean isEmpty() {
		return getRowsNumber() == 0 || getRows().stream().allMatch(ROW::isEmpty);
	}

	protected List<Integer> getColumnsIndexesOnSheet() {
		return new ArrayList<>(this.columnsIndexesOnSheet);
	}

	protected List<Integer> getRowsIndexesOnSheet() {
		return new ArrayList<>(this.rowsIndexesOnSheet);
	}

	@Override
	@Nonnull
	public Iterator<ROW> iterator() {
		return new RowIterator<>(this);
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
		ExcelArea<?, ?, ?> otherArea = (ExcelArea<?, ?, ?>) other;
		conditions.add(Objects.equals(getPoiSheet().getSheetName(), otherArea.getPoiSheet().getSheetName()));
		conditions.add(Objects.equals(getCellTypes(), otherArea.getCellTypes()));
		if (isRowsComparisonRuleSet() && otherArea.isRowsComparisonRuleSet()) {
			conditions.add(Objects.equals(getRowsIndexes(), otherArea.getRowsIndexes()));
		}
		if (isColumnsComparisonRuleSet() && otherArea.isColumnsComparisonRuleSet()) {
			conditions.add(Objects.equals(getColumnsIndexes(), otherArea.getColumnsIndexes()));
		}

		return !conditions.contains(false);
	}

	@Override
	public int hashCode() {
		int hash = Objects.hash(getPoiSheet().getSheetName(), getCellTypes());
		if (this.considerRowsOnComparison) {
			hash += Objects.hash(getRowsIndexes());
		}
		if (this.considerColumnsOnComparison) {
			hash += Objects.hash(getColumnsIndexes());
		}
		return hash;
	}

	@Override
	public ExcelManager getExcelManager() {
		return this.excelManager;
	}

	@Override
	public String toString() {
		return "ExcelArea{" +
				"sheetName=" + getSheetName() +
				", rowsNumber=" + getRowsNumber() +
				", columnsNumber=" + getColumnsNumber() +
				", cellTypes=" + getCellTypes() +
				'}';
	}

	public ExcelArea<CELL, ROW, COLUMN> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getRows().forEach(r -> r.registerCellType(cellTypes));
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> considerRowsOnComparison(boolean considerRowsOnComparison) {
		this.considerRowsOnComparison = considerRowsOnComparison;
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> considerColumnsOnComparison(boolean considerColumnsOnComparison) {
		this.considerColumnsOnComparison = considerColumnsOnComparison;
		return this;
	}

	public boolean isRowsComparisonRuleSet() {
		return this.considerRowsOnComparison;
	}

	public boolean isColumnsComparisonRuleSet() {
		return this.considerColumnsOnComparison;
	}

	public CELL getFirstColumnCell(int rowIndex) {
		return getRow(rowIndex).getFirstCell();
	}

	public CELL getLastColumnCell(int rowIndex) {
		return getRow(rowIndex).getLastCell();
	}

	public ROW getRow(int rowIndex) {
		assertThat(hasRow(rowIndex)).as("There is no row number %1$s int %2$s", rowIndex, this).isTrue();
		return getAreaIndexesAndRowsMap().get(rowIndex);
	}

	public COLUMN getColumn(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column number %1$s in %2$s", columnIndex, this).isTrue();
		return getAreaIndexesAndColumnsMap().get(columnIndex);
	}

	public boolean hasRow(int rowIndex) {
		return getAreaIndexesAndRowsMap().containsKey(rowIndex);
	}

	public boolean hasColumn(int columnIndex) {
		return getAreaIndexesAndColumnsMap().containsKey(columnIndex);
	}

	public CELL getCell(int rowIndex, int columnIndex) {
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

	public ROW getRow(String... valuesInCells) {
		return getRow(false, valuesInCells);
	}

	public ROW getRow(boolean ignoreCase, String... valuesInCells) {
		Set<String> expectedCellValues = new HashSet<>(Arrays.asList(valuesInCells));
		Pair<Integer, List<String>> bestMatchRowNumberWithMissedValues = null;
		for (ROW row : this) {
			List<String> cellValues = row.getStringValues();
			List<String> missedCellValues = new ArrayList<>(expectedCellValues);

			for (String cellValue : cellValues) {
				if (cellValue == null) {
					continue;
				}
				Predicate<String> cellValueEqualsToExpectedValue = ignoreCase ? cellValue::equalsIgnoreCase : cellValue::equals;
				missedCellValues.removeIf(cellValueEqualsToExpectedValue);
				if (missedCellValues.isEmpty()) {
					return row;
				}
			}

			if (bestMatchRowNumberWithMissedValues == null || bestMatchRowNumberWithMissedValues.getRight().size() > missedCellValues.size()) {
				bestMatchRowNumberWithMissedValues = Pair.of(row.getIndex(), missedCellValues);
			}
		}

		String errorMessage = String.format("Unable to find row with all these values: %1$s in %2$s", expectedCellValues, this);
		if (bestMatchRowNumberWithMissedValues.getRight().size() < expectedCellValues.size()) {
			errorMessage = String.format("%1$s\nBest match was found in row #%2$s with missed cell values: %3$s",
					errorMessage, bestMatchRowNumberWithMissedValues.getLeft(), bestMatchRowNumberWithMissedValues.getRight());
		}

		throw new IstfException(errorMessage);
	}

	public ExcelArea<CELL, ROW, COLUMN> excludeColumns(Integer... columnsIndexes) {
		assertThat(columnsIndexes).as("Can't exclude columns with indexes %s", Arrays.asList(columnsIndexes)).allMatch(this::hasColumn);
		for (Integer columnIndex : columnsIndexes) {
			for (ExcelRow<CELL> row : this) {
				row.removeCellsIndexes(columnIndex);
			}
			this.columnsIndexesOnSheet.remove(getColumn(columnIndex).getIndexOnSheet());
			getAreaIndexesAndColumnsMap().remove(columnIndex);
		}
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> excludeRows(Integer... rowsIndexes) {
		assertThat(rowsIndexes).as("Can't exclude rows with indexes %s", Arrays.asList(rowsIndexes)).allMatch(this::hasRow);
		for (Integer rowIndex : rowsIndexes) {
			for (ExcelColumn<CELL> column : getColumns()) {
				column.removeCellsIndexes(rowIndex);
			}
			this.rowsIndexesOnSheet.remove(getRow(rowIndex).getIndexOnSheet());
			getAreaIndexesAndRowsMap().remove(rowIndex);
		}
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> clearColumns(Integer... columnsIndexes) {
		for (ROW row : this) {
			for (Integer index : columnsIndexes) {
				row.getCell(index).clear();
			}
		}
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> clearRows(Integer... rowsIndexes) {
		for (Integer index : rowsIndexes) {
			getRow(index).clear();
		}
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> copyColumn(int columnIndex, int destinationColumnIndex) {
		for (ROW row : this) {
			row.getCell(columnIndex).copy(row.getIndex(), row.getCell(destinationColumnIndex).getColumnIndex());
		}
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> copyRow(int rowIndex, int destinationRowIndex) {
		getRow(rowIndex).copy(destinationRowIndex);
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> deleteColumns(Integer... columnsIndexes) {
		//TODO-dchubkov: implement delete columns
		throw new NotImplementedException("Columns deletion is not implemented yet");
	}

	public ExcelArea<CELL, ROW, COLUMN> deleteRows(Integer... rowsIndexes) {
		int rowsShifts = 0;
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toSet());
		Sheet sheet = getPoiSheet();
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %1$s in %2$s", index, this).isTrue();
			sheet.shiftRows(index - rowsShifts, sheet.getLastRowNum(), -1);
			rowsShifts++;
		}
		excludeRows(rowsIndexes);
		return this;
	}

	protected abstract Map<Integer, ROW> gatherAreaIndexesAndRowsMap(Set<Integer> rowsIndexesOnSheet, Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes);

	protected abstract Map<Integer, COLUMN> gatherAreaIndexesAndColumnsMap(Set<Integer> rowsIndexesOnSheet, Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes);

	private Map<Integer, COLUMN> getAreaIndexesAndColumnsMap() {
		if (this.areaIndexesAndColumnsMap == null) {
			this.areaIndexesAndColumnsMap = gatherAreaIndexesAndColumnsMap(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return this.areaIndexesAndColumnsMap;
	}

	private Map<Integer, ROW> getAreaIndexesAndRowsMap() {
		if (this.areaIndexesAndRowsMap == null) {
			this.areaIndexesAndRowsMap = gatherAreaIndexesAndRowsMap(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return this.areaIndexesAndRowsMap;
	}

	private Set<Integer> getColumnsIndexes(Sheet sheet) {
		int maxCellsNumber = 1;
		for (Row row : sheet) {
			if (row.getLastCellNum() > maxCellsNumber) {
				maxCellsNumber = row.getLastCellNum();
			}
		}
		return IntStream.rangeClosed(1, maxCellsNumber).boxed().collect(Collectors.toCollection(LinkedHashSet::new));
	}

	private Set<Integer> getRowsIndexes(Sheet sheet) {
		return IntStream.rangeClosed(1, sheet.getLastRowNum() + 1).boxed().collect(Collectors.toCollection(LinkedHashSet::new));
	}
}
