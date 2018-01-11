package aaa.utils.excel.io.entity;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import aaa.utils.excel.io.celltype.CellType;

public abstract class ImmutableCellsQueue {
	protected int index;
	protected CellsArea cellsArea;
	protected Set<CellType<?>> cellTypes;

	protected ImmutableCellsQueue(int index, CellsArea cellsArea) {
		this(index, cellsArea, cellsArea.getCellTypes());
	}

	protected ImmutableCellsQueue(int index, CellsArea cellsArea, Set<CellType<?>> cellTypes) {
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

	protected CellsArea getArea() {
		return cellsArea;
	}

	protected abstract <C extends ExcelCell> Map<Integer, C> getCellsMap();

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

	public boolean hasCell(int queueIndex) {
		return getCellsMap().containsKey(queueIndex);
	}

	public boolean hasValue(int queueIndex, Object expectedValue) {
		return Objects.equals(getCell(queueIndex).getValue(), expectedValue);
	}

	public ImmutableCellsQueue save() {
		getArea().save();
		return this;
	}

	public ImmutableCellsQueue save(File destinationFile) {
		getArea().save(destinationFile);
		return this;
	}

	public ImmutableCellsQueue close() {
		getArea().close();
		return this;
	}

	public ImmutableCellsQueue saveAndClose() {
		getArea().saveAndClose();
		return this;
	}

	public ImmutableCellsQueue saveAndClose(File destinationFile) {
		getArea().saveAndClose(destinationFile);
		return this;
	}
}
