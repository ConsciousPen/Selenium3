package aaa.utils.excel.io.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class SheetColumn extends ExcelColumn implements Iterable<ExcelCell> {
	private Map<Integer, ExcelCell> cells;

	public SheetColumn(int columnIndex, ExcelSheet sheet) {
		super(columnIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, ExcelCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new HashMap<>(getSheet().getRowsMap().size());
			for (Map.Entry<Integer, SheetRow> rowEntry : getSheet().getRowsMap().entrySet()) {
				this.cells.put(rowEntry.getKey(), rowEntry.getValue().getCell(getIndex()));
			}
		}
		return this.cells;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<ExcelCell> iterator() {
		return (Iterator<ExcelCell>) new CellIterator(this);
	}
}
