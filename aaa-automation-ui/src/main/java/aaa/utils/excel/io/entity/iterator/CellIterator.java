package aaa.utils.excel.io.entity.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.cell.ExcelCell;
import aaa.utils.excel.io.entity.queue.CellsQueue;

public class CellIterator<C extends ExcelCell> implements Iterator<C> {
	private CellsQueue<C> cellsQueue;
	private List<Integer> cellsIndexes;
	private Integer currentIndex;

	@Nonnull
	public CellIterator(CellsQueue<C> cellsQueue) {
		this.cellsQueue = cellsQueue;
		this.cellsIndexes = cellsQueue.getCellsIndexes();
		this.currentIndex = cellsQueue.getFirstCellIndex();
	}

	@Override
	public boolean hasNext() {
		return cellsQueue.hasCell(currentIndex);
	}

	@Override
	public C next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		C returnCell = cellsQueue.getCell(currentIndex);
		cellsIndexes.remove(currentIndex);
		currentIndex = cellsIndexes.isEmpty() ? -1 : cellsIndexes.get(0);
		return returnCell;
	}
}
