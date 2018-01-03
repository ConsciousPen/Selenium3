package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.Row;

public class TableRow extends ExcelRow {
	private ExcelTable table;
	private Map<Integer, TableCell> tableCells;

	public TableRow(Row row, int rowIndex, ExcelTable table) {
		super(row, rowIndex, table.getSheet(), table.getCellTypes());
		this.table = table;
	}

	public ExcelTable getTable() {
		return table;
	}

	TableRow setTable(ExcelTable table) {
		this.table = table;
		return this;
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

	public List<String> getColumnNames() {
		return getTable().getHeader().getColumnsNames();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new HashMap<>();
			for (int columnIndex : getColumnsIndexes()) {
				this.tableCells.put(columnIndex, new TableCell(getPoiRow().getCell(columnIndex - 1), this, columnIndex));
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
	public void erase() {
		getTable().eraseRow(this);
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

	@Override
	public List<Integer> getColumnsIndexes() {
		return getTable().getHeader().getColumnsIndexes();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <R extends ExcelRow> R copy(R destinationRow, boolean copyRowIndex) {
		super.copy(destinationRow, copyRowIndex);
		((TableRow) destinationRow)
				.setTable(this.getTable())
				.setCellsMap(this.getCellsMap());
		return (R) this;
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
