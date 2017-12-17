package aaa.utils.excel.io.entity;

import java.util.Arrays;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.io.celltype.CellType;

public class TableCell extends ExcelCell {
	private TableRow tableRow;

	public TableCell(Cell cell, TableRow tableRow, CellType<?>... allowableCellTypes) {
		super(cell, allowableCellTypes);
		this.tableRow = tableRow;
	}

	public TableRow getTableRow() {
		return tableRow;
	}

	public String getHeaderColumnName() {
		return getTableRow().getTable().getHeader().getColumnName(getColumnNumber());
	}

	@Override
	public int getRowNumber() {
		return getTableRow().getRowNumber();
	}

	@Override
	public String toString() {
		return "ExcelCell{" +
				"Sheet name=" + getPoiCell().getSheet().getSheetName() +
				", Row number=" + getRowNumber() +
				", Column number=" + getColumnNumber() +
				", Header column name=" + getHeaderColumnName() +
				", Cell Types=" + Arrays.toString(getCellTypes()) +
				", Cell value=" + getStringValue() +
				'}';
	}
}
