package aaa.utils.excel;

import org.apache.poi.ss.usermodel.Row;

public class TableRow {
	private TableHeader header;
	private Row row;
	private int rowNumber;

	public TableRow(TableHeader header, int rowNumber) {
		int rowNumberInSheet = header.getRowNum() + rowNumber;
		this.row = header.getSheet().getRow(rowNumberInSheet);
		this.header = header;
		this.rowNumber = rowNumber;
	}

	public TableHeader getHeader() {
		return header;
	}

	public Row getRow() {
		return this.row;
	}

	public int getRowNumber() {
		return this.rowNumber;
	}

	public String getValue(String headerColumnName) {
		ExcelParser excelParser = new ExcelParser(header.getSheet());
		return excelParser.getValue(getRow(), getHeader().getColumnNumber(headerColumnName));
	}
}
