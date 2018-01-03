package aaa.utils.excel.io.entity.iterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.ExcelCell;
import aaa.utils.excel.io.entity.ExcelRow;

public class CellIterator<C extends ExcelCell> implements Iterator<C> {
	private ExcelRow row;
	private List<Integer> cellsIndexes;
	private Integer currentIndex;

	@Nonnull
	public CellIterator(ExcelRow row) {
		this.row = row;
		this.cellsIndexes = row.getColumnsIndexes();
		this.currentIndex = row.getFirstColumnIndex();
	}

	@Override
	public boolean hasNext() {
		return row.hasColumn(currentIndex);
	}

	@Override
	public C next() {
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next cell");
		}
		C returnCell = row.getCell(currentIndex);
		cellsIndexes.remove(currentIndex);
		currentIndex = cellsIndexes.isEmpty() ? -1 : cellsIndexes.get(0);
		return returnCell;
	}
}
