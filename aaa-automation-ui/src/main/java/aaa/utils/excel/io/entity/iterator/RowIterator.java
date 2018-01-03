package aaa.utils.excel.io.entity.iterator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;
import aaa.utils.excel.io.entity.ExcelRow;

public class RowIterator<R extends ExcelRow> implements Iterator<R> {
	private List<Integer> rowsIndexes;
	private Integer currentIndex;
	private Function<Integer, R> getRowFunction;

	public RowIterator(List<Integer> rowsIndexes, Function<Integer, R> getRowFunction) {
		this.rowsIndexes = new ArrayList<>(rowsIndexes);
		this.currentIndex = rowsIndexes.isEmpty() ? -1 : rowsIndexes.get(0);
		this.getRowFunction = getRowFunction;
	}

	@Override
	public boolean hasNext() {
		return currentIndex > 0;
	}

	@Override
	public R next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next row");
		}
		R returnRow = getRowFunction.apply(currentIndex);
		rowsIndexes.remove(currentIndex);
		currentIndex = rowsIndexes.isEmpty() ? -1 : rowsIndexes.get(0);
		return returnRow;
	}
}
