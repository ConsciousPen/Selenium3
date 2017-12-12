package aaa.utils.excel.table;

import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.CellType;
import aaa.utils.excel.ExcelParser;

public class TableCell {
	private int cellNumber;
	private TableRow tableRow;
	private Cell cell;
	private String columnName;
	private TableHeader header;
	private ExcelTable table;
	private ExcelParser excelParser;
	private CellType<?> cellType;

	public TableCell(TableRow tableRow, int cellNumber) {
		this.tableRow = tableRow;
		this.cellNumber = cellNumber;
		this.cell = tableRow.getRow().getCell(cellNumber + 1);
		this.header = tableRow.getHeader();
		this.table = tableRow.getTable();
		this.excelParser = new ExcelParser(table.getSheet());
	}

	public int getCellNumber() {
		return cellNumber;
	}

	public TableRow getTableRow() {
		return tableRow;
	}

	public TableHeader getHeader() {
		return header;
	}

	public ExcelTable getTable() {
		return table;
	}

	public String getColumnName() {
		if (columnName == null) {
			columnName = getHeader().getColumnName(cellNumber);
		}
		return columnName;
	}

	public CellType<?> getCellType() {
		if (cellType == null) {
			cellType = excelParser.getCellType(getCell());
		}
		return cellType;
	}

	public Object getValue() {
		return excelParser.getValue(getCell());
	}

	public String getStringValue() {
		return excelParser.getStringValue(cell);
	}

	Cell getCell() {
		return cell;
	}
}
