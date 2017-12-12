package aaa.utils.excel;

import org.apache.poi.ss.usermodel.Cell;

public abstract class CellType<T> {
	protected ExcelParser excelParser;

	public CellType() {
	}

	public CellType(ExcelParser excelParser) {
		this.excelParser = excelParser;
	}

	abstract T getValueFor(Cell cell);

	abstract boolean isTypeOf(Cell cell);

	protected ExcelParser getParser(Cell cell) {
		return excelParser.switchSheet(cell.getSheet().getSheetName());
	}
}
