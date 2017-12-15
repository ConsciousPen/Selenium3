package aaa.utils.excel.io.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class TableRow extends ExcelRow implements Iterable<TableCell> {
	protected List<TableCell> cells;
	private ExcelTable table;
	private int rowNumber;

	public TableRow(Row row, ExcelTable table, int rowNumber) {
		super(row, table.getCellTypes());
		this.table = table;
		this.rowNumber = rowNumber;
	}

	public ExcelTable getTable() {
		return table;
	}

	public Map<String, Object> getTableValues() {
		Map<String, Object> values = new HashMap<>(getSize());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getValue());
		}
		return values;
	}

	public Map<String, String> getTableStringValues() {
		Map<String, String> values = new HashMap<>(getSize());
		for (TableCell cell : this) {
			values.put(cell.getHeaderColumnName(), cell.getStringValue());
		}
		return values;
	}

	@Override
	public int getRowNumber() {
		return this.rowNumber;
	}

	@Override
	public List<TableCell> getCells() {
		if (cells == null) {
			cells = new ArrayList<>();
			for (Cell cell : getPoiRow()) {
				cells.add(new TableCell(cell, this, getCellTypes()));
			}
		}
		return Collections.unmodifiableList(this.cells);
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowNumber=" + rowNumber +
				", values=" + getTableValues().entrySet() +
				'}';
	}

	@Nonnull
	@Override
	public Iterator<TableCell> iterator() {
		return new CellIterator<>(getCellIndexes(), this);
	}

	public boolean hasColumnName(String headerColumnName) {
		return getTable().getHeader().getColumnNames().contains(headerColumnName);
	}

	public boolean hasCellIndex(int columnIndex) {
		return getTable().getHeader().getColumnIndexes().contains(columnIndex);
	}

	public Integer getCellIndex(String headerColumnName) {
		return getTable().getHeader().getColumnIndex(headerColumnName);
	}

	public TableCell getCell(String headerColumnName) {
		return getCells().stream().filter(c -> c.getHeaderColumnName().equals(headerColumnName)).findFirst().get();
	}

	public Object getValue(String headerColumnName) {
		return getCell(headerColumnName).getValue();
	}

	public String getStringValue(String headerColumnName) {
		return getCell(headerColumnName).getStringValue();
	}

	public boolean getBoolValue(String headerColumnName) {
		return getCell(headerColumnName).getBoolValue();
	}

	public int getIntValue(String headerColumnName) {
		return getCell(headerColumnName).getIntValue();
	}

	public LocalDateTime getDateValue(String headerColumnName) {
		return getCell(headerColumnName).getDateValue();
	}

	public boolean hasCell(String headerColumnName) {
		int columnNumber = getTable().getHeader().getColumnIndex(headerColumnName);
		return hasCell(columnNumber);
	}

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return getCell(headerColumnName).hasValue(expectedValue);
	}
}
