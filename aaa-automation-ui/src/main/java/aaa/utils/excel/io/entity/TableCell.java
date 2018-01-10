package aaa.utils.excel.io.entity;

import org.apache.poi.ss.usermodel.Cell;

public class TableCell extends ExcelCell {
	private int tableColumnIndex;

	public TableCell(Cell cell, TableRow tableRow, int tableColumnIndex, int columnIndex) {
		super(cell, tableRow, columnIndex);
		this.tableColumnIndex = tableColumnIndex;
	}

	public String getHeaderColumnName() {
		return ((TableRow) getRow()).getTable().getHeader().getColumnName(getColumnIndex());
	}

	@Override
	public int getColumnIndex() {
		return this.tableColumnIndex;
	}

	public int getColumnIndexOnSheet() {
		return this.columnIndex;
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + ((TableRow) getRow()).getTable().getSheet().getSheetName() +
				", Row number=" + getRowIndex() +
				", Column number=" + getColumnIndex() +
				", Header column name=" + getHeaderColumnName() +
				", Cell Types=" + getCellTypes() +
				", Cell value=" + getStringValue() +
				'}';
	}
}
