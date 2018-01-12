package aaa.utils.excel.io.entity.area.table;

import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.entity.cell.ExcelCell;

public class HeaderCell extends ExcelCell {
	private int tableColumnIndex;

	public HeaderCell(Cell cell, TableRow tableRow, int tableColumnIndex, int columnIndex) {
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
}
