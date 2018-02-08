package aaa.utils.excel.io.entity.queue;

import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;

public abstract class EditableColumn<C extends EditableCell> extends CellsQueue<C> {
	protected EditableColumn(int columnIndex, ExcelManager excelManager) {
		super(columnIndex, excelManager);
	}

	public EditableColumn<C> exclude() {
		getArea().excludeColumns(getIndex());
		return this;
	}

	public EditableColumn<C> copy(int destinationColumnIndex) {
		for (C cell : this) {
			cell.copy(cell.getRowIndex(), destinationColumnIndex);
		}
		return this;
	}

	public EditableColumn<C> clear() {
		getCells().forEach(EditableCell::clear);
		return this;
	}

	/*@Override
	public A delete() {
		getArea().deleteColumns(getIndex());
		return getArea();
	}*/

	@Override
	public String toString() {
		return "ExcelColumn{" +
				"columnIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}

	protected abstract EditableCellsArea<C, ?, ?> getArea();
}
