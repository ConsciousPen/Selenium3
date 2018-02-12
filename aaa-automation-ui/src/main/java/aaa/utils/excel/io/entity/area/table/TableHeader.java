package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;
import aaa.utils.excel.io.entity.area.ExcelRow;

public class TableHeader extends ExcelRow<TableCell> {
	public TableHeader(Row headerRow, Set<Integer> columnsIndexesOnSheet, ExcelTable table) {
		super(headerRow, 0, headerRow.getRowNum() + 1, columnsIndexesOnSheet, table, Collections.singleton(ExcelCell.STRING_TYPE));
	}

	public ExcelTable getTable() {
		return (ExcelTable) getArea();
	}

	public List<String> getColumnsNames() {
		return getStringValues();
	}

	@Override
	public int getIndexOnSheet() {
		return super.getIndexOnSheet();
	}

	@Override
	protected Map<Integer, TableCell> gatherQueueIndexesAndCellsMap(Set<Integer> columnsIndexesOnSheet, Set<CellType<?>> cellTypes) {
		Map<Integer, TableCell> columnsIndexesAndCellsMap = new LinkedHashMap<>(columnsIndexesOnSheet.size());
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndexOnSheet - 1) : null;
			TableRow headerRow = new TableRow(getPoiRow(), getIndex(), getIndexOnSheet(), new HashSet<>(getCellsIndexesOnSheet()), getTable(), getCellTypes());
			TableCell tableCell = new TableCell(poiCell, columnIndexInTable, columnIndexOnSheet, headerRow, getCellTypes());
			columnsIndexesAndCellsMap.put(columnIndexInTable, tableCell);
			columnIndexInTable++;
		}
		return columnsIndexesAndCellsMap;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"headerColumns=" + getColumnsNames() +
				'}';
	}

	public boolean hasColumn(String headerColumnName) {
		return getColumnsNames().contains(headerColumnName);
	}

	public boolean hasColumn(int columnIndex) {
		return hasCell(columnIndex);
	}

	public TableCell getCell(String headerColumnName) {
		assertThat(hasColumn(headerColumnName)).as("There is no column name \"%1$s\" in the table %2$s", headerColumnName, getTable()).isTrue();
		return getCells().stream().filter(c -> c.hasValue(headerColumnName, ExcelCell.STRING_TYPE)).findFirst().get();
	}

	public int getColumnIndex(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndex();
	}

	public int getColumnIndexOnSheet(String headerColumnName) {
		return getCell(headerColumnName).getColumnIndexOnSheet();
	}

	public int getColumnIndexOnSheet(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column index %s in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getColumnIndexOnSheet();
	}

	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getStringValue();
	}

	@Override
	protected void removeCellsIndexes(Integer... cellsIndexesInQueue) {
		super.removeCellsIndexes(cellsIndexesInQueue);
	}
}
