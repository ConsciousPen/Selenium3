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
	private int indexOnSheet;
	//protected ExcelManager excelManager;
	private Set<CellType<?>> cellTypes;
	private ExcelArea<CELL, ?, ?> excelArea;

	protected CellsQueue(int indexOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		this(indexOnSheet, excelArea, excelArea.getCellTypes());
	}

	protected CellsQueue(int indexOnSheet, ExcelArea<CELL, ?, ?> excelArea, Set<CellType<?>> cellTypes) {
		this.indexOnSheet = indexOnSheet;
		this.excelArea = excelArea;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public List<CELL> getCells() {
		return new ArrayList<>(getCellsMap().values());
	}

	public List<Integer> getCellsIndexes() {
		return new ArrayList<>(getCellsMap().keySet());
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

	public int getSize() {
		return getCellsMap().size();
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
		return getSum(getCellsIndexes().toArray(new Integer[getCellsMap().size()]));
	}

	public int getMaxValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).max().getAsInt();
	}

	public int getMinValue() {
		return getCells().stream().filter(c -> !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).min().getAsInt();
	}

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	protected int getIndexOnSheet() {
		return this.indexOnSheet;
	}

	protected ExcelArea<CELL, ?, ?> getArea() {
		return this.excelArea;
	}

	protected abstract Map<Integer, CELL> getCellsMap();

	@Override
	@Nonnull
	public Iterator<CELL> iterator() {
		return new CellIterator<>(this);
	}

	@Override
	public ExcelManager getExcelManager() {
		return getArea().getExcelManager();
	}

	public int getSum(Integer... cellsIndexes) {
		List<Integer> cellsIndexesList = Arrays.asList(cellsIndexes);
		return getCells().stream().filter(c -> cellsIndexesList.contains(c.getColumnIndex()) && !c.isEmpty() && c.hasType(ExcelCell.INTEGER_TYPE)).mapToInt(ExcelCell::getIntValue).sum();
	}

	public boolean isEmpty(int queueIndex) {
		return getCell(queueIndex).isEmpty();
	}

	public CELL getCell(int queueIndex) {
		assertThat(hasCell(queueIndex)).as("There is no cell with %s index", queueIndex, getIndex()).isTrue();
		return getCellsMap().get(queueIndex);
	}

	public Object getValue(int queueIndex) {
		return getCell(queueIndex).getValue();
	}

	public String getStringValue(int queueIndex) {
		return getCell(queueIndex).getStringValue();
	}

	public Boolean getBoolValue(int queueIndex) {
		return getCell(queueIndex).getBoolValue();
	}

	public Integer getIntValue(int queueIndex) {
		return getCell(queueIndex).getIntValue();
	}

	public Double getDoubleValue(int queueIndex) {
		return getCell(queueIndex).getDoubleValue();
	}

	public LocalDateTime getDateValue(int queueIndex, DateTimeFormatter... formatters) {
		return getCell(queueIndex).getDateValue(formatters);
	}

	public boolean hasCell(int queueIndex) {
		return getCellsMap().containsKey(queueIndex);
	}

	public boolean hasValue(int queueIndex, Object expectedValue, DateTimeFormatter... formatters) {
		CELL cell = getCell(queueIndex);
		if (cell.isDate(formatters)) {
			return Objects.equals(cell.getDateValue(formatters), expectedValue);
		}
		return Objects.equals(cell.getValue(), expectedValue);
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
}
