package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;

public class TableRow extends ExcelRow {
	private int tableRowIndex;
	private ExcelTable table;
	private Map<Integer, TableCell> tableCells;

	public TableRow(Row row, int tableRowIndex, Set<Integer> sheetColumnsIndexes, ExcelTable table) {
		super(row, tableRowIndex, sheetColumnsIndexes, table.getSheet(), table.getCellTypes());
		this.table = table;
	}

	public ExcelTable getTable() {
		return table;
	}

	public Map<String, Object> getTableValues() {
		Map<String, Object> values = new HashMap<>(getSize());
		for (TableCell cell : getCells()) {
			values.put(cell.getHeaderColumnName(), cell.getValue());
		}
		return values;
	}

	public Map<String, String> getTableStringValues() {
		Map<String, String> values = new HashMap<>(getSize());
		for (TableCell cell : getCells()) {
			values.put(cell.getHeaderColumnName(), cell.getStringValue());
		}
		return values;
	}

	@Override
	public List<Integer> getColumnsIndexes() {
		return getTable().getHeader().getColumnsIndexes();
	}

	public List<String> getColumnNames() {
		return getTable().getHeader().getColumnsNames();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new HashMap<>();
			for (int sheetColumnIndex : this.columnsIndexes) {
				TableCell tableCell = new TableCell(getPoiRow().getCell(sheetColumnIndex - 1), this, sheetColumnIndex);
				this.tableCells.put(tableCell.getColumnIndex(), tableCell);
			}
		}
		return new HashMap<>(this.tableCells);
	}

	int getRowIndexOnSheet() {
		return getTable().getHeader().getRowIndexOnSheet() + getRowIndex();
	}

	@Override
	public List<TableCell> getCells() {
		return new ArrayList<>(getCellsMap().values());
	}

	@Override
	public void delete() {
		getTable().deleteRows(this);
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowIndex=" + getRowIndex() +
				", values=" + getTableValues().entrySet() +
				'}';
	}

	public boolean hasColumnName(String headerColumnName) {
		return getTable().getHeader().hasColumnName(headerColumnName);
	}

	public Integer getColumnIndex(String headerColumnName) {
		return getTable().getHeader().getColumnIndex(headerColumnName);
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumnName(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCells().stream().filter(c -> c.getHeaderColumnName().equals(headerColumnName)).findFirst().get();
	}

	public List<TableCell> getCellsContains(String headerColumnNamePattern) {
		return getCells().stream().filter(c -> c.getHeaderColumnName().contains(headerColumnNamePattern)).collect(Collectors.toList());
	}

	public Object getValue(String headerColumnName) {
		return getCell(headerColumnName).getValue();
	}

	public String getStringValue(String headerColumnName) {
		return getCell(headerColumnName).getStringValue();
	}

	public Boolean getBoolValue(String headerColumnName) {
		return getCell(headerColumnName).getBoolValue();
	}

	public Integer getIntValue(String headerColumnName) {
		return getCell(headerColumnName).getIntValue();
	}

	public LocalDateTime getDateValue(String headerColumnName) {
		return getCell(headerColumnName).getDateValue();
	}

	public boolean hasCell(String headerColumnName) {
		return getTable().getHeader().hasColumnName(headerColumnName);
	}

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return getCell(headerColumnName).hasValue(expectedValue);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected <R extends ExcelRow, C extends ExcelCell> R setCellsMap(Map<Integer, C> tableCells) {
		this.tableCells = new HashMap<>((Map<Integer, TableCell>) tableCells);
		return (R) this;
	}

	void excludeColumn(int columnIndex) {
		getCellsMap().remove(columnIndex);
	}
}
