package aaa.utils.excel.io.entity;

public abstract class ExcelColumn extends CellsQueue {

	protected ExcelColumn(int columnIndex, CellsArea cellsArea) {
		super(columnIndex, cellsArea);
	}

	@Override
	public ExcelColumn exclude() {
		getArea().excludeColumns(getIndex());
		return this;
	}

	@Override
	public ExcelColumn copy(int destinationColumnIndex) {
		for (ExcelCell cell : getCells()) {
			cell.copy(cell.getRowIndex(), destinationColumnIndex);
		}
		return this;
	}

	@Override
	public CellsArea delete() {
		getArea().deleteColumns(getIndex());
		return getArea();
	}
}
