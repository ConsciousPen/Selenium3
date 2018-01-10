package aaa.utils.excel.io.entity;

import org.apache.poi.ss.usermodel.Row;

public abstract class ExcelRow extends CellsQueue {
	protected Row row;

	public ExcelRow(Row row, int rowIndex, CellsArea cellsArea) {
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
		getArea().excludeRows(this.getIndex());
		return this;
	}

	@Override
	public ExcelRow copy(int destinationRowIndex) {
		for (ExcelCell cell : getCells()) {
			cell.copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}

	@Override
	public CellsArea delete() {
		getArea().deleteRows(getIndex());
		return getArea();
	}
}
