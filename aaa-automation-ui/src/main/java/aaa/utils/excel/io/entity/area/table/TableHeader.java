package aaa.utils.excel.io.entity.area.table;

import static toolkit.verification.CustomAssertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.ws.rs.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import aaa.utils.excel.io.celltype.CellType;
import aaa.utils.excel.io.entity.area.ExcelCell;

public class TableHeader extends TableRow {
	public TableHeader(Row headerRow, List<Integer> columnsIndexesOnSheet, ExcelTable table) {
		super(headerRow, 0, headerRow.getRowNum() + 1, columnsIndexesOnSheet, table, Collections.singletonList(ExcelCell.STRING_TYPE));
	}

	public List<String> getColumnsNames() {
		return getStringValues();
	}

	/*@Override
	protected ImmutableSortedMap<Integer, TableCell> gatherQueueIndexesAndCellsMap(List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes) {
		ImmutableSortedMap.Builder<Integer, TableCell> queueIndexesAndCellsBuilder = ImmutableSortedMap.naturalOrder();
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndexOnSheet - 1) : null;
			HeaderCell headerCell = new HeaderCell(poiCell, columnIndexInTable, columnIndexOnSheet, this);
			queueIndexesAndCellsBuilder.put(columnIndexInTable, headerCell);
			columnIndexInTable++;
		}
		return queueIndexesAndCellsBuilder.build();
	}*/

	@Override
	protected List<TableCell> gatherCells(List<Integer> columnsIndexesOnSheet, List<CellType<?>> cellTypes) {
		List<TableCell> tableHeaderCells = new ArrayList<>(columnsIndexesOnSheet.size());
		int columnIndexInTable = 1;
		for (Integer columnIndexOnSheet : columnsIndexesOnSheet) {
			Cell poiCell = getPoiRow() != null ? getPoiRow().getCell(columnIndexOnSheet - 1) : null;
			HeaderCell headerCell = new HeaderCell(poiCell, columnIndexInTable, columnIndexOnSheet, this);
			tableHeaderCells.add(headerCell);
			columnIndexInTable++;
		}
		return tableHeaderCells;
	}

	@Override
	public String toString() {
		return "TableHeader{" +
				"sheetName=" + getSheetName() +
				", headerColumns=" + getColumnsNames() +
				", columnsNumber=" + getCellsNumber() +
				'}';
	}

	@Override
	public boolean hasColumn(String headerColumnName) {
		return hasColumn(headerColumnName, false);
	}

	@Override
	public boolean hasColumn(String headerColumnName, boolean ignoreCase) {
		return getColumnsNames().stream().anyMatch(cn -> ignoreCase ? cn.equalsIgnoreCase(headerColumnName) : cn.equals(headerColumnName));
	}

	@Override
	public TableCell getCell(String headerColumnName) {
		return getCell(headerColumnName, false);
	}

	@Override
	public TableCell getCell(String headerColumnName, boolean ignoreCase) {
		assertThat(hasColumn(headerColumnName, ignoreCase)).as("There is no column name \"%1$s\" in the table %2$s", headerColumnName, getTable()).isTrue();
		return getCells().stream().filter(c -> c.hasStringValue(headerColumnName, ignoreCase)).findFirst().get();
	}

	@Override
	public String getColumnName(int columnIndex) {
		assertThat(hasCell(columnIndex)).as("There is no column with %s index in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getStringValue();
	}

	@Override
	public ExcelTable exclude() {
		throw new NotSupportedException("Excluding of table header's row is not supported");
	}

	@Override
	public TableHeader clear() {
		throw new NotSupportedException("Clearing of table header's row is not supported");
	}

	@Override
	public ExcelTable delete() {
		throw new NotSupportedException("Deletion of table header's row is not supported");
	}

	@Override
	public TableHeader registerCellType(CellType<?>... cellTypes) {
		if (Arrays.stream(cellTypes).anyMatch(t -> !ExcelCell.STRING_TYPE.equals(t))) {
			throw new NotSupportedException("Table header row does not support non string cell types");
		}
		return (TableHeader) super.registerCellType(cellTypes);
	}

	@Override
	protected void removeCellsIndexes(Integer... cellsIndexesInQueue) {
		super.removeCellsIndexes(cellsIndexesInQueue);
	}

	public boolean hasColumn(int columnIndex) {
		return hasCell(columnIndex);
	}

	public int getColumnIndex(String headerColumnName) {
		return getColumnIndex(headerColumnName, false);
	}

	public int getColumnIndex(String headerColumnName, boolean ignoreCase) {
		return getCell(headerColumnName, ignoreCase).getColumnIndex();
	}

	public int getColumnIndexOnSheet(String headerColumnName) {
		return getColumnIndexOnSheet(headerColumnName, false);
	}

	public int getColumnIndexOnSheet(String headerColumnName, boolean ignoreCase) {
		return getCell(headerColumnName, ignoreCase).getColumnIndexOnSheet();
	}

	public int getColumnIndexOnSheet(int columnIndex) {
		assertThat(hasColumn(columnIndex)).as("There is no column index %s in the table %2$s", columnIndex, getTable()).isTrue();
		return getCell(columnIndex).getColumnIndexOnSheet();
	}
}
