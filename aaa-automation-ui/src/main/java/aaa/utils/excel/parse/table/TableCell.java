package aaa.utils.excel.parse.table;

import java.time.LocalDateTime;
import org.apache.poi.ss.usermodel.Cell;
import aaa.utils.excel.parse.celltype.BaseCellType;
import aaa.utils.excel.ExcelParser;
import aaa.utils.excel.parse.celltype.CellType;

public class TableCell<T> {
	private int cellNumber;
	private TableRow tableRow;
	private Cell cell;
	private String columnName;
	private TableHeader header;
	private ExcelTable table;
	private ExcelParser excelParser;
	private BaseCellType<T> cellType;

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

	public BaseCellType<T> getCellType() {
		if (cellType == null) {
			cellType = excelParser.getCellType(getCell());
		}
		return cellType;
	}

	public T getValue() {
		return excelParser.getValue(getCell(), getCellType());
	}

	public Boolean getBoolValue() {
		return excelParser.getValue(getCell(), CellType.BOOLEAN.get());
	}

	public String getStringValue() {
		return excelParser.getValue(getCell(), CellType.STRING.get());
	}

	public int getIntValue() {
		return excelParser.getValue(getCell(), CellType.INTEGER.get());
	}

	public LocalDateTime getDateValue() {
		return excelParser.getValue(getCell(), CellType.LOCAL_DATE_TIME.get());
	}

	Cell getCell() {
		return cell;
	}
}
