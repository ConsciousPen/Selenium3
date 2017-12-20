package aaa.utils.excel.io.entity.iterator;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;
import aaa.utils.excel.io.entity.ExcelRow;

public class RowIterator<R extends ExcelRow> implements Iterator<R> {
	private int currentIndex;
	private int endIndex;
	private Function<Integer, R> getRowFunction;

	public RowIterator(int startIndex, int endIndex, Function<Integer, R> getRowFunction) {
		this.currentIndex = startIndex;
		this.endIndex = endIndex;
		this.getRowFunction = getRowFunction;
	}

	@Override
	public boolean hasNext() {
		return currentIndex <= endIndex;
	}

	@Override
	public R next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next row");
		}
		R returnRow = getRowFunction.apply(currentIndex);
		currentIndex++;
		return returnRow;
	}
}
