package aaa.utils.excel.io.entity.queue;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.Writable;
import aaa.utils.excel.io.entity.area.CellsArea;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class CellsQueue implements Writable {
	protected int index;
	protected CellsArea cellsArea;
	protected Set<CellType<?>> cellTypes;

	protected CellsQueue(int index, EditableCellsArea cellsArea) {
		this(index, cellsArea, cellsArea.getCellTypes());
	}

	protected CellsQueue(int index, EditableCellsArea cellsArea, Set<CellType<?>> cellTypes) {
		this.index = index;
		this.cellsArea = cellsArea;
		this.cellTypes = new HashSet<>(cellTypes);
	}

	public int getIndex() {
		return index;
	}

	public List<? extends ExcelCell> getCells() {
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

	public ExcelCell getFirstCell() {
		return getCell(getFirstCellIndex());
	}

	public ExcelCell getLastCell() {
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

	public Set<CellType<?>> getCellTypes() {
		return new HashSet<>(this.cellTypes);
	}

	public CellsArea getArea() {
		return cellsArea;
	}

	protected abstract <C extends ExcelCell> Map<Integer, C> getCellsMap();

	@Override
	public ExcelManager getExcelManager() {
		return getArea().getExcelManager();
	}

	public ExcelCell getCell(int queueIndex) {
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

	public LocalDateTime getDateValue(int queueIndex) {
		return getCell(queueIndex).getDateValue();
	}

	public boolean hasCell(int queueIndex) {
		return getCellsMap().containsKey(queueIndex);
	}

	public boolean hasValue(int queueIndex, Object expectedValue) {
		return Objects.equals(getCell(queueIndex).getValue(), expectedValue);
	}
}
