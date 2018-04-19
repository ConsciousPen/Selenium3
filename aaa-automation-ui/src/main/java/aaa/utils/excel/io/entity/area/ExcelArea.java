package aaa.utils.excel.io.entity.area;

import static java.util.stream.Collectors.collectingAndThen;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import com.google.common.collect.ImmutableSet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.iterator.RowIterator;
import toolkit.exceptions.IstfException;

public abstract class ExcelArea<CELL extends ExcelCell, ROW extends ExcelRow<CELL>, COLUMN extends ExcelColumn<CELL>> implements Writable, Iterable<ROW> {
	private final Sheet sheet;
	private final ExcelManager excelManager;

	private List<Integer> columnsIndexesOnSheet;
	private List<Integer> rowsIndexesOnSheet;
	private List<CellType<?>> cellTypes;

	private boolean considerRowsOnComparison;
	private boolean considerColumnsOnComparison;

	//private ImmutableSortedMap<Integer, ROW> areaIndexesAndRowsMap;
	//private ImmutableSortedMap<Integer, COLUMN> areaIndexesAndColumnsMap;

	private List<ROW> rows;
	private List<COLUMN> columns;

	protected ExcelArea(Sheet sheet, List<Integer> columnsIndexes, List<Integer> rowsIndexes, ExcelManager excelManager) {
		this(sheet, columnsIndexes, rowsIndexes, excelManager, excelManager.getCellTypes());
	}

	protected ExcelArea(Sheet sheet, List<Integer> columnsIndexesOnSheet, List<Integer> rowsIndexesOnSheet, ExcelManager excelManager, List<CellType<?>> cellTypes) {
		this.sheet = sheet;
		this.columnsIndexesOnSheet = CollectionUtils.isNotEmpty(columnsIndexesOnSheet)
				//? columnsIndexesOnSheet.stream().sorted().collect(collectingAndThen(Collectors.toCollection(LinkedHashSet::new), ImmutableList::copyOf))
				? columnsIndexesOnSheet.stream().sorted().collect(collectingAndThen(Collectors.toCollection(LinkedHashSet::new), ArrayList::new))
				: getColumnsIndexes(sheet);
		this.rowsIndexesOnSheet = CollectionUtils.isNotEmpty(rowsIndexesOnSheet)
				? rowsIndexesOnSheet.stream().sorted().collect(collectingAndThen(Collectors.toCollection(LinkedHashSet::new), ArrayList::new))
				: getRowsIndexes(sheet);
		this.excelManager = excelManager;
		//this.cellTypes = ImmutableList.copyOf(cellTypes);
		this.cellTypes = new ArrayList<>(cellTypes);
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
		//return getAreaIndexesAndColumnsMap().keySet().asList();
		return getColumns().stream().map(CellsQueue::getIndex).collect(Collectors.toList());
	}

