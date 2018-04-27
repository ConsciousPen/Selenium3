package aaa.utils.excel.io.entity.area;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableSet;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.celltype.DateCellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.iterator.CellIterator;
import toolkit.exceptions.IstfException;

public abstract class CellsQueue<CELL extends ExcelCell> implements Writable, Iterable<CELL> {
	private final int queueIndexInArea;
	private final int queueIndexOnSheet;
	private final ExcelArea<CELL, ?, ?> excelArea;
	//private ImmutableSortedMap<Integer, CELL> queueIndexesAndCellsMap;
	protected List<CELL> cells;
	private List<CellType<?>> cellTypes;
	private List<Integer> cellsIndexesOnSheet;

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, List<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(queueIndexInArea, queueIndexOnSheet, cellsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, List<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, List<CellType<?>> cellTypes) {
		this.queueIndexInArea = queueIndexInArea;
		this.queueIndexOnSheet = queueIndexOnSheet;
		this.excelArea = excelArea;
		//this.cellTypes = ImmutableList.copyOf(cellTypes);
		this.cellTypes = new ArrayList<>(cellTypes);
		/*LinkedHashSet<Integer> cellsIndexesSet = cellsIndexesOnSheet.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
		this.cellsIndexesOnSheet = ImmutableList.copyOf(cellsIndexesSet);*/
		this.cellsIndexesOnSheet = cellsIndexesOnSheet.stream().sorted().collect(collectingAndThen(Collectors.toCollection(LinkedHashSet::new), ArrayList::new));
	}

	public String getSheetName() {
		return getArea().getSheetName();
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<CELL> getCells() {
		//return getQueueIndexesAndCellsMap().values().asList();
		if (this.cells == null) {
			this.cells = gatherCells(getCellsIndexesOnSheet(), getCellTypes());
		}
		return Collections.unmodifiableList(this.cells);
	}

	public abstract List<Integer> getCellsIndexes();

	public int getFirstCellIndex() {
		return getCellsIndexes().get(0);
	}

	public int getLastCellIndex() {
		/*List<Integer> cellsIndexes = getCellsIndexes();
		return cellsIndexes.get(cellsIndexes.size() - 1);*/
		return getCellsIndexes().get(getCellsNumber() - 1);
	}

	public CELL getFirstCell() {
		return getCell(getFirstCellIndex());
	}


	/*public List<Integer> getCellsIndexes() {
		//return getQueueIndexesAndCellsMap().keySet().asList();
		return getCells().stream().map(c -> c.getr)
	}*/

	public CELL getLastCell() {
		return getCell(getLastCellIndex());
	}

	public int getCellsNumber() {
		//return getQueueIndexesAndCellsMap().size();
		return getCells().size();
	}

	public boolean isEmpty() {
		return getCells().stream().allMatch(ExcelCell::isEmpty);
	}

	public List<Object> getValues() {
		return getCells().stream().map(ExcelCell::getValue).collect(toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(toList());
	}

	public int getSum() {
		return getSum(getCellsIndexes().toArray(new Integer[getCellsNumber()]));
	}

	public int getMaxIntValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).max().orElse(0);
	}

