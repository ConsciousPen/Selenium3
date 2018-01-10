package aaa.utils.excel.io.entity.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.CellsQueue;
import aaa.utils.excel.io.entity.ExcelCell;

public class CellIterator<C extends ExcelCell> implements Iterator<C> {
	private CellsQueue cellsQueue;
	private List<Integer> cellsIndexes;
	private Integer currentIndex;

	@Nonnull
	public CellIterator(CellsQueue cellsQueue) {
		this.cellsQueue = cellsQueue;
		this.cellsIndexes = cellsQueue.getCellsIndexes();
		this.currentIndex = cellsQueue.getFirstCellIndex();
	}

	@Override
	public boolean hasNext() {
		return cellsQueue.hasCell(currentIndex);
	}

	@Override
	@SuppressWarnings("unchecked")
	public C next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		C returnCell = (C) cellsQueue.getCell(currentIndex);
		cellsIndexes.remove(currentIndex);
		currentIndex = cellsIndexes.isEmpty() ? -1 : cellsIndexes.get(0);
		return returnCell;
	}
}
