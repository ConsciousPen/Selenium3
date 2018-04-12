package aaa.utils.excel.io.entity.area;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedMap;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public abstract class CellsQueue<CELL extends ExcelCell> implements Writable, Iterable<CELL> {
	private final int queueIndexInArea;
	private final int queueIndexOnSheet;
	private final ExcelArea<CELL, ?, ?> excelArea;

	private List<CellType<?>> cellTypes;
	private List<Integer> cellsIndexesOnSheet;
	private ImmutableSortedMap<Integer, CELL> queueIndexesAndCellsMap;

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, List<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(queueIndexInArea, queueIndexOnSheet, cellsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, List<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, List<CellType<?>> cellTypes) {
		this.queueIndexInArea = queueIndexInArea;
		this.queueIndexOnSheet = queueIndexOnSheet;
		this.excelArea = excelArea;
		this.cellTypes = ImmutableList.copyOf(cellTypes);
		LinkedHashSet<Integer> cellsIndexesSet = cellsIndexesOnSheet.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
		this.cellsIndexesOnSheet = ImmutableList.copyOf(cellsIndexesSet);
	}

	public String getSheetName() {
		return getArea().getSheetName();
	}

	public List<CELL> getCells() {
		return getQueueIndexesAndCellsMap().values().asList();
	}

	public List<Integer> getCellsIndexes() {
		return getQueueIndexesAndCellsMap().keySet().asList();
	}

	public int getFirstCellIndex() {
		return getCellsIndexes().get(0);
	}

	public int getLastCellIndex() {
		List<Integer> cellsIndexes = getCellsIndexes();
		return cellsIndexes.get(cellsIndexes.size() - 1);
	}

	public CELL getFirstCell() {
		return getCell(getFirstCellIndex());
	}

	public CELL getLastCell() {
		return getCell(getLastCellIndex());
	}

	public int getCellsNumber() {
		return getQueueIndexesAndCellsMap().size();
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

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<CellType<?>> getCellTypes() {
		return this.cellTypes;
	}

	public int getIndex() {
		return this.queueIndexInArea;
	}

	public int getIndexOnSheet() {
		return this.queueIndexOnSheet;
	}

	@SuppressWarnings("AssignmentOrReturnOfFieldWithMutableType")
	public List<Integer> getCellsIndexesOnSheet() {
		return this.cellsIndexesOnSheet;
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

	public int getSum(Integer... cellsIndexesInQueue) {
		List<Integer> cellsIndexesList = Arrays.asList(cellsIndexesInQueue);
		return getCells().stream().filter(c -> cellsIndexesList.contains(c.getColumnIndex()) && !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).sum();
	}

	public boolean isEmpty(int cellIndexInQueue) {
		return getCell(cellIndexInQueue).isEmpty();
	}

	public CELL getCell(int cellIndexInQueue) {
		assertThat(hasCell(cellIndexInQueue)).as("There is no cell with %1$s index in %2$s", cellIndexInQueue, this).isTrue();
		return getQueueIndexesAndCellsMap().get(cellIndexInQueue);
	}

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

	public LocalDateTime getDateValue(int cellIndexInQueue, DateTimeFormatter... formatters) {
		return getCell(cellIndexInQueue).getDateValue(formatters);
	}

	public CellsQueue<CELL> setValue(int cellIndexInQueue, Object value) {
		getCell(cellIndexInQueue).setValue(value);
		return this;
	}

	public <T> CellsQueue<CELL> setValue(int cellIndexInQueue, T value, CellType<T> valueType) {
		getCell(cellIndexInQueue).setValue(value, valueType);
		return this;
	}

	public boolean hasCell(int cellIndexInQueue) {
		return getQueueIndexesAndCellsMap().containsKey(cellIndexInQueue);
	}

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
		List<Integer> newCellsIndexesOnSheet = new ArrayList<>(this.cellsIndexesOnSheet);
		Map<Integer, CELL> newQueueIndexesAndCellsMap = new LinkedHashMap<>(getQueueIndexesAndCellsMap());

		for (Integer cellIndex : cellsIndexesInQueue) {
			newCellsIndexesOnSheet.remove(getCellIndexOnSheet(cellIndex));
			newQueueIndexesAndCellsMap.remove(cellIndex);
		}

		this.cellsIndexesOnSheet = ImmutableList.copyOf(newCellsIndexesOnSheet);
		this.queueIndexesAndCellsMap = ImmutableSortedMap.copyOf(newQueueIndexesAndCellsMap);
	}

	protected abstract ImmutableSortedMap<Integer, CELL> gatherQueueIndexesAndCellsMap(List<Integer> cellsIndexesOnSheet, List<CellType<?>> cellTypes);

	protected abstract Integer getCellIndexOnSheet(Integer cellIndexInQueue);

	private ImmutableSortedMap<Integer, CELL> getQueueIndexesAndCellsMap() {
		if (this.queueIndexesAndCellsMap == null) {
			this.queueIndexesAndCellsMap = gatherQueueIndexesAndCellsMap(getCellsIndexesOnSheet(), getCellTypes());
		}
		return this.queueIndexesAndCellsMap;
	}
}
