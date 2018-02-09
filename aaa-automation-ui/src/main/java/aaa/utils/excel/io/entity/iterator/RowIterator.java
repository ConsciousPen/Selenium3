package aaa.utils.excel.io.entity.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import aaa.utils.excel.io.entity.area.CellsQueue;

public class RowIterator<ROW extends CellsQueue<?>> implements Iterator<ROW> {
	private List<Integer> rowsIndexes;
	private Integer currentIndex;
	private Function<Integer, ROW> getRowFunction;

	public RowIterator(List<Integer> rowsIndexes, Function<Integer, ROW> getRowFunction) {
		this.rowsIndexes = new ArrayList<>(rowsIndexes);
		this.currentIndex = rowsIndexes.isEmpty() ? -1 : rowsIndexes.get(0);
		this.getRowFunction = getRowFunction;
	}

	@Override
	public boolean hasNext() {
		return currentIndex > 0;
	}

	@Override
	public ROW next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next row");
		}
		ROW returnRow = getRowFunction.apply(currentIndex);
		rowsIndexes.remove(currentIndex);
		currentIndex = rowsIndexes.isEmpty() ? -1 : rowsIndexes.get(0);
		return returnRow;
	}
}
