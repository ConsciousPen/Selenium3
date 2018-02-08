package aaa.utils.excel.io.entity.area.sheet;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.queue.EditableRow;

public class SheetRow extends EditableRow<SheetCell> {
	private Map<Integer, SheetCell> cells;
	private ExcelSheet sheet;

	public SheetRow(Row row, int rowIndex, ExcelSheet sheet) {
		super(row, rowIndex, sheet.getExcelManager());
		this.sheet = sheet;
	}

	public ExcelSheet getSheet() {
		return this.sheet;
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, SheetCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getColumnsMap().size());
			for (Map.Entry<Integer, SheetColumn> columnEntry : getSheet().getColumnsMap().entrySet()) {
				Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnEntry.getKey() - 1) : null;
				SheetCell cell = new SheetCell(poiCell, this, columnEntry.getKey());
				this.cells.put(columnEntry.getKey(), cell);
			}
		}
		return this.cells;
	}

	/*@Override
	public ExcelSheet exclude() {
		getSheet().excludeRows(this.getIndex());
		return getSheet();
	}*/

	@Override
	protected EditableCellsArea<SheetCell, ?, ?> getArea() {
		return getSheet();
	}

	@Override
	public ExcelSheet delete() {
		getSheet().deleteRows(getIndex());
		return getSheet();
	}

	/*@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<EditableCell> iterator() {
		return (Iterator<EditableCell>) new CellIterator(this);
	}*/
}
