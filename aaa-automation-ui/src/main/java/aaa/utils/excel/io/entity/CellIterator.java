package aaa.utils.excel.io.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

class CellIterator<C extends ExcelCell, R extends ExcelRow> implements Iterator<C> {
	List<Integer> cellIndexes;
	R excelRow;
	private int currentIndex;
	private int endIndex;

	@Nonnull
	CellIterator(List<Integer> cellIndexes, R excelRow) {
		this.cellIndexes = new ArrayList<>(cellIndexes);
		this.excelRow = excelRow;
		this.currentIndex = 0;
		this.endIndex = cellIndexes.size();
	}

	@Override
	public boolean hasNext() {
		return currentIndex < endIndex;
	}

	@Override
	public C next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		C returnCell = getCell(currentIndex);
		currentIndex++;
		return returnCell;
	}

	@SuppressWarnings("unchecked")
	private C getCell(int iteratorIndex) {
		return (C) excelRow.getCell(cellIndexes.get(iteratorIndex));
	}
}
