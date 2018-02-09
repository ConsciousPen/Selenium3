package aaa.utils.excel.io.entity.area;

public abstract class ExcelColumn<CELL extends ExcelCell> extends CellsQueue<CELL> {
	protected ExcelColumn(int columnIndex, ExcelArea<CELL, ?, ?> excelArea) {
		super(columnIndex, excelArea);
	}

	@Override
	public String toString() {
		return "ExcelColumn{" +
				"columnIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}

	@Override
	public ExcelArea<CELL, ?, ?> exclude() {
		getArea().excludeColumns(getIndex());
		return getArea();
	}

	@Override
	public ExcelArea<CELL, ?, ?> delete() {
		getArea().deleteColumns(getIndex());
		return getArea();
	}

	@Override
	public ExcelColumn<CELL> copy(int destinationColumnIndex) {
		for (CELL cell : this) {
			cell.copy(cell.getRowIndex(), destinationColumnIndex);
		}
		return this;
	}
}
