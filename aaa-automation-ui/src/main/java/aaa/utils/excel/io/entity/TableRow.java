package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.entity.iterator.CellIterator;

public class TableRow extends ExcelRow implements Iterable<TableCell> {
	protected Map<Integer, TableCell> tableCells;
	private int tableRowIndex;

	public TableRow(Row row, int tableRowIndex, int rowIndex, ExcelTable table) {
		super(row, rowIndex, table);
		this.tableRowIndex = tableRowIndex;
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
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

	public List<String> getColumnsNames() {
		return new ArrayList<>(getCellsMap().values().stream().map(TableCell::getStringValue).collect(Collectors.toSet()));
	}

	@Override
	@SuppressWarnings({"unchecked", "AssignmentOrReturnOfFieldWithMutableType"})
	protected Map<Integer, TableCell> getCellsMap() {
		if (this.tableCells == null) {
			this.tableCells = new HashMap<>(getTable().getColumnsMap().size());
			for (Map.Entry<Integer, TableColumn> columnEntry : getTable().getColumnsMap().entrySet()) {
				this.tableCells.put(columnEntry.getKey(), (TableCell) columnEntry.getValue().getCell(getIndex()));
			}
		}
		return this.tableCells;
	}

	int getIndexOnSheet() {
		return this.index;
	}

	@Override
	public int getIndex() {
		return this.tableRowIndex;
	}

	@Override
	@Nonnull
	@SuppressWarnings("unchecked")
	public Iterator<TableCell> iterator() {
		return (Iterator<TableCell>) new CellIterator(this);
	}

	@Override
	public String toString() {
		return "TableRow{" +
				"rowIndex=" + getIndex() +
				", values=" + getTableValues().entrySet() +
				'}';
	}

	public boolean hasCell(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	public int getCellIndex(String headerColumnName) {
		assertThat(hasCell(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCellsMap().values().stream().filter(c -> c.getStringValue().equals(headerColumnName)).findFirst().get().getColumnIndex();
	}

	public int getCellIndexOnSheet(String headerColumnName) {
		assertThat(hasCell(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return getCellsMap().values().stream().filter(c -> c.getStringValue().equals(headerColumnName)).findFirst().get().getColumnIndexOnSheet();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getCellsMap().get(columnIndex).getStringValue();
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasCell(headerColumnName)).as("There is no column name \"%s\" in the table's header", headerColumnName).isTrue();
		return (TableCell) getCells().stream().filter(c -> ((TableCell) c).getHeaderColumnName().equals(headerColumnName)).findFirst().get();
	}

	@SuppressWarnings("unchecked")
	public List<TableCell> getCellsContains(String headerColumnNamePattern) {
		return (List<TableCell>) getCells().stream().filter(c -> ((TableCell) c).getHeaderColumnName().contains(headerColumnNamePattern)).collect(Collectors.toList());
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

	public boolean hasValue(String headerColumnName, Object expectedValue) {
		return getCell(headerColumnName).hasValue(expectedValue);
	}
}
