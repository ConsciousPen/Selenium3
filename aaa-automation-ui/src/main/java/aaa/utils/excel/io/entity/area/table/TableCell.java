package aaa.utils.excel.io.entity.area.table;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class TableCell extends ExcelCell {
	private int tableColumnIndex;

	public TableCell(Cell cell, TableRow tableRow, int tableColumnIndex, int columnIndex) {
		super(cell, tableRow, columnIndex);
		this.tableColumnIndex = tableColumnIndex;
	}

	public String getHeaderColumnName() {
		return getRow().getColumnName(getColumnIndex());
	}

	public ExcelTable getTable() {
		return getRow().getTable();
	}

	public int getColumnIndexOnSheet() {
		return this.columnIndex;
	}

	/*@SuppressWarnings("unchecked")
	@Override
	protected ExcelTable getArea() {
		return getTable();
	}*/

	/*@Override
	public TableRow getRow() {
		return (TableRow) super.getRow();
	}*/

	@Override
	public TableRow getRow() {
		return (TableRow) super.getRow();
	}

	@Override
	public int getColumnIndex() {
		return this.tableColumnIndex;
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getTable().getSheet().getSheetName() +
				", Row number=" + getRowIndex() +
				", Column number=" + getColumnIndex() +
				", Header column name=" + getHeaderColumnName() +
				", Cell value=" + getStringValue() +
				", Cell Types=" + getCellTypes() +
				'}';
	}

	/*@Override
	public EditableCell excludeColumn() {
		getTable().excludeColumns(getColumnIndex());
		return this;
	}*/

	@Override
	public ExcelCell delete() {
		//TODO-dchubkov: implement delete ExcelCell and TableCell
		throw new NotImplementedException("Cell deletion is not implemented yet");
	}

	public TableCell copy(int destinationRowIndex, String destinationHeaderColumnName) {
		return copy(destinationRowIndex, destinationHeaderColumnName, true, true, true);
	}

	public TableCell copy(int destinationRowIndex, String destinationHeaderColumnName, boolean copyCellStyle, boolean copyComment, boolean copyHyperlink) {
		return (TableCell) copy(getTable().getCell(destinationRowIndex, destinationHeaderColumnName), copyCellStyle, copyComment, copyHyperlink);
	}
}