	public int getMinIntValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).min().orElse(0);
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<CellType<?>> getCellTypes() {
		return Collections.unmodifiableList(this.cellTypes);
	}

	public int getIndex() {
		return this.queueIndexInArea;
	}

	public int getIndexOnSheet() {
		return this.queueIndexOnSheet;
	}

	//@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<Integer> getCellsIndexesOnSheet() {
		return Collections.unmodifiableList(this.cellsIndexesOnSheet);
	}

	protected ExcelArea<CELL, ?, ?> getArea() {
		return this.excelArea;
	}

	@Override
	@Nonnull
	public Iterator<CELL> iterator() {
		return new CellIterator<>(this);
	}

	@Override
	public ExcelManager getExcelManager() {
		return getArea().getExcelManager();
	}

	@Override
	public String toString() {
		return "CellsQueue{" +
				"sheetName=" + getSheetName() +
				", queueIndex=" + getIndex() +
				", cellsNumber=" + getCellsNumber() +
				", cellTypes=" + getCellTypes() +
				", values=" + getStringValues() +
				'}';
	}

	public abstract List<CELL> getCellsByIndexes(List<Integer> cellsIndexesInQueue);

	public CELL getCellByValue(Object expectedValue) {
		for (CELL cell : getCells()) {
			if (cell.hasValue(expectedValue)) {
				return cell;
			}
		}
		throw new IstfException(String.format("There is no cell with \"%1$s\" value in %2$s", expectedValue, this));
	}

	public <T> CELL getCellByValue(T expectedValue, CellType<T> cellType) {
		for (CELL cell : getCells()) {
			if (cell.hasValue(expectedValue, cellType)) {
				return cell;
			}
		}
		throw new IstfException(String.format("There is no cell with \"%1$s\" value of %2$s type in %3$s", expectedValue, cellType, this));
	}

	public <T extends Temporal> CELL getCellWithDate(T expectedValue, DateCellType<T> cellType, List<DateTimeFormatter> dateTimeFormatters) {
		for (CELL cell : getCells()) {
			if (cell.hasDateValue(expectedValue, cellType, dateTimeFormatters)) {
				return cell;
			}
		}
		throw new IstfException(String.format("There is no cell with \"%1$s\" value of %2$s type%3$s in %4$s",
				expectedValue, cellType, dateTimeFormatters.isEmpty() ? "" : " with date formatters: " + dateTimeFormatters, this));
	}

	public int getSum(Integer... cellsIndexesInQueue) {
		List<Integer> cellsIndexesList = Arrays.asList(cellsIndexesInQueue);
		return getCells().stream().filter(c -> cellsIndexesList.contains(c.getColumnIndex()) && !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).sum();
	}

	public boolean isEmpty(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).isEmpty();
	}

	/*public CELL getCell(int cellIndexInQueue) {
		assertThat(hasCell(cellIndexInQueue)).as("There is no cell with %1$s index in %2$s", cellIndexInQueue, this).isTrue();
		return getQueueIndexesAndCellsMap().get(cellIndexInQueue);
	}*/

	public abstract CELL getCell(int cellIndexInQueue);

	public Object getValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getValue();
	}

	public String getStringValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getStringValue();
	}

	public Boolean getBoolValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getBoolValue();
	}

	public Integer getIntValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getIntValue();
	}

	public Double getDoubleValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getDoubleValue();
	}

	public LocalDateTime getDateValue(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).getDateValue();
	}

	public LocalDateTime getDateValue(int cellIndexInQueue, List<DateTimeFormatter> dateTimeFormatters) {
		return getCell(cellIndexInQueue).getDateValue(dateTimeFormatters);
	}

	public CellsQueue<CELL> setValue(int cellIndexInQueue, Object value) {
		getCell(cellIndexInQueue).setValue(value);
		return this;
	}

	public <T> CellsQueue<CELL> setValue(int cellIndexInQueue, T value, CellType<T> valueType) {
		getCell(cellIndexInQueue).setValue(value, valueType);
		return this;
	}

	/*public boolean hasCell(int cellIndexInQueue) {
		return getQueueIndexesAndCellsMap().containsKey(cellIndexInQueue);
	}*/

	public abstract boolean hasCell(int cellIndexInQueue);

	public boolean hasValue(int cellIndexInQueue, Object expectedValue, DateTimeFormatter... formatters) {
		return getCell(cellIndexInQueue).hasValue(expectedValue);
	}

	public CellsQueue<CELL> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes = ImmutableSet.<CellType<?>>builder().addAll(getCellTypes()).add(cellTypes).build().asList();
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return this;
	}

	public abstract ExcelArea<CELL, ?, ?> exclude();

	public abstract ExcelArea<CELL, ?, ?> delete();

	public CellsQueue<CELL> clear() {
		getCells().forEach(ExcelCell::clear);
		return this;
	}

	public abstract CellsQueue<CELL> copy(int destinationQueueIndex);

	protected void removeCellsIndexes(Integer... cellsIndexesInQueue) {
		List<Integer> cellsIndexesToExclude = Arrays.asList(cellsIndexesInQueue);
		//TODO-dchubkov: replace with forEach and throw exception with exact missing index
		assertThat(cellsIndexesInQueue).as("Can't exclude cells with indexes %s", cellsIndexesToExclude).allMatch(this::hasCell);

		//List<Integer> newCellsIndexesOnSheet = new ArrayList<>(this.cellsIndexesOnSheet);
		///>>>>>>>>>>>>>>>>>>Map<Integer, CELL> newQueueIndexesAndCellsMap = new LinkedHashMap<>(getQueueIndexesAndCellsMap());

		for (Integer cellIndex : cellsIndexesInQueue) {
			this.cellsIndexesOnSheet.remove(getCellIndexOnSheet(cellIndex));
			///>>>>>>>>>>>>>>>>>>newQueueIndexesAndCellsMap.remove(cellIndex);
		}
		//this.cellsIndexesOnSheet = ImmutableList.copyOf(newCellsIndexesOnSheet);
		///>>>>>>>>>>>>>>>>>>this.queueIndexesAndCellsMap = ImmutableSortedMap.copyOf(newQueueIndexesAndCellsMap);
	}

	//protected abstract ImmutableSortedMap<Integer, CELL> gatherQueueIndexesAndCellsMap(List<Integer> cellsIndexesOnSheet, List<CellType<?>> cellTypes);

	protected abstract List<CELL> gatherCells(List<Integer> cellsIndexesOnSheet, List<CellType<?>> cellTypes);

	protected abstract Integer getCellIndexOnSheet(Integer cellIndexInQueue);

	/*private ImmutableSortedMap<Integer, CELL> getQueueIndexesAndCellsMap() {
		if (this.queueIndexesAndCellsMap == null) {
			this.queueIndexesAndCellsMap = gatherQueueIndexesAndCellsMap(getCellsIndexesOnSheet(), getCellTypes());
		}
		return this.queueIndexesAndCellsMap;
	}*/
}
