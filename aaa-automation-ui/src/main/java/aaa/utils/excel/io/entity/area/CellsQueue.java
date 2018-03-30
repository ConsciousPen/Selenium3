package aaa.utils.excel.io.entity.area;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public abstract class CellsQueue<CELL extends ExcelCell> implements Writable, Iterable<CELL> {
	private int queueIndexInArea;
	private int queueIndexOnSheet;
	private Set<Integer> cellsIndexesOnSheet;
	private ExcelArea<CELL, ?, ?> excelArea;
	private Set<CellType<?>> cellTypes;
	private Map<Integer, CELL> queueIndexesAndCellsMap;

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, Set<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(queueIndexInArea, queueIndexOnSheet, cellsIndexesOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected CellsQueue(int queueIndexInArea, int queueIndexOnSheet, Set<Integer> cellsIndexesOnSheet, ExcelArea<CELL, ?, ?> excelArea, Set<CellType<?>> cellTypes) {
		this.queueIndexInArea = queueIndexInArea;
		this.queueIndexOnSheet = queueIndexOnSheet;
		this.cellsIndexesOnSheet = new HashSet<>(cellsIndexesOnSheet);
		this.excelArea = excelArea;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public String getSheetName() {
		return getArea().getSheetName();
	}

	public List<CELL> getCells() {
		return new ArrayList<>(getQueueIndexesAndCellsMap().values());
	}

	public List<Integer> getCellsIndexes() {
		return new ArrayList<>(getQueueIndexesAndCellsMap().keySet());
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
		return getCells().stream().map(ExcelCell::getValue).collect(Collectors.toList());
	}

	public List<String> getStringValues() {
		return getCells().stream().map(ExcelCell::getStringValue).collect(Collectors.toList());
	}

	public int getSum() {
		return getSum(getCellsIndexes().toArray(new Integer[getQueueIndexesAndCellsMap().size()]));
	}

	public int getMaxValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).max().orElse(0);
	}

	public int getMinValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).min().orElse(0);
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public int getIndex() {
		return this.queueIndexInArea;
	}

	protected int getIndexOnSheet() {
		return this.queueIndexOnSheet;
	}

	protected List<Integer> getCellsIndexesOnSheet() {
		return new ArrayList<>(this.cellsIndexesOnSheet);
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
		CELL cell = getCell(cellIndexInQueue);
		if (cell.isDate(formatters)) {
			return Objects.equals(cell.getDateValue(formatters), expectedValue);
		}
		return cell.getCellTypes().stream().anyMatch(cellType -> Objects.equals(cell.getValue(cellType), expectedValue));
	}

	public CellsQueue<CELL> registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
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
		for (Integer cellIndex : cellsIndexesInQueue) {
			this.cellsIndexesOnSheet.remove(getCellIndexOnSheet(cellIndex));
			getQueueIndexesAndCellsMap().remove(cellIndex);
		}
	}

	protected abstract Map<Integer, CELL> gatherQueueIndexesAndCellsMap(Set<Integer> cellsIndexesOnSheet, Set<CellType<?>> cellTypes);

	protected abstract Integer getCellIndexOnSheet(Integer cellIndexInQueue);

	private Map<Integer, CELL> getQueueIndexesAndCellsMap() {
		if (this.queueIndexesAndCellsMap == null) {
			this.queueIndexesAndCellsMap = gatherQueueIndexesAndCellsMap(this.cellsIndexesOnSheet, this.cellTypes);
		}
		return this.queueIndexesAndCellsMap;
	}
}
