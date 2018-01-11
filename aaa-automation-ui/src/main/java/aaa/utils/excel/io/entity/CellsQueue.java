package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import aaa.utils.excel.io.celltype.CellType;

public abstract class CellsQueue extends ImmutableCellsQueue {

	protected CellsQueue(int index, CellsArea cellsArea) {
		super(index, cellsArea);
	}

	public CellsQueue registerCellType(CellType<?>... cellTypes) {
		this.cellTypes.addAll(Arrays.asList(cellTypes));
		getCells().forEach(c -> c.registerCellType(cellTypes));
		return this;
	}

	public abstract CellsQueue exclude();

	public CellsQueue clear() {
		getCells().forEach(ExcelCell::clear);
		return this;
	}

	public abstract CellsQueue copy(int destinationQueueIndex);

	public abstract CellsArea delete();

	public Boolean getBoolValue(int queueIndex) {
		return getCell(queueIndex).getBoolValue();
	}

	public Integer getIntValue(int queueIndex) {
		return getCell(queueIndex).getIntValue();
	}

	public LocalDateTime getDateValue(int queueIndex) {
		return getCell(queueIndex).getDateValue();
	}
}
