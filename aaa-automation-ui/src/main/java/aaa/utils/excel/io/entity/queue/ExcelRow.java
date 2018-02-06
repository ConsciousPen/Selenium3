package aaa.utils.excel.io.entity.queue;

import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.area.EditableCellsArea;
import aaa.utils.excel.io.entity.cell.EditableCell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class ExcelRow extends EditableCellsQueue {
	protected Row row;

	protected ExcelRow(Row row, int rowIndex, EditableCellsArea cellsArea) {
		super(rowIndex, cellsArea);
		this.row = row;
	}

	public Row getPoiRow() {
		return this.row;
	}

	@Override
	public boolean isEmpty() {
		return getPoiRow() == null || getPoiRow().getLastCellNum() <= 0 || super.isEmpty();
	}

	@Override
	public ExcelRow exclude() {
		((EditableCellsArea) getArea()).excludeRows(this.getIndex());
		return this;
	}

	@Override
	public ExcelRow copy(int destinationRowIndex) {
		for (ExcelCell cell : getCells()) {
			((EditableCell) cell).copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}

	@Override
	public EditableCellsArea delete() {
		((EditableCellsArea) getArea()).deleteRows(getIndex());
		return (EditableCellsArea) getArea();
	}

	@Override
	public String toString() {
		return "ExcelRow{" +
				"rowIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}
}
