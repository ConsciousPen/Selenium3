package aaa.utils.excel.io.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

class CellIterator implements Iterator<ExcelCell<?>> {
	List<Integer> cellIndexes;
	ExcelRow excelRow;
	private int currentIndex;
	private int endIndex;

	CellIterator(List<Integer> cellIndexes, ExcelRow excelRow) {
		this.cellIndexes = new ArrayList<>(cellIndexes);
		this.excelRow = excelRow;
		this.currentIndex = 0;
		this.endIndex = cellIndexes.size();
	}

	@Override
	public boolean hasNext() {
		return currentIndex < endIndex && getCell(currentIndex).getValue() != null;
	}

	@Override
	public ExcelCell<?> next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		ExcelCell<?> returnCell = getCell(currentIndex);
		currentIndex++;
		return returnCell;
	}

	private ExcelCell<?> getCell(int iteratorIndex) {
		return excelRow.getCell(cellIndexes.get(iteratorIndex));
	}
}
