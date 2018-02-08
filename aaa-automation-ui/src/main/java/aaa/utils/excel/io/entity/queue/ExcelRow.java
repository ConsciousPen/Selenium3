package aaa.utils.excel.io.entity.queue;

import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.ExcelManager;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public abstract class ExcelRow<C extends ExcelCell> extends CellsQueue<C> {
	protected Row row;

	protected ExcelRow(Row row, int rowIndex, ExcelManager excelManager) {
		super(rowIndex, excelManager);
		this.row = row;
	}

	public Row getPoiRow() {
		return this.row;
	}

	@Override
	public boolean isEmpty() {
		return getPoiRow() == null || getPoiRow().getLastCellNum() <= 0 || super.isEmpty();
	}

	/*@Override
	public ExcelRow<C> exclude() {
		getArea().excludeRows(this.getIndex());
		return this;
	}*/

	/*@Override
	public ExcelRow<C> copy(int destinationRowIndex) {
		for (C cell : this) {
			cell.copy(destinationRowIndex, cell.getColumnIndex());
		}
		return this;
	}*/

	/*@Override
	public EditableCellsArea<?, ?> delete() {
		getArea().deleteRows(getIndex());
		return getArea();
	}*/

	@Override
	public String toString() {
		return "ExcelRow{" +
				"rowIndex=" + getIndex() +
				", values=" + getValues() +
				'}';
	}
}
