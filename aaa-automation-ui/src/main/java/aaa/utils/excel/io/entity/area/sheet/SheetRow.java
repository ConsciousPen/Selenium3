package aaa.utils.excel.io.entity.area.sheet;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.iterator.CellIterator;
import aaa.utils.excel.io.entity.queue.ExcelRow;

public class SheetRow extends ExcelRow implements Iterable<EditableCell> {
	private Map<Integer, EditableCell> cells;

	public SheetRow(Row row, int rowIndex, EditableCellsArea sheet) {
		super(row, rowIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, EditableCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getColumnsMap().size());
			for (Map.Entry<Integer, SheetColumn> columnEntry : getSheet().getColumnsMap().entrySet()) {
				Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnEntry.getKey() - 1) : null;
				EditableCell cell = new EditableCell(poiCell, this, columnEntry.getKey());
				this.cells.put(columnEntry.getKey(), cell);
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
