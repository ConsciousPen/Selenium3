package aaa.utils.excel.io.entity;

import static toolkit.verification.CustomAssertions.assertThat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import toolkit.exceptions.IstfException;

public class TableHeader extends ExcelRow {
	private ExcelTable table;
	private Map<String, Pair<Integer, Integer>> columnNamesAndIndexes;

	public TableHeader(Row row, Set<Integer> columnsIndexes, ExcelTable table) {
		super(row, 0, columnsIndexes, table.getSheet(), table.getCellTypes());
		this.table = table;
		this.cellTypes.removeIf(t -> !t.equals(ExcelCell.STRING_TYPE));
		assertThat(this.cellTypes).as("Table header row should have type " + ExcelCell.STRING_TYPE).isNotEmpty();
	}

	public List<String> getColumnsNames() {
		return new ArrayList<>(getColumnNamesAndIndexesMap().keySet());
	}

	public ExcelTable getTable() {
		return table;
	}

	int getRowIndexOnSheet() {
		return getPoiRow().getRowNum() + 1;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	@Override
	public Boolean getBoolValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have boolean values");
	}

	@Override
	public Integer getIntValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have int values");
	}

	@Override
	public LocalDateTime getDateValue(int columnIndex) {
		throw new UnsupportedOperationException("Table header cells don't have LocalDateTime values");
	}

	@Override
	public <R extends ExcelRow> R registerCellType(CellType<?>... cellTypes) {
		throw new UnsupportedOperationException("Table header cell types should not be updated");
	}

	@Override
	public <R extends ExcelRow> R clear() {
		throw new UnsupportedOperationException("Table header erasing is not supported");
	}

	@Override
	public void delete() {
		throw new UnsupportedOperationException("Table header deleting is not supported");
	}

	@Override
	public boolean hasColumn(int columnIndex) {
		return getColumnNamesAndIndexesMap().values().stream().anyMatch(i -> i.getLeft() == columnIndex);
	}

	public boolean hasColumnName(String columnName) {
		return getColumnsNames().contains(columnName);
	}

	public int getColumnIndex(String columnName) {
		assertThat(hasColumnName(columnName)).as("There is no column name \"%s\" in the table's header", columnName).isTrue();
		return getColumnNamesAndIndexesMap().entrySet().stream().filter(c -> c.getKey().equals(columnName)).findFirst().get().getValue().getLeft();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column with %s index in table's header", columnIndex).isTrue();
		return getColumnNamesAndIndexesMap().entrySet().stream().filter(c -> c.getValue().getLeft().equals(columnIndex)).findFirst().get().getKey();
	}

	public void excludeColumns(String... columnNames) {
		getTable().excludeColumns(columnNames);
	}

	int getColumnIndex(int sheetColumnIndex) {
		return getColumnNamesAndIndexesMap().values().stream().filter(i -> i.getRight() == sheetColumnIndex).findFirst()
				.orElseThrow(() -> new IstfException("There is no column index in table with column index on sheet: " + sheetColumnIndex)).getLeft();
	}

	void excludeColumn(int columnIndex) {
		this.columnNamesAndIndexes.remove(getColumnName(columnIndex));
	}

	private Map<String, Pair<Integer, Integer>> getColumnNamesAndIndexesMap() {
		if (this.columnNamesAndIndexes == null) {
			this.columnNamesAndIndexes = new HashMap<>();
			int tableColumnIndex = 1;
			for (int sheetColumnIndex : this.columnsIndexes) {
				String headerColumnName = getPoiRow().getCell(sheetColumnIndex - 1).getStringCellValue();
				this.columnNamesAndIndexes.putIfAbsent(headerColumnName, Pair.of(tableColumnIndex, sheetColumnIndex));
				tableColumnIndex++;
			}
		}
		return this.columnNamesAndIndexes;
	}
}
