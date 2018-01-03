package aaa.utils.excel.io.entity;

import org.apache.poi.ss.usermodel.Cell;

public class TableCell extends ExcelCell {

	public TableCell(Cell cell, TableRow tableRow, int columnNumber) {
		super(cell, tableRow, columnNumber);
	}

	public String getHeaderColumnName() {
		return ((TableRow) getRow()).getTable().getHeader().getColumnName(getColumnIndex());
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getRow().getSheet().getSheetName() +
				", Row number=" + getRowIndex() +
				", Column number=" + getColumnIndex() +
				", Header column name=" + getHeaderColumnName() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}
}
