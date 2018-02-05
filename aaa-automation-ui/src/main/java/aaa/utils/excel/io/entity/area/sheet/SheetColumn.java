package aaa.utils.excel.io.entity.area.sheet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.iterator.CellIterator;
import aaa.utils.excel.io.entity.queue.ExcelColumn;

public class SheetColumn extends ExcelColumn implements Iterable<EditableCell> {
	private Map<Integer, EditableCell> cells;

	public SheetColumn(int columnIndex, ExcelSheet sheet) {
		super(columnIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, EditableCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getRowsMap().size());
			for (Map.Entry<Integer, SheetRow> rowEntry : getSheet().getRowsMap().entrySet()) {
				this.cells.put(rowEntry.getKey(), (EditableCell) rowEntry.getValue().getCell(getIndex()));
			}
		}
		return this.cells;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<EditableCell> iterator() {
		return (Iterator<EditableCell>) new CellIterator(this);
	}
}
