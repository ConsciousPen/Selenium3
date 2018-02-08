package aaa.utils.excel.io.entity.area.sheet;

import java.util.LinkedHashMap;
import java.util.Map;
import aaa.utils.excel.io.entity.queue.EditableColumn;

public class SheetColumn extends EditableColumn<SheetCell> {
	private Map<Integer, SheetCell> cells;
	private ExcelSheet sheet;

	public SheetColumn(int columnIndex, ExcelSheet sheet) {
		super(columnIndex, sheet.getExcelManager());
		this.sheet = sheet;
	}

	public ExcelSheet getSheet() {
		return this.sheet;
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, SheetCell> getCellsMap() {
		if (this.cells == null) {
			this.cells = new LinkedHashMap<>(getSheet().getRowsMap().size());
			for (Map.Entry<Integer, SheetRow> rowEntry : getSheet().getRowsMap().entrySet()) {
				this.cells.put(rowEntry.getKey(), rowEntry.getValue().getCell(getIndex()));
			}
		}
		return this.cells;
	}

	/*@Override
	public EditableCellsArea<SheetCell, ?, ?> exclude() {
		//TODO-dchubkov: >>>>>>>>>>>>>>>>>>>>>
		return null;
	}
*/
	@Override
	protected ExcelSheet getArea() {
		return getSheet();
	}

	/*@Override
	public EditableCellsArea<SheetCell, ?, ?> delete() {
		//TODO-dchubkov: >>>>>>>>>>>>>>>>>>>>>
		return null;
	}*/



	/*@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<EditableCell> iterator() {
		return (Iterator<EditableCell>) new CellIterator(this);
	}*/
}
