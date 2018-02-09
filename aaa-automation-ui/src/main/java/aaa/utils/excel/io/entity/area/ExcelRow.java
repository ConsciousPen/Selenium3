package aaa.utils.excel.io.entity.area;

import org.apache.poi.ss.usermodel.Row;

public abstract class ExcelRow<CELL extends ExcelCell> extends CellsQueue<CELL> {
	protected Row row;

	protected ExcelRow(Row row, int rowIndexOnSheet, ExcelArea<CELL, ?, ?> excelArea) {
		super(rowIndexOnSheet, excelArea);
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
	public ExcelArea<CELL, ?, ?> exclude() {
		getArea().excludeRows(this.getIndex());
		return getArea();
	}

	@Override
	public ExcelRow<CELL> copy(int destinationRowIndex) {
		for (CELL cell : this) {
			cell.copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}

	@Override
	public ExcelArea<CELL, ?, ?> delete() {
		getArea().deleteRows(getIndex());
		return getArea();
	}

	@Override
	public String toString() {
		return "ExcelRow{" +
				"rowIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}
}
