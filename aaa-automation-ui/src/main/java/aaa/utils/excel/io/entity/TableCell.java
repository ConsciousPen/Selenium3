package aaa.utils.excel.io.entity;

import org.apache.poi.ss.usermodel.Cell;

public class TableCell extends ExcelCell {

	public TableCell(Cell cell, TableRow tableRow, int columnNumber) {
		super(cell, tableRow, columnNumber);
	}

	@Override
	public TableRow getRow() {
		return (TableRow) super.getRow();
	}

	public String getHeaderColumnName() {
		return getRow().getTable().getHeader().getColumnName(getColumnNumber());
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getRow().getSheet().getSheetName() +
				", Row number=" + getRowNumber() +
				", Column number=" + getColumnNumber() +
				", Header column name=" + getHeaderColumnName() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}
}
