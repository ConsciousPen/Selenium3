package aaa.utils.excel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import toolkit.exceptions.IstfException;

public class TableHeader {
	private Map<String, Integer> headerColumns;
	private Row row;

	public TableHeader(Row row) {
		this(row, 0);
	}

	public TableHeader(Row row, int fromColumnNumber) {
		this(row, fromColumnNumber, row.getLastCellNum());
	}

	public TableHeader(Row row, int fromColumnNumber, int columnsNumber) {
		this.row = row;
		int headerSize = fromColumnNumber + columnsNumber;
		this.headerColumns = new HashMap<>(headerSize);

		ExcelParser excelParser = new ExcelParser(row.getSheet());
		for (int column = fromColumnNumber; column < fromColumnNumber + headerSize; column++) {
			String value = excelParser.getValue(row, column);
			this.headerColumns.put(value, column);
		}
	}

	public Sheet getSheet() {
		return row.getSheet();
	}

	public int getRowNum() {
		return row.getRowNum();
	}

	public Set<String> getHeaderNames() {
		return headerColumns.keySet();
	}

	public int getSize() {
		return headerColumns.size();
	}

	public int getColumnNumber(String columnName) {
		if (!headerColumns.containsKey(columnName)) {
			throw new IstfException(String.format("There is no column name \"%s\" in the table's header", columnName));
		}
		return headerColumns.get(columnName);
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getHeaderNames() +
				'}';
	}
}
