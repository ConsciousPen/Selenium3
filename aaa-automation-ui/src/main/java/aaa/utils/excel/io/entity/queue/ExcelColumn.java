package aaa.utils.excel.io.entity.queue;

import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class ExcelColumn extends EditableCellsQueue {

	protected ExcelColumn(int columnIndex, EditableCellsArea cellsArea) {
		super(columnIndex, cellsArea);
	}

	@Override
	public ExcelColumn exclude() {
		((EditableCellsArea) getArea()).excludeColumns(getIndex());
		return this;
	}

	@Override
	public ExcelColumn copy(int destinationColumnIndex) {
		for (ExcelCell cell : getCells()) {
			((EditableCell) cell).copy(cell.getRowIndex(), destinationColumnIndex);
		}
		return this;
	}

	@Override
	public EditableCellsArea delete() {
		((EditableCellsArea) getArea()).deleteColumns(getIndex());
		return (EditableCellsArea) getArea();
	}

	@Override
	public String toString() {
		return "ExcelColumn{" +
				"columnIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}
}
