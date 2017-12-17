package aaa.utils.excel.io.entity;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

class CellIterator implements Iterator<ExcelCell> {
	private ExcelRow row;
	private List<Integer> cellIndexes;
	private int currentIndex;

	@Nonnull
	CellIterator(ExcelRow row) {
		this.row = row;
		this.cellIndexes = row.getColumnNumbers();
		this.currentIndex = row.getFirstColumnNumber();
	}

	@Override
	public boolean hasNext() {
		return row.hasColumn(currentIndex);
	}

	@Override
	public ExcelCell next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		ExcelCell returnCell = row.getCell(currentIndex);
		cellIndexes.remove(currentIndex);
		currentIndex = cellIndexes.get(0);
		return returnCell;
	}
}
