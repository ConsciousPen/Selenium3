package aaa.utils.excel.io.entity.area.sheet;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class SheetRow extends ExcelRow<SheetCell> {
	private Map<Integer, SheetCell> cells;

	public SheetRow(Row row, int rowIndex, ExcelSheet sheet) {
		super(row, rowIndex, sheet);
	}

	public ExcelSheet getSheet() {
		return (ExcelSheet) getArea();
	}

	@Override
	//@SuppressWarnings({"AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, SheetCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getColumnsIndexes().size());
			for (Integer columnIndex : getSheet().getColumnsIndexes()) {
				Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndex - 1) : null;
				SheetCell cell = new SheetCell(poiCell, this, columnIndex);
				this.cells.put(columnIndex, cell);
			}
		}
		return this.cells;
	}

	/*@Override
	public ExcelSheet exclude() {
		getSheet().excludeRows(this.getIndex());
		return getSheet();
	}*/

	/*@Override
	protected ExcelSheet getArea() {
		return getSheet();
	}*/

	/*@Override
	public ExcelSheet delete() {
		getSheet().deleteRows(getIndex());
		return getSheet();
	}*/

	/*@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<EditableCell> iterator() {
		return (Iterator<EditableCell>) new CellIterator(this);
	}*/
}