	public List<Integer> getRowsIndexes() {
		//return getAreaIndexesAndRowsMap().keySet().asList();
		return getRows().stream().map(CellsQueue::getIndex).collect(Collectors.toList());
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<CellType<?>> getCellTypes() {
		return Collections.unmodifiableList(this.cellTypes);
	}

	public int getRowsNumber() {
		//return getAreaIndexesAndRowsMap().size();
		return getRows().size();
	}

	public int getColumnsNumber() {
		//return getColumnsIndexes().size();
		return getColumns().size();
	}

	public int getFirstRowIndex() {
		return getRowsIndexes().get(0);
	}

	public int getLastRowIndex() {
		/*List<Integer> rowsIndexes = getRowsIndexes();
		return rowsIndexes.get(rowsIndexes.size() - 1);*/
		return getRowsIndexes().get(getRowsNumber() - 1);
	}

	public int getFirstColumnIndex() {
		return getColumnsIndexes().get(0);
	}

	public int getLastColumnIndex() {
		/*List<Integer> columnsIndexes = getColumnsIndexes();
		return columnsIndexes.get(columnsIndexes.size() - 1);*/
		return getColumnsIndexes().get(getColumnsNumber() - 1);
	}

	public ROW getFirstRow() {
		return getRow(getFirstRowIndex());
	}

	public ROW getLastRow() {
		return getRow(getLastRowIndex());
	}

	/*public List<ROW> getRows() {
		return getAreaIndexesAndRowsMap().values().asList();
	}*/

	/*public List<COLUMN> getColumns() {
		return getAreaIndexesAndColumnsMap().values().asList();
	}*/

	public boolean isEmpty() {
		return getRowsNumber() == 0 || getRows().stream().allMatch(ROW::isEmpty);
	}

	public boolean isRowsComparisonRuleSet() {
		return this.considerRowsOnComparison;
	}

	public boolean isColumnsComparisonRuleSet() {
		return this.considerColumnsOnComparison;
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<Integer> getColumnsIndexesOnSheet() {
		return Collections.unmodifiableList(this.columnsIndexesOnSheet);
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<Integer> getRowsIndexesOnSheet() {
		return Collections.unmodifiableList(this.rowsIndexesOnSheet);
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
		this.cellTypes = ImmutableSet.<CellType<?>>builder().addAll(getCellTypes()).add(cellTypes).build().asList();
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

	public CELL getFirstColumnCell(int rowIndex) {
		return getRow(rowIndex).getFirstCell();
	}

	public CELL getLastColumnCell(int rowIndex) {
		return getRow(rowIndex).getLastCell();
	}

	public ROW getRow(int rowIndex) {
		//assertThat(hasRow(rowIndex)).as("There is no row number %1$s int %2$s", rowIndex, this).isTrue();
		//return getAreaIndexesAndRowsMap().get(rowIndex);
		/*return getRows().stream().filter(r -> r.getIndex() == rowIndex).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no row number %1$s int %2$s", rowIndex, this)));*/
		for (ROW row : getRows()) {
			if (row.getIndex() == rowIndex) {
				return row;
			}
		}
		throw new IstfException(String.format("There is no row number %1$s int %2$s", rowIndex, this));
	}

	public COLUMN getColumn(int columnIndex) {
		//assertThat(hasColumn(columnIndex)).as("There is no column number %1$s in %2$s", columnIndex, this).isTrue();
		//return getAreaIndexesAndColumnsMap().get(columnIndex);

		/*return getColumns().stream().filter(c -> c.getIndex() == columnIndex).findFirst()
				.orElseThrow(() -> new IstfException(String.format("There is no column number %1$s in %2$s", columnIndex, this)));*/
		for (COLUMN column : getColumns()) {
			if (column.getIndex() == columnIndex) {
				return column;
			}
		}
		throw new IstfException(String.format("There is no column number %1$s in %2$s", columnIndex, this));
	}

	public boolean hasRow(int rowIndex) {
		//return getAreaIndexesAndRowsMap().containsKey(rowIndex);
		//return getRows().stream().anyMatch(r -> r.getIndex() == rowIndex);
		for (ROW row : getRows()) {
			if (row.getIndex() == rowIndex) {
				return true;
			}
		}
		return false;
	}

	public boolean hasColumn(int columnIndex) {
		//return getAreaIndexesAndColumnsMap().containsKey(columnIndex);
		//return getColumns().stream().anyMatch(c -> c.getIndex() == columnIndex);
		for (COLUMN column : getColumns()) {
			if (column.getIndex() == columnIndex) {
				return true;
			}
		}
		return false;
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
		for (ROW row : getRows()) {
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
		List<Integer> columnsIndexesToExclude = Arrays.asList(columnsIndexes);
		//TODO-dchubkov: replace with forEach and throw exception with exact missing index
		assertThat(columnsIndexes).as("Can't exclude columns with indexes %s", columnsIndexesToExclude).allMatch(this::hasColumn);

		//ImmutableSortedMap.Builder<Integer, COLUMN> areaIndexesAndColumnsBuilder = ImmutableSortedMap.naturalOrder();

		getRows().forEach(r -> r.removeCellsIndexes(columnsIndexes));

		//List<Integer> newColumnsIndexesOnSheet = new ArrayList<>(this.columnsIndexesOnSheet);
		for (Integer columnIndex : columnsIndexesToExclude) {
			this.columnsIndexesOnSheet.remove(new Integer(getColumn(columnIndex).getIndexOnSheet()));
		}

		this.columns.removeIf(c -> columnsIndexesToExclude.contains(c.getIndex()));
		//this.columnsIndexesOnSheet = newColumnsIndexesOnSheet;

		//getColumns().stream().filter(c -> newColumnsIndexesOnSheet.contains(c.getIndexOnSheet())).forEach(c -> areaIndexesAndColumnsBuilder.put(c.getIndex(), c));
		////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>this.areaIndexesAndColumnsMap = areaIndexesAndColumnsBuilder.build();
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> excludeRows(Integer... rowsIndexes) {
		List<Integer> rowsIndexesToExclude = Arrays.asList(rowsIndexes);
		//TODO-dchubkov: replace with forEach and throw exception with exact missing index
		assertThat(rowsIndexes).as("Can't exclude rows with indexes %s", rowsIndexesToExclude).allMatch(this::hasRow);

		//ImmutableSortedMap.Builder<Integer, ROW> areaIndexesAndRowsBuilder = ImmutableSortedMap.naturalOrder();

		getColumns().forEach(c -> c.removeCellsIndexes(rowsIndexes));

		//List<Integer> newRowsIndexesOnSheet = new ArrayList<>(this.rowsIndexesOnSheet);
		for (Integer rowIndex : rowsIndexesToExclude) {
			this.rowsIndexesOnSheet.remove(new Integer(getRow(rowIndex).getIndexOnSheet()));
		}

		this.rows.removeIf(r -> rowsIndexesToExclude.contains(r.getIndex()));
		//getRows().stream().filter(r -> newRowsIndexesOnSheet.contains(r.getIndexOnSheet())).forEach(r -> areaIndexesAndRowsBuilder.put(r.getIndex(), r));
		//this.rowsIndexesOnSheet = newRowsIndexesOnSheet;

		////>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>this.areaIndexesAndRowsMap = areaIndexesAndRowsBuilder.build();
		return this;
	}

	public ExcelArea<CELL, ROW, COLUMN> clearColumns(Integer... columnsIndexes) {
		for (ROW row : getRows()) {
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
		for (ROW row : getRows()) {
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
		Set<Integer> uniqueSortedRowIndexes = Arrays.stream(rowsIndexes).sorted().collect(Collectors.toCollection(LinkedHashSet::new));
		Sheet sheet = getPoiSheet();
		for (int index : uniqueSortedRowIndexes) {
			assertThat(hasRow(index - rowsShifts)).as("There is no row number %1$s in %2$s", index, this).isTrue();
			sheet.shiftRows(index - rowsShifts, sheet.getLastRowNum(), -1);
			rowsShifts++;
		}
		excludeRows(rowsIndexes);
		return this;
	}

	//protected abstract ImmutableSortedMap<Integer, ROW> gatherAreaIndexesAndRowsMap(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes);

	//protected abstract ImmutableSortedMap<Integer, COLUMN> gatherAreaIndexesAndColumnsMap(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes);

	/*private ImmutableSortedMap<Integer, COLUMN> getAreaIndexesAndColumnsMap() {
		if (this.areaIndexesAndColumnsMap == null) {
			this.areaIndexesAndColumnsMap = gatherAreaIndexesAndColumnsMap(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return this.areaIndexesAndColumnsMap;
	}

	private ImmutableSortedMap<Integer, ROW> getAreaIndexesAndRowsMap() {
		if (this.areaIndexesAndRowsMap == null) {
			this.areaIndexesAndRowsMap = gatherAreaIndexesAndRowsMap(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return this.areaIndexesAndRowsMap;
	}*/

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<ROW> getRows() {
		if (this.rows == null) {
			this.rows = gatherRows(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return Collections.unmodifiableList(this.rows);
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<COLUMN> getColumns() {
		if (this.columns == null) {
			this.columns = gatherColumns(this.rowsIndexesOnSheet, this.columnsIndexesOnSheet, this.cellTypes);
		}
		return Collections.unmodifiableList(this.columns);
	}

	protected abstract List<ROW> gatherRows(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes);

	protected abstract List<COLUMN> gatherColumns(List<Integer> rowsIndexesOnSheet, List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes);

	private List<Integer> getColumnsIndexes(Sheet sheet) {
		int maxCellsNumber = 1;
		for (Row row : sheet) {
			if (row.getLastCellNum() > maxCellsNumber) {
				maxCellsNumber = row.getLastCellNum();
			}
		}
		//List<Integer> columnsIndexes = IntStream.rangeClosed(1, maxCellsNumber).boxed().collect(Collectors.toList());
		//return ImmutableList.copyOf(columnsIndexes);
		return IntStream.rangeClosed(1, maxCellsNumber).boxed().collect(Collectors.toList());
	}

	private List<Integer> getRowsIndexes(Sheet sheet) {
		//List<Integer> rowsIndexes = IntStream.rangeClosed(1, sheet.getLastRowNum() + 1).boxed().collect(Collectors.toList());
		//return ImmutableList.copyOf(rowsIndexes);
		return IntStream.rangeClosed(1, sheet.getLastRowNum() + 1).boxed().collect(Collectors.toList());
	}
}
